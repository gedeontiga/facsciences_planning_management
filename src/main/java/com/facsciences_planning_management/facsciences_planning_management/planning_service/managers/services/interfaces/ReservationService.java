package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation.RequestStatus;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationRequestDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationresponseDTO;

public interface ReservationService {
    ReservationresponseDTO createRequest(ReservationRequestDTO request);

    ReservationresponseDTO updateRequestStatus(String requestId, ReservationresponseDTO request);

    List<ReservationresponseDTO> getReservations(String teacherId, Optional<Sort> sort);

    List<ReservationresponseDTO> getAllRequests(Optional<RequestStatus> status, Optional<Sort> sort);

    void deleteRequest(String id);
}
