package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationProcessingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationRequestDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationResponseDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.ReservationService;

@Validated
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservationRequest(
            @RequestBody ReservationRequestDTO request) {
        ReservationResponseDTO createdReservation = reservationService.createRequest(request);
        return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
    }

    @PatchMapping("/process/{requestId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ReservationResponseDTO> processReservationRequest(
            @PathVariable String requestId,
            @RequestBody ReservationProcessingDTO request) {
        ReservationResponseDTO processedReservation = reservationService.processRequest(requestId, request);
        return ResponseEntity.ok(processedReservation);
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'DEPARTMENT_HEAD', 'ADMIN')")
    public ResponseEntity<Page<ReservationResponseDTO>> getReservationsByTeacher(
            @PathVariable String teacherId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ReservationResponseDTO> reservations = reservationService.getReservationByTeacher(teacherId, pageable);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<ReservationResponseDTO>> getAllReservationRequests(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ReservationResponseDTO> requests = reservationService.getAllRequests(status, pageable);
        return ResponseEntity.ok(requests);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteReservationRequest(@PathVariable String id) {
        reservationService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statuses")
    public ResponseEntity<Iterable<String>> getAllReservationStatuses() {
        Iterable<String> statuses = reservationService.getAllReservationStatuses();
        return ResponseEntity.ok(statuses);
    }
}
