package com.facsciencesuy1.planning_management.academic_service.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.Timetable;

@Repository
public interface TimetableRepository extends MongoRepository<Timetable, String> {
}

// interface TimetableRepositoryCustom {
// }