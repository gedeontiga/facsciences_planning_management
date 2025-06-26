package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("courseSchedulingService")
@RequiredArgsConstructor
public class CourseSchedulingServiceImpl implements SchedulingService<CourseSchedulingDTO> {

        private final CourseSchedulingRepository schedulingRepository;
        private final TimetableRepository timetableRepository;
        private final RoomRepository roomRepository;
        private final CourseRepository courseRepository;

        @Override
        public CourseSchedulingDTO createScheduling(CourseSchedulingDTO request) {
                Timetable timetable = timetableRepository.findById(request.timetableId())
                                .orElseThrow(() -> new CustomBusinessException(
                                                "Timetable not found: " + request.timetableId()));
                Room room = roomRepository.findById(request.roomId())
                                .orElseThrow(() -> new CustomBusinessException("Room not found: " + request.roomId()));
                Course assignedCourse = courseRepository.findByObsoleteFalseAndUeId(request.ueId())
                                .orElseThrow(() -> new CustomBusinessException(
                                                "Course for UE not found: " + request.ueId()));

                CourseScheduling scheduling = CourseScheduling.builder()
                                .timetable(timetable)
                                .room(room)
                                .timeSlot(TimeSlot.CourseTimeSlot.valueOf(request.timeSlotLabel()))
                                .day(DayOfWeek.valueOf(request.day()))
                                .assignedCourse(assignedCourse)
                                .build();

                return schedulingRepository.save(scheduling).toDTO();
        }

        @Override
        public List<CourseSchedulingDTO> getSchedulesByRoom(String roomId) {
                return schedulingRepository.findByRoomIdAndTimetableUsedTrue(roomId).stream()
                                .map(CourseScheduling::toDTO)
                                .collect(Collectors.toList());
        }

        @Override
        public List<CourseSchedulingDTO> getSchedulesByTeacherOrProctor(String teacherId) {
                return schedulingRepository.findByAssignedCourseTeacherIdAndAssignedCourseObsoleteFalse(teacherId)
                                .stream()
                                .map(CourseScheduling::toDTO)
                                .collect(Collectors.toList());
        }

        @Override
        public List<CourseSchedulingDTO> getSchedulesByLevel(String levelId) {
                return schedulingRepository.findByAssignedCourseObsoleteFalseAndAssignedCourseUeLevelId(levelId)
                                .stream()
                                .map(CourseScheduling::toDTO)
                                .collect(Collectors.toList());
        }

        @Override
        public Page<CourseSchedulingDTO> getScheduleByBranch(String branchId, Pageable page) {
                return schedulingRepository
                                .findByAssignedCourseObsoleteFalseAndAssignedCourseUeLevelBranchId(branchId, page)
                                .map(CourseScheduling::toDTO);
        }

        @Override
        public List<CourseSchedulingDTO> getSchedulesByTimetable(String timetableId) {
                return schedulingRepository.findByTimetableId(timetableId).stream()
                                .map(CourseScheduling::toDTO)
                                .collect(Collectors.toList());
        }

        @Override
        public List<CourseSchedulingDTO> getSchedulesByTimeSlot(String timeSlot) {
                return schedulingRepository
                                .findByTimetableUsedTrueAndTimeSlot(TimeSlot.CourseTimeSlot.valueOf(timeSlot))
                                .stream()
                                .map(CourseScheduling::toDTO)
                                .collect(Collectors.toList());
        }

        @Override
        public void deleteScheduling(String id) {
                if (!schedulingRepository.existsById(id)) {
                        throw new CustomBusinessException("Scheduling not found: " + id);
                }
                schedulingRepository.deleteById(id);
        }

        @Override
        public CourseSchedulingDTO updateScheduling(String id, CourseSchedulingDTO request) {
                CourseScheduling scheduling = schedulingRepository.findById(id)
                                .orElseThrow(() -> new CustomBusinessException("Scheduling not found: " + id));

                Room room = roomRepository.findById(request.roomId())
                                .orElseThrow(() -> new CustomBusinessException("Room not found: " + request.roomId()));

                scheduling.setRoom(room);
                scheduling.setDay(DayOfWeek.valueOf(request.day()));
                scheduling.setTimeSlot(TimeSlot.CourseTimeSlot.valueOf(request.timeSlotLabel()));

                return schedulingRepository.save(scheduling).toDTO();
        }

        @Override
        public List<TimeSlotDTO> getTimeSlots() {
                return Arrays.stream(TimeSlot.CourseTimeSlot.values())
                                .map(TimeSlotDTO::fromTimeSlot)
                                .collect(Collectors.toList());
        }
}