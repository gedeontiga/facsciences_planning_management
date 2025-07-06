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
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.CourseTimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseSchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseSchedulingRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimeSlotDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.SchedulingService;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("courseSchedulingService")
@RequiredArgsConstructor
public class CourseSchedulingServiceImpl implements SchedulingService<CourseSchedulingDTO, CourseSchedulingRequest> {

	private final CourseSchedulingRepository schedulingRepository;
	private final TimetableRepository timetableRepository;
	private final RoomRepository roomRepository;
	private final CourseRepository courseRepository;
	private final WebSocketUpdateService webSocketUpdateService;
	private final SchedulingConflictService conflictService;

	private static final String TIMETABLE_TOPIC_DESTINATION = "/topic/timetable/";

	@Override
	@Transactional
	public CourseSchedulingDTO createScheduling(CourseSchedulingRequest request) {
		return createCourseScheduling(request).toDTO();
	}

	public CourseScheduling createCourseScheduling(CourseSchedulingRequest request) {
		// 1. Validate for conflicts before creating

		Timetable timetable = timetableRepository.findById(request.timetableId())
				.orElseThrow(() -> new CustomBusinessException("Timetable not found: " + request.timetableId()));
		Room room = roomRepository.findById(request.roomId())
				.orElseThrow(() -> new CustomBusinessException("Room not found: " + request.roomId()));
		// Assuming CourseRepository has a method to find an active course by UE id
		Course assignedCourse = courseRepository.findByUeIdAndObsoleteFalse(request.ueId())
				.orElseThrow(() -> new CustomBusinessException("Active course for UE not found: " + request.ueId()));

		if (request.headCount() != null && request.headCount() > room.getCapacity()) {
			throw new CustomBusinessException("Level headcount exceeds room capacity: "
					+ request.headCount() + " > " + room.getCapacity());
		}
		conflictService.validateScheduling(assignedCourse.getTeacher(), room,
				request.day(),
				CourseTimeSlot.valueOf(request.timeSlotLabel()).getStartTime(),
				CourseTimeSlot.valueOf(request.timeSlotLabel()).getEndTime());

		CourseScheduling scheduling = CourseScheduling.builder()
				.timetable(timetable)
				.room(room)
				.timeSlot(TimeSlot.CourseTimeSlot.valueOf(request.timeSlotLabel()))
				.day(DayOfWeek.valueOf(request.day()))
				.assignedCourse(assignedCourse)
				.headCount(request.headCount())
				.build();
		log.info("Saving scheduling: {}", request);

		CourseScheduling savedScheduling = schedulingRepository.save(scheduling);
		timetable.getSchedules().add(savedScheduling);
		room.setAvailability(false);
		roomRepository.save(room);
		timetableRepository.save(timetable);
		CourseSchedulingDTO responseDTO = savedScheduling.toDTO();

		// 2. Send real-time update
		webSocketUpdateService.sendUpdate(TIMETABLE_TOPIC_DESTINATION + "create/" + responseDTO.timetableId(),
				responseDTO);
		return savedScheduling;
	}

	@Override
	@Transactional
	public CourseSchedulingDTO updateScheduling(String id, CourseSchedulingRequest request) {
		// 1. Validate for conflicts, excluding the current schedule from the check

		CourseScheduling scheduling = schedulingRepository.findById(id)
				.orElseThrow(() -> new CustomBusinessException("Scheduling not found: " + id));

		// Update properties if they are provided in the request
		Room room = roomRepository.findById(request.roomId())
				.orElseThrow(() -> new CustomBusinessException("Room not found: " + request.roomId()));
		Course course = courseRepository.findByUeIdAndObsoleteFalse(request.ueId()).orElseThrow(
				() -> new CustomBusinessException("Active course for UE not found: " + request.ueId()));

		if (request.headCount() != null && request.headCount() > room.getCapacity()) {
			throw new CustomBusinessException("Level headcount exceeds room capacity: "
					+ request.headCount() + " > " + room.getCapacity());
		}

		if (request.roomId() != null && !request.roomId().equals(scheduling.getRoom().getId())) {
			scheduling.setRoom(room);
		}
		if (request.day() != null) {
			scheduling.setDay(DayOfWeek.valueOf(request.day()));
		}
		if (request.timeSlotLabel() != null) {
			scheduling.setTimeSlot(TimeSlot.CourseTimeSlot.valueOf(request.timeSlotLabel()));
		}
		if (request.headCount() != null) {
			scheduling.setHeadCount(request.headCount());
		}
		conflictService.validateScheduling(course.getTeacher(), room,
				request.day(),
				CourseTimeSlot.valueOf(request.timeSlotLabel()).getStartTime(),
				CourseTimeSlot.valueOf(request.timeSlotLabel()).getEndTime());

		// Note: Changing the assigned course might be a complex operation, handled here
		// if needed.

		CourseScheduling updatedScheduling = schedulingRepository.save(scheduling);
		CourseSchedulingDTO responseDTO = updatedScheduling.toDTO();

		// 2. Send real-time update
		webSocketUpdateService.sendUpdate(TIMETABLE_TOPIC_DESTINATION + "update/" + responseDTO.timetableId(),
				responseDTO);

		return responseDTO;
	}

	@Override
	@Transactional
	public void deleteScheduling(String id) {
		// 1. Fetch scheduling to get details for the websocket message before deleting
		CourseScheduling scheduling = schedulingRepository.findById(id)
				.orElseThrow(() -> new CustomBusinessException("Scheduling not found: " + id));

		CourseSchedulingDTO deletedDTO = scheduling.toDTO();
		timetableRepository.findById(deletedDTO.timetableId())
				.ifPresent(timetable -> {
					timetable.getSchedules().remove(scheduling);
					timetableRepository.save(timetable);
				});
		schedulingRepository.deleteById(id);

		// 2. Send a notification of the deletion.
		// The client can use this DTO to identify which schedule to remove from the UI.
		webSocketUpdateService.sendUpdate(TIMETABLE_TOPIC_DESTINATION + "delete/" + deletedDTO.timetableId(),
				Map.of("schedulingId", deletedDTO.id()));
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

	public CourseScheduling toEntity(CourseSchedulingDTO dto) {
		return CourseScheduling.builder()
				.id(dto.id())
				.room(roomRepository.findById(dto.roomId())
						.orElseThrow(() -> new CustomBusinessException("Room not found: " + dto.roomId())))
				.timeSlot(TimeSlot.CourseTimeSlot.valueOf(dto.timeSlotLabel()))
				.day(DayOfWeek.valueOf(dto.day()))
				.assignedCourse(courseRepository.findById(dto.ueId())
						.orElseThrow(() -> new CustomBusinessException("Course not found: " + dto.ueId())))
				.headCount(dto.headCount())
				.build();
	}
}