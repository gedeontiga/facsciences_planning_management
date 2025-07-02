package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facsciences_planning_management.facsciences_planning_management.exceptions.CustomBusinessException;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Course;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Room;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Timetable;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.CourseRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.CourseSchedulingRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.RoomRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.TimetableRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseSchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimeSlotDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.SchedulingService;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("courseSchedulingService")
@RequiredArgsConstructor
public class CourseSchedulingServiceImpl implements SchedulingService<CourseSchedulingDTO> {

	private final CourseSchedulingRepository schedulingRepository;
	private final TimetableRepository timetableRepository;
	private final RoomRepository roomRepository;
	private final CourseRepository courseRepository;
	private final WebSocketUpdateService webSocketUpdateService;
	private final SchedulingConflictService conflictService;

	private static final String TIMETABLE_TOPIC_DESTINATION = "/topic/timetable/";

	@Override
	@Transactional
	public CourseSchedulingDTO createScheduling(CourseSchedulingDTO request) {
		// 1. Validate for conflicts before creating

		Timetable timetable = timetableRepository.findById(request.timetableId())
				.orElseThrow(() -> new CustomBusinessException("Timetable not found: " + request.timetableId()));
		Room room = roomRepository.findById(request.roomId())
				.orElseThrow(() -> new CustomBusinessException("Room not found: " + request.roomId()));
		// Assuming CourseRepository has a method to find an active course by UE id
		Course assignedCourse = courseRepository.findByUeIdAndObsoleteFalse(request.ueId())
				.orElseThrow(() -> new CustomBusinessException("Active course for UE not found: " + request.ueId()));

		// log.info("Creating scheduling: {}", request.day());
		conflictService.validateCourseScheduling(assignedCourse.getTeacher(), assignedCourse, room,
				request.day(),
				LocalTime.parse(request.startTime()), LocalTime.parse(request.endTime()));

		CourseScheduling scheduling = CourseScheduling.builder()
				.timetable(timetable)
				.room(room)
				.timeSlot(TimeSlot.CourseTimeSlot.valueOf(request.timeSlotLabel()))
				.day(DayOfWeek.valueOf(request.day()))
				.assignedCourse(assignedCourse)
				.build();

		CourseScheduling savedScheduling = schedulingRepository.save(scheduling);
		room.setAvailability(false);
		roomRepository.save(room);
		CourseSchedulingDTO responseDTO = savedScheduling.toDTO();

		// 2. Send real-time update
		webSocketUpdateService.sendUpdate(TIMETABLE_TOPIC_DESTINATION + responseDTO.timetableId(), responseDTO);

		return responseDTO;
	}

	@Override
	@Transactional
	public CourseSchedulingDTO updateScheduling(String id, CourseSchedulingDTO request) {
		// 1. Validate for conflicts, excluding the current schedule from the check

		CourseScheduling scheduling = schedulingRepository.findById(id)
				.orElseThrow(() -> new CustomBusinessException("Scheduling not found: " + id));

		// Update properties if they are provided in the request
		Room room = roomRepository.findById(request.roomId())
				.orElseThrow(() -> new CustomBusinessException("Room not found: " + request.roomId()));
		Course course = courseRepository.findByUeIdAndObsoleteFalse(request.ueId()).orElseThrow(
				() -> new CustomBusinessException("Active course for UE not found: " + request.ueId()));
		if (request.roomId() != null && !request.roomId().equals(scheduling.getRoom().getId())) {
			scheduling.setRoom(room);
		}
		if (request.day() != null) {
			scheduling.setDay(DayOfWeek.valueOf(request.day()));
		}
		if (request.timeSlotLabel() != null) {
			scheduling.setTimeSlot(TimeSlot.CourseTimeSlot.valueOf(request.timeSlotLabel()));
		}
		conflictService.validateCourseScheduling(course.getTeacher(),
				course, room,
				request.day(),
				LocalTime.parse(request.startTime()), LocalTime.parse(request.endTime()));

		// Note: Changing the assigned course might be a complex operation, handled here
		// if needed.

		CourseScheduling updatedScheduling = schedulingRepository.save(scheduling);
		room.setAvailability(false);
		roomRepository.save(room);
		CourseSchedulingDTO responseDTO = updatedScheduling.toDTO();

		// 2. Send real-time update
		webSocketUpdateService.sendUpdate(TIMETABLE_TOPIC_DESTINATION + responseDTO.timetableId(), responseDTO);

		return responseDTO;
	}

	@Override
	@Transactional
	public void deleteScheduling(String id) {
		// 1. Fetch scheduling to get details for the websocket message before deleting
		CourseScheduling scheduling = schedulingRepository.findById(id)
				.orElseThrow(() -> new CustomBusinessException("Scheduling not found: " + id));

		CourseSchedulingDTO deletedDTO = scheduling.toDTO();

		schedulingRepository.deleteById(id);

		// 2. Send a notification of the deletion.
		// The client can use this DTO to identify which schedule to remove from the UI.
		webSocketUpdateService.sendUpdate(TIMETABLE_TOPIC_DESTINATION + deletedDTO.timetableId(),
				Map.of("deletedId", deletedDTO.id()));
	}

	@Override
	public CourseSchedulingDTO getScheduling(String schedulingId) {
		return schedulingRepository.findById(schedulingId)
				.orElseThrow(() -> new CustomBusinessException("Scheduling not found: "))
				.toDTO();
	}

	@Override
	public List<TimeSlotDTO> getTimeSlots() {
		return Arrays.stream(TimeSlot.CourseTimeSlot.values())
				.map(TimeSlotDTO::fromTimeSlot)
				.collect(Collectors.toList());
	}
}