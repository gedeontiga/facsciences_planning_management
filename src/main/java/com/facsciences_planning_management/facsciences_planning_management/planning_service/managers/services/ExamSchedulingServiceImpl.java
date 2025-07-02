package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.entities.repositories.UserRepository;
import com.facsciences_planning_management.facsciences_planning_management.exceptions.CustomBusinessException;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.ExamScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Room;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Timetable;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Ue;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.ExamSchedulingRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.RoomRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.TimetableRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.UeRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ExamSchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimeSlotDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.SchedulingService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * Fully implemented and secured with conflict validation and real-time updates.
 */
@Service("examSchedulingService")
@RequiredArgsConstructor
public class ExamSchedulingServiceImpl implements SchedulingService<ExamSchedulingDTO> {

	private final ExamSchedulingRepository schedulingRepository;
	private final TimetableRepository timetableRepository;
	private final RoomRepository roomRepository;
	private final UeRepository ueRepository;
	private final UserRepository userRepository;
	private final SchedulingConflictService conflictService;
	private final WebSocketUpdateService webSocketUpdateService;

	private static final String TIMETABLE_TOPIC_DESTINATION = "/topic/timetable/";

	@Override
	@Transactional
	public ExamSchedulingDTO createScheduling(ExamSchedulingDTO request) {
		// 1. Validate for conflicts before creating

		Timetable timetable = timetableRepository.findById(request.timetableId())
				.orElseThrow(() -> new CustomBusinessException("Timetable not found: " + request.timetableId()));
		Room room = roomRepository.findById(request.roomId())
				.orElseThrow(() -> new CustomBusinessException("Room not found: " + request.roomId()));
		Ue ue = ueRepository.findById(request.ueId())
				.orElseThrow(() -> new CustomBusinessException("UE not found: " + request.ueId()));
		Users proctor = userRepository.findById(request.ueId())
				.orElseThrow(() -> new CustomBusinessException("Proctor not found: " + request.userId()));

		conflictService.validateScheduling(proctor, room, request.date(),
				LocalTime.parse(request.startTime()), LocalTime.parse(request.endTime()));

		ExamScheduling scheduling = ExamScheduling.builder()
				.timetable(timetable)
				.room(room)
				.timeSlot(TimeSlot.ExamTimeSlot.valueOf(request.timeSlotLabel()))
				.sessionDate(LocalDate.parse(request.date()))
				.ue(ue)
				.proctor(proctor)
				.build();

		ExamScheduling savedScheduling = schedulingRepository.save(scheduling);
		room.setAvailability(false);
		roomRepository.save(room);
		ExamSchedulingDTO responseDTO = savedScheduling.toDTO();

		// 2. Send real-time update
		webSocketUpdateService.sendUpdate(TIMETABLE_TOPIC_DESTINATION + responseDTO.timetableId(), responseDTO);

		return responseDTO;
	}

	@Override
	@Transactional
	public ExamSchedulingDTO updateScheduling(String id, ExamSchedulingDTO request) {
		// 1. Validate for conflicts, excluding the current schedule from the check

		ExamScheduling scheduling = schedulingRepository.findById(id)
				.orElseThrow(() -> new CustomBusinessException("Scheduling not found: " + id));

		// Update properties if provided in the request
		Room room = roomRepository.findById(request.roomId())
				.orElseThrow(() -> new CustomBusinessException("Room not found: " + request.roomId()));
		if (request.roomId() != null && !request.roomId().equals(scheduling.getRoom().getId())) {
			scheduling.setRoom(room);
		}
		Users proctor = userRepository.findById(request.userId())
				.orElseThrow(() -> new CustomBusinessException("Proctor not found: " + request.userId()));
		if (request.userId() != null && !request.userId().equals(scheduling.getProctor().getId())) {
			scheduling.setProctor(proctor);
		}
		if (request.date() != null) {
			scheduling.setSessionDate(LocalDate.parse(request.date()));
		}
		if (request.timeSlotLabel() != null) {
			scheduling.setTimeSlot(TimeSlot.ExamTimeSlot.valueOf(request.timeSlotLabel()));
		}
		conflictService.validateScheduling(proctor, room, request.date(),
				LocalTime.parse(request.startTime()), LocalTime.parse(request.endTime()));

		ExamScheduling updatedScheduling = schedulingRepository.save(scheduling);
		room.setAvailability(false);
		roomRepository.save(room);
		ExamSchedulingDTO responseDTO = updatedScheduling.toDTO();

		// 2. Send real-time update
		webSocketUpdateService.sendUpdate(TIMETABLE_TOPIC_DESTINATION + responseDTO.timetableId(), responseDTO);

		return responseDTO;
	}

	@Override
	public ExamSchedulingDTO getScheduling(String id) {
		return schedulingRepository.findById(id)
				.orElseThrow(() -> new CustomBusinessException("Scheduling not found: "))
				.toDTO();
	}

	@Override
	@Transactional
	public void deleteScheduling(String id) {
		ExamScheduling scheduling = schedulingRepository.findById(id)
				.orElseThrow(() -> new CustomBusinessException("Scheduling not found: " + id));

		ExamSchedulingDTO deletedDTO = scheduling.toDTO();

		schedulingRepository.deleteById(id);

		webSocketUpdateService.sendUpdate(TIMETABLE_TOPIC_DESTINATION + deletedDTO.timetableId(),
				Map.of("deletedId", deletedDTO.id()));
	}

	@Override
	public List<TimeSlotDTO> getTimeSlots() {
		return Arrays.stream(TimeSlot.ExamTimeSlot.values())
				.map(TimeSlotDTO::fromTimeSlot)
				.collect(Collectors.toList());
	}
}