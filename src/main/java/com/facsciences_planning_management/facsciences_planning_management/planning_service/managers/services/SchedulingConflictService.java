package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Course;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Room;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.CourseSchedulingRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.ExamSchedulingRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.CourseTimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.ExamTimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.exception.SchedulingConflictException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulingConflictService {

    private final CourseSchedulingRepository courseSchedulingRepository;
    private final ExamSchedulingRepository examSchedulingRepository;

    public void validateScheduling(Users user, Room room, String date, LocalTime startTime, LocalTime endTime) {
        if (isRoomOccupied(room, date, startTime, endTime)) {
            throw new SchedulingConflictException("Room is occupied");
        }
        if (isTeacherOccupied(user, date, startTime, endTime)) {
            throw new SchedulingConflictException("Teacher is occupied");
        }
    }

    public void validateCourseScheduling(Users user, Course course, Room room, String date, LocalTime startTime,
            LocalTime endTime) {
        if (isRoomOccupied(room, date, startTime, endTime)) {
            throw new SchedulingConflictException("Room is occupied");
        }
        if (isTeacherOccupied(user, date, startTime, endTime)) {
            throw new SchedulingConflictException("Teacher is occupied");
        }
        if (!onlyOneCourseIsScheduledPerWeek(course)) {
            throw new SchedulingConflictException("Only one course is scheduled per week");
        }
    }

    private boolean isRoomOccupied(Room room, String date, LocalTime startTime, LocalTime endTime) {
        CourseTimeSlot courseTimeSlot = CourseTimeSlot.fromTimeSlot(startTime, endTime);
        ExamTimeSlot examTimeSlot = ExamTimeSlot.fromTimeSlot(startTime, endTime);
        boolean isOccupied = false;
        if (courseTimeSlot != null) {
            isOccupied = courseSchedulingRepository.existsByRoomIdAndDayAndTimeSlot(room.getId(),
                    DayOfWeek.valueOf(date), courseTimeSlot);
        } else if (examTimeSlot != null) {
            isOccupied = examSchedulingRepository.existsByRoomIdAndSessionDateAndTimeSlot(room.getId(),
                    LocalDate.parse(date),
                    examTimeSlot);
        }
        return isOccupied;
    }

    private boolean isTeacherOccupied(Users user, String date, LocalTime startTime, LocalTime endTime) {
        CourseTimeSlot courseTimeSlot = CourseTimeSlot.fromTimeSlot(startTime, endTime);
        ExamTimeSlot examTimeSlot = ExamTimeSlot.fromTimeSlot(startTime, endTime);
        boolean isOccupied = false;
        if (courseTimeSlot != null) {
            isOccupied = courseSchedulingRepository.existsByAssignedCourseTeacherIdAndDayAndTimeSlot(user.getId(),
                    DayOfWeek.valueOf(date),
                    courseTimeSlot);
        } else if (examTimeSlot != null) {
            isOccupied = examSchedulingRepository.existsByProctorIdAndSessionDateAndTimeSlot(user.getId(),
                    LocalDate.parse(date),
                    examTimeSlot);
        }
        return isOccupied;
    }

    private boolean onlyOneCourseIsScheduledPerWeek(Course course) {
        Integer counter = 0;
        for (DayOfWeek day : DayOfWeek.values()) {
            if (courseSchedulingRepository.existsByAssignedCourseIdAndDay(course.getId(), day))
                counter++;
        }
        return counter == 1;
    }
}
