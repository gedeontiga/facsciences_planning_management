package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationProcessingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationRequestDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationResponseDTO;

public interface ReservationService {
    ReservationResponseDTO createRequest(ReservationRequestDTO request);

    ReservationResponseDTO processRequest(String requestId, ReservationProcessingDTO request);

    Page<ReservationResponseDTO> getReservations(String teacherId, Pageable page);

    Page<ReservationResponseDTO> getAllRequests(String status, Pageable page);

    void deleteRequest(String id);
}
