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

    public void validateScheduling(Users user, Room room, LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (isRoomOccupied(room, date, startTime, endTime)) {
            throw new SchedulingConflictException("Room is occupied");
        }
        if (isTeacherOccupied(user, date, startTime, endTime)) {
            throw new SchedulingConflictException("Teacher is occupied");
        }
    }

    public void validateCourseScheduling(Users user, Course course, Room room, LocalDate date, LocalTime startTime,
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

    private boolean isRoomOccupied(Room room, LocalDate date, LocalTime startTime, LocalTime endTime) {
        CourseTimeSlot courseTimeSlot = CourseTimeSlot.get(startTime, endTime);
        ExamTimeSlot examTimeSlot = ExamTimeSlot.get(startTime, endTime);
        DayOfWeek day = date.getDayOfWeek();
        boolean isOccupiedForCourse = courseSchedulingRepository.existsByRoomAndDayAndTimeSlot(room, day,
                courseTimeSlot);
        boolean isOccupiedForExam = examSchedulingRepository.existsByRoomAndSessionDateAndTimeSlot(room, date,
                examTimeSlot);
        return isOccupiedForCourse || isOccupiedForExam;
    }

    private boolean isTeacherOccupied(Users user, LocalDate date, LocalTime startTime, LocalTime endTime) {
        CourseTimeSlot courseTimeSlot = CourseTimeSlot.get(startTime, endTime);
        ExamTimeSlot examTimeSlot = ExamTimeSlot.get(startTime, endTime);
        DayOfWeek day = date.getDayOfWeek();
        boolean isOccupiedForCourse = courseSchedulingRepository.existsByAssignedCourseTeacherAndDayAndTimeSlot(user,
                day,
                courseTimeSlot);
        boolean isOccupiedForExam = examSchedulingRepository.existsByProctorAndSessionDateAndTimeSlot(user, date,
                examTimeSlot);
        return isOccupiedForCourse || isOccupiedForExam;
    }

    private boolean onlyOneCourseIsScheduledPerWeek(Course course) {
        Integer counter = 0;
        for (DayOfWeek day : DayOfWeek.values()) {
            if (courseSchedulingRepository.existsByAssignedCourseAndDay(course, day))
                counter++;
        }
        return counter == 1;
    }
}
