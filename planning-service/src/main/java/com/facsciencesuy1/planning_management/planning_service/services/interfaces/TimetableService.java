package com.facsciencesuy1.planning_management.planning_service.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.facsciencesuy1.planning_management.dtos.TimetableDTO;
import com.facsciencesuy1.planning_management.entities.types.Semester;
import com.facsciencesuy1.planning_management.entities.types.SessionType;
import com.facsciencesuy1.planning_management.planning_service.utils.dtos.TimeSlotDTO;

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
