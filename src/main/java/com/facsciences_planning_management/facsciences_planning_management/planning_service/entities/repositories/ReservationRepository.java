package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation.RequestStatus;
import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface ReservationRepository extends
		MongoRepository<Reservation, String> {
	Page<Reservation> findByTeacherId(String teacherId, Pageable page);

	Page<Reservation> findByStatusOrderByCreatedAt(RequestStatus status, Pageable page);

	boolean existsByTeacherIdAndDateAndStartTimeAndEndTime(String teacherId, LocalDate date, LocalTime startTime,
			LocalTime endTime);

	List<Reservation> findAllByOrderByCreatedAtDesc();

	List<Reservation> findByDateBeforeAndStatus(LocalDate date, RequestStatus status);
}
