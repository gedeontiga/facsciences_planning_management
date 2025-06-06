package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import java.time.Year;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Planning;

import java.util.List;

@Repository
public interface PlanningRepository extends MongoRepository<Planning, String> {
    List<Planning> findByAcademicYearAndSemester(Year academicYear, String semester);
}