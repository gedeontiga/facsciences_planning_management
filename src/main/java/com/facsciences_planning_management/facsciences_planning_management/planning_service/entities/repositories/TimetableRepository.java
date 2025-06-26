package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Timetable;

import java.util.List;
import java.util.Optional;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

@Repository
public interface TimetableRepository extends MongoRepository<Timetable, String> {
        List<Timetable> findByAcademicYearAndSemester(String academicYear, String semester);

        Optional<Timetable> findByAcademicYearAndSemesterAndLevelIdAndSessionTypeAndUsedTrue(String academicYear,
                        String semester,
                        String levelId, SessionType sessionType);

        Page<Timetable> findByAcademicYearAndSessionTypeAndUsedTrueAndLevel_Branch_Id(String academicYear,
                        SessionType sessionType, String branchId, Pageable page);

        List<Integer> findDistinctByAcademicYearOrderByAcademicYearDesc();
}