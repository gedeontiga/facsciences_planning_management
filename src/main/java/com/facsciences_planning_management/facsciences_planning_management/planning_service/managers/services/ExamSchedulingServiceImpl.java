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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("examSchedulingService")
@RequiredArgsConstructor
public class ExamSchedulingServiceImpl implements SchedulingService<ExamSchedulingDTO> {

	private final ExamSchedulingRepository schedulingRepository;
	private final TimetableRepository timetableRepository;
	private final RoomRepository roomRepository;
	private final UeRepository ueRepository;
	private final UserRepository userRepository;

	@Override
	public ExamSchedulingDTO createScheduling(ExamSchedulingDTO request) {
		Timetable timetable = timetableRepository.findById(request.timetableId())
				.orElseThrow(() -> new CustomBusinessException("Timetable not found: " + request.timetableId()));
		Room room = roomRepository.findById(request.roomId())
				.orElseThrow(() -> new CustomBusinessException("Room not found: " + request.roomId()));
		Ue ue = ueRepository.findById(request.ueId())
				.orElseThrow(() -> new CustomBusinessException("UE not found: " + request.ueId()));
		Users proctor = userRepository.findById(request.proctorId())
				.orElseThrow(() -> new CustomBusinessException("Proctor not found: " + request.proctorId()));

		ExamScheduling scheduling = ExamScheduling.builder()
				.timetable(timetable)
				.room(room)
				.timeSlot(TimeSlot.ExamTimeSlot.valueOf(request.timeSlotLabel()))
				.sessionDate(request.sessionDate())
				.ue(ue)
				.proctor(proctor)
				.build();

		return schedulingRepository.save(scheduling).toDTO();
	}

	@Override
	public List<ExamSchedulingDTO> getSchedulesByRoom(String roomId) {
		return schedulingRepository.findByRoomIdAndTimetableUsedTrue(roomId).stream()
				.map(ExamScheduling::toDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<ExamSchedulingDTO> getSchedulesByTeacherOrProctor(String proctorId) {
		return schedulingRepository.findByTimetableUsedTrueAndProctorId(proctorId).stream()
				.map(ExamScheduling::toDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<ExamSchedulingDTO> getSchedulesByLevel(String levelId) {
		return schedulingRepository.findByTimetableUsedTrueAndUe_Level_Id(levelId).stream()
				.map(ExamScheduling::toDTO)
				.collect(Collectors.toList());
	}

	@Override
	public Page<ExamSchedulingDTO> getScheduleByBranch(String branchId, Pageable page) {
		return schedulingRepository.findByTimetableUsedTrueAndUe_Level_Branch_Id(branchId, page)
				.map(ExamScheduling::toDTO);
	}

	@Override
	public List<ExamSchedulingDTO> getSchedulesByTimetable(String timetableId) {
		return schedulingRepository.findByTimetableId(timetableId).stream()
				.map(ExamScheduling::toDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<ExamSchedulingDTO> getSchedulesByTimeSlot(String timeSlot) {
		return schedulingRepository.findByTimetableUsedTrueAndTimeSlot(TimeSlot.ExamTimeSlot.valueOf(timeSlot)).stream()
				.map(ExamScheduling::toDTO)
				.collect(Collectors.toList());
	}

	@Override
	public void deleteScheduling(String id) {
		if (!schedulingRepository.existsById(id)) {
			throw new CustomBusinessException("Exam Scheduling not found: " + id);
		}
		schedulingRepository.deleteById(id);
	}

	@Override
	public ExamSchedulingDTO updateScheduling(String id, ExamSchedulingDTO request) {
		ExamScheduling scheduling = schedulingRepository.findById(id)
				.orElseThrow(() -> new CustomBusinessException("Scheduling not found: " + id));

		Room room = roomRepository.findById(request.roomId())
				.orElseThrow(() -> new CustomBusinessException("Room not found: " + request.roomId()));
		Users proctor = userRepository.findById(request.proctorId())
				.orElseThrow(() -> new CustomBusinessException("Proctor not found: " + request.proctorId()));

		scheduling.setRoom(room);
		scheduling.setProctor(proctor);
		scheduling.setTimeSlot(TimeSlot.ExamTimeSlot.valueOf(request.timeSlotLabel()));
		scheduling.setSessionDate(request.sessionDate());

		return schedulingRepository.save(scheduling).toDTO();
	}

	@Override
	public List<TimeSlotDTO> getTimeSlots() {
		return Arrays.stream(TimeSlot.ExamTimeSlot.values())
				.map(ts -> new TimeSlotDTO(ts.getStartTime(), ts.getEndTime(), ts.getDuration(), ts.name()))
				.collect(Collectors.toList());
	}
}