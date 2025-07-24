package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.Semester;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimeSlotDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimetableDTO;

public interface TimetableService {
	TimetableDTO createTimetableForExam(String levelId, String academicYear, Semester semester,
			SessionType sessionType);

	TimetableDTO createTimetableForCourse(String levelId, String academicYear, Semester semester,
			SessionType sessionType);

	TimetableDTO getTimetableById(String id);

	Page<TimetableDTO> getTimetablesByBranch(String academicYear, Semester semester,
			String branchId, SessionType sessionType, Pageable page);

	String createAcademicYear(String label);

	TimetableDTO generateTimetableForLevel(String academicYear, Semester semester, String levelId);

	TimetableDTO getTimetableByLevelAndSemester(String academicYear, String levelId, Semester semester,
			SessionType sessionType);

	Page<TimetableDTO> getTimetableForCurrentUser(Pageable page, SessionType sessionType);

	Page<TimetableDTO> getTimetableForCurrentUser(Pageable page, SessionType sessionType, Semester semester);

	List<String> getAllAcademicYears();

	List<String> getSessionTypes();

	List<TimeSlotDTO> getCoursesTimeSlots();

	List<TimeSlotDTO> getExamTimeSlots();

	List<String> getSemestersForCourseTimetable();

	List<String> getSemestersForExamTimetable();

	void deleteTimetable(String id);
}
