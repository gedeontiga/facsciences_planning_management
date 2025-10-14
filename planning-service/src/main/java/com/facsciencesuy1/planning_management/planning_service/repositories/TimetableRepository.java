package com.facsciencesuy1.planning_management.planning_service.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.Timetable;
import com.facsciencesuy1.planning_management.entities.types.Semester;
import com.facsciencesuy1.planning_management.entities.types.SessionType;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimetableRepository extends MongoRepository<Timetable, String> {
	List<Timetable> findByAcademicYearAndSemester(String academicYear, Semester semester);

	boolean existsByAcademicYearAndSemesterAndLevelIdAndSessionType(String academicYear, Semester semester,
			String levelId, SessionType sessionType);

	Optional<Timetable> findByAcademicYearAndSemesterAndLevelIdAndSessionTypeAndUsedTrue(String academicYear,
			Semester semester,
			String levelId, SessionType sessionType);

	Page<Timetable> findByAcademicYearAndSemesterAndSessionTypeAndUsedTrueAndBranchId(String academicYear,
			Semester semester,
			SessionType sessionType, String branchId, Pageable page);

	Page<Timetable> findBySessionTypeAndUsedTrue(SessionType sessionType, Pageable page);

	Page<Timetable> findBySessionTypeAndSemesterAndUsedTrue(SessionType sessionType, Semester semester, Pageable page);

	Page<Timetable> findByBranchIdAndSessionTypeAndUsedTrue(String branchId, SessionType sessionType, Pageable page);

	Page<Timetable> findByLevelIdAndSessionTypeAndUsedTrue(String levelId, SessionType sessionType,
			Pageable page);

	Page<Timetable> findByBranchIdAndSemesterAndSessionTypeAndUsedTrue(String branchId, Semester semester,
			SessionType sessionType, Pageable page);

	Page<Timetable> findByLevelIdAndSemesterAndSessionTypeAndUsedTrue(String levelId, Semester semester,
			SessionType sessionType,
			Pageable page);

	Page<Timetable> findBySemesterAndSessionTypeAndUsedTrue(Semester semester, SessionType sessionType,
			Pageable page);
}

// interface TimetableRepositoryCustom {
// }