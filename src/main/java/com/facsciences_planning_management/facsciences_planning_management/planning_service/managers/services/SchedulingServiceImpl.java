package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.entities.repositories.UserRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.ExamScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Planning;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Room;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.SimpleScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Ue;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.ExamSchedulingRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.RoomRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.SimpleSchedulingRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ExamSchedulingCreateRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ExamSchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ExamSchedulingUpdateRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SchedulingConflictCheckRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SimpleSchedulingCreateRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SimpleSchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SimpleSchedulingUpdateRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.exceptions.ResourceConflictException;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.exceptions.ResourceNotFoundException;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.PlanningService;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.RoomService;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.SchedulingService;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.UeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchedulingServiceImpl implements SchedulingService {
    private final SimpleSchedulingRepository simpleSchedulingRepository;
    private final ExamSchedulingRepository examSchedulingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final UeService ueService;
    private final RoomService roomService;
    private final PlanningService planningService;

    @Override
    public SimpleSchedulingDTO createSimpleScheduling(SimpleSchedulingCreateRequest request) {
        // Validate resources availability
        if (!roomService.isRoomAvailable(request.roomId(), request.startTime(), request.endTime(), request.day())) {
            throw new ResourceConflictException("Room is not available at the requested time");
        }

        // Get required entities
        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        Ue ue = ueService.getUeEntityById(request.ueId());
        Planning planning = planningService.getPlanningEntityById(request.planningId());
        Users teacher = userRepository.findById(request.teacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        // Create and save scheduling
        SimpleScheduling scheduling = SimpleScheduling.builder()
                .room(room)
                .ue(ue)
                .planning(planning)
                .teacher(teacher)
                .startTime(request.startTime())
                .endTime(request.endTime())
                .sessionType(request.sessionType())
                .day(request.day())
                .build();

        return simpleSchedulingRepository.save(scheduling).toDTO();
    }

    @Override
    public ExamSchedulingDTO createExamScheduling(ExamSchedulingCreateRequest request) {
        // Validate resources availability
        if (!roomService.isRoomAvailableForDate(request.roomId(), request.startTime(), request.endTime(),
                request.sessionDate())) {
            throw new ResourceConflictException("Room is not available at the requested time");
        }

        // Get required entities
        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        Ue ue = ueService.getUeEntityById(request.ueId());
        Planning planning = planningService.getPlanningEntityById(request.planningId());
        Users proctor = userRepository.findById(request.proctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Proctor not found"));

        // Create and save scheduling
        ExamScheduling scheduling = ExamScheduling.builder()
                .room(room)
                .ue(ue)
                .planning(planning)
                .proctor(proctor)
                .startTime(request.startTime())
                .endTime(request.endTime())
                .sessionType(request.sessionType())
                .sessionDate(request.sessionDate())
                .build();

        return examSchedulingRepository.save(scheduling).toDTO();
    }

    @Override
    public List<SchedulingDTO> getSchedulesByRoom(String roomId) {
        List<SimpleScheduling> simpleSchedulings = simpleSchedulingRepository.findByRoomId(roomId);
        List<ExamScheduling> examSchedulings = examSchedulingRepository.findByRoomId(roomId);

        List<SchedulingDTO> schedulingDTOs = new ArrayList<>();
        schedulingDTOs.addAll(simpleSchedulings.stream().map(SimpleScheduling::toDTO).toList());
        schedulingDTOs.addAll(examSchedulings.stream().map(ExamScheduling::toDTO).toList());

        return schedulingDTOs;
    }

    @Override
    public List<SchedulingDTO> getSchedulesForTeacher(String teacherId) {
        List<SimpleScheduling> simpleSchedulings = simpleSchedulingRepository.findByTeacherId(teacherId);
        List<ExamScheduling> examSchedulings = examSchedulingRepository.findByProctorId(teacherId);

        List<SchedulingDTO> schedulingDTOs = new ArrayList<>();
        schedulingDTOs.addAll(simpleSchedulings.stream().map(SimpleScheduling::toDTO).toList());
        schedulingDTOs.addAll(examSchedulings.stream().map(ExamScheduling::toDTO).toList());

        return schedulingDTOs;
    }

    @Override
    public List<SchedulingDTO> getSchedulesForLevel(String levelId) {
        List<Ue> levelUes = ueService.getUesByLevelId(levelId);
        List<String> ueIds = levelUes.stream().map(Ue::getId).toList();

        List<SimpleScheduling> simpleSchedulings = simpleSchedulingRepository.findByUeIdIn(ueIds);
        List<ExamScheduling> examSchedulings = examSchedulingRepository.findByUeIdIn(ueIds);

        List<SchedulingDTO> schedulingDTOs = new ArrayList<>();
        schedulingDTOs.addAll(simpleSchedulings.stream().map(SimpleScheduling::toDTO).toList());
        schedulingDTOs.addAll(examSchedulings.stream().map(ExamScheduling::toDTO).toList());

        return schedulingDTOs;
    }

    @Override
    public void deleteScheduling(String id, SessionType type) {
        switch (type) {
            case COURSE -> simpleSchedulingRepository.deleteById(id);
            case TUTORIAL -> simpleSchedulingRepository.deleteById(id);
            case LECTURE -> examSchedulingRepository.deleteById(id);
            case NORMAL_SESSION -> examSchedulingRepository.deleteById(id);
            case CONTINUOUS_ASSESSMENT -> examSchedulingRepository.deleteById(id);
            default -> throw new IllegalArgumentException("Unknown scheduling type: " + type);
        }
    }

    @Override
    public SimpleSchedulingDTO updateSimpleScheduling(String id, SimpleSchedulingUpdateRequest request) {
        SimpleScheduling scheduling = simpleSchedulingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Simple scheduling not found with id: " + id));

        // Verify room availability if changed
        boolean roomChanged = request.roomId() != null && !request.roomId().equals(scheduling.getRoom().getId());
        boolean timeChanged = (request.startTime() != null && !request.startTime().equals(scheduling.getStartTime())) ||
                (request.endTime() != null && !request.endTime().equals(scheduling.getEndTime()));
        boolean dayChanged = request.day() != null && request.day() != scheduling.getDay();

        if ((roomChanged || timeChanged || dayChanged) &&
                !roomService.isRoomAvailable(
                        request.roomId() != null ? request.roomId() : scheduling.getRoom().getId(),
                        request.startTime() != null ? request.startTime() : scheduling.getStartTime(),
                        request.endTime() != null ? request.endTime() : scheduling.getEndTime(),
                        request.day() != null ? request.day() : scheduling.getDay())) {

            throw new ResourceConflictException("Room is not available at the requested time");
        }

        Optional.of(request.roomId()).ifPresent(roomId -> scheduling.setRoom(roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"))));

        Optional.of(request.ueId()).ifPresent(ueId -> scheduling.setUe(ueService.getUeEntityById(ueId)));
        Optional.of(request.teacherId()).ifPresent(teacherId -> scheduling.setTeacher(userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"))));

        if (request.startTime() != null) {
            scheduling.setStartTime(request.startTime());
        }

        if (request.endTime() != null) {
            scheduling.setEndTime(request.endTime());
        }

        if (request.day() != null) {
            scheduling.setDay(request.day());
        }

        if (request.sessionType() != null) {
            scheduling.setSessionType(request.sessionType());
        }

        return simpleSchedulingRepository.save(scheduling).toDTO();
    }

    @Override
    public ExamSchedulingDTO updateExamScheduling(String id, ExamSchedulingUpdateRequest request) {
        ExamScheduling scheduling = examSchedulingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam scheduling not found with id: " + id));

        // Verify room availability if changed
        boolean roomChanged = request.roomId() != null && !request.roomId().equals(scheduling.getRoom().getId());
        boolean timeChanged = (request.startTime() != null && !request.startTime().equals(scheduling.getStartTime())) ||
                (request.endTime() != null && !request.endTime().equals(scheduling.getEndTime()));
        boolean dateChanged = request.sessionDate() != null
                && !request.sessionDate().equals(scheduling.getSessionDate());

        if ((roomChanged || timeChanged || dateChanged) &&
                !roomService.isRoomAvailableForDate(
                        request.roomId() != null ? request.roomId() : scheduling.getRoom().getId(),
                        request.startTime() != null ? request.startTime() : scheduling.getStartTime(),
                        request.endTime() != null ? request.endTime() : scheduling.getEndTime(),
                        request.sessionDate() != null ? request.sessionDate() : scheduling.getSessionDate())) {

            throw new ResourceConflictException("Room is not available at the requested time");
        }

        // Update fields if provided
        if (request.roomId() != null) {
            scheduling.setRoom(roomRepository.findById(request.roomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found")));
        }

        if (request.ueId() != null) {
            scheduling.setUe(ueService.getUeEntityById(request.ueId()));
        }

        if (request.proctorId() != null) {
            scheduling.setProctor(userRepository.findById(request.proctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proctor not found")));
        }

        if (request.startTime() != null) {
            scheduling.setStartTime(request.startTime());
        }

        if (request.endTime() != null) {
            scheduling.setEndTime(request.endTime());
        }

        if (request.sessionDate() != null) {
            scheduling.setSessionDate(request.sessionDate());
        }

        if (request.sessionType() != null) {
            scheduling.setSessionType(request.sessionType());
        }

        return examSchedulingRepository.save(scheduling).toDTO();
    }

    @Override
    public boolean checkForSchedulingConflicts(SchedulingConflictCheckRequest request) {
        // For simple scheduling conflicts (weekly)
        if (request.day() != null) {
            return !simpleSchedulingRepository.findConflicts(
                    request.roomId(),
                    request.startTime(),
                    request.endTime(),
                    request.day()).isEmpty();
        }
        // For exam scheduling conflicts (specific date)
        else if (request.date() != null) {
            return !examSchedulingRepository.findConflicts(
                    request.roomId(),
                    request.startTime(),
                    request.endTime(),
                    request.date()).isEmpty();
        }

        throw new IllegalArgumentException("Either day or date must be provided");
    }
}
