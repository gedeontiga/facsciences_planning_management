package com.facsciencesuy1.planning_management.planning_service.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.facsciencesuy1.planning_management.dtos.ReservationResponseDTO;
import com.facsciencesuy1.planning_management.planning_service.utils.dtos.ReservationProcessingDTO;
import com.facsciencesuy1.planning_management.planning_service.utils.dtos.ReservationRequestDTO;

public interface ReservationService {
    ReservationResponseDTO createRequest(ReservationRequestDTO request);

    ReservationResponseDTO processRequest(String requestId, ReservationProcessingDTO request);

    Page<ReservationResponseDTO> getReservationByTeacher(String teacherId, Pageable page);

    Page<ReservationResponseDTO> getAllRequests(String status, Pageable page);

    void deleteRequest(String id);

    List<String> getAllReservationStatuses();
}
