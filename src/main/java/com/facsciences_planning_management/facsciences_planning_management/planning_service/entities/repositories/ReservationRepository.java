package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation.RequestStatus;

@Repository
public interface ReservationRepository extends
        MongoRepository<Reservation, String> {
    List<Reservation> findByTeacherId(String teacherId);

    List<Reservation> findByStatusOrderByCreatedAt(RequestStatus status);

    List<Reservation> findAllByOrderByCreatedAtDesc();
}
