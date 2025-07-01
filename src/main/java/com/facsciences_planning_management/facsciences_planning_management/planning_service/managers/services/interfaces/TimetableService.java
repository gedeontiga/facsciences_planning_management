package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimeSlotDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimetableDTO;

public interface TimetableService {
	TimetableDTO createTimetableForExam(String levelId, String academicYear, String semester, SessionType sessionType);

	TimetableDTO getTimetableById(String id);

	// List<TimetableDTO> getAllTimetablesByAcademicYear(String academicYear,
	// Pageable
	// page);

	// List<TimetableDTO> getTimetablesBySemester(String academicYear,
	// String semester);

	Page<TimetableDTO> getTimetablesByBranch(String academicYear,
			String branchId, SessionType sessionType, Pageable page);

	String createAcademicYear(String label);

	// List<TimetableDTO> getTimetablesByBranchAndSemester(Year
	// academicYear, String branchId,
	// String semester);

	// TimetableDTO getTimetablesByLevel(String academicYear, String
	// levelId,
	// SessionType sessionType);

	TimetableDTO generateTimetableForLevel(String academicYear, String semester, String levelId);

	TimetableDTO getTimetableByLevelAndSemester(String academicYear, String levelId, String semester,
			SessionType sessionType);

	// TimetableDTO updateTimetable(String id, TimetableDTO request);

	// void deleteTimetable(String id);

	List<String> getAllAcademicYears();

	List<String> getSessionTypes();

	List<TimeSlotDTO> getCoursesTimeSlots();

	List<TimeSlotDTO> getExamTimeSlots();

	List<String> getSemestersForCourseTimetable();

	List<String> getSemestersForExamTimetable();
}
