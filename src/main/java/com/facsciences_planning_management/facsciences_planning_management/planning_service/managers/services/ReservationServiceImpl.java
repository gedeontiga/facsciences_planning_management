package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.entities.repositories.UserRepository;
import com.facsciences_planning_management.facsciences_planning_management.exceptions.CustomBusinessException;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation.RequestStatus;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Room;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.ReservationRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.RoomRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationProcessingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationRequestDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationResponseDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.ReservationService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String WS_RESERVATION_TOPIC = "/topic/reservations";

    @Override
    @Transactional
    public ReservationResponseDTO createRequest(ReservationRequestDTO request) {
        Users teacher = userRepository.findById(request.teacherId())
                .orElseThrow(() -> new CustomBusinessException("Teacher not found with id: " + request.teacherId()));

        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new CustomBusinessException("Room not found with id: " + request.roomId()));

        Reservation reservation = Reservation.builder()
                .teacher(teacher)
                .sessionType(request.sessionType())
                .status(RequestStatus.PENDING)
                .preferredRoom(room)
                .preferredStartTime(request.startTime())
                .preferredEndTime(request.endTime())
                .preferredDay(request.day())
                .createdAt(LocalDateTime.now())
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        ReservationResponseDTO responseDTO = ReservationResponseDTO.fromReservation(savedReservation);

        // Notify all subscribed clients of the new reservation
        messagingTemplate.convertAndSend(WS_RESERVATION_TOPIC, responseDTO);

        return responseDTO;
    }

    @Override
    @Transactional
    public ReservationResponseDTO processRequest(String requestId, ReservationProcessingDTO request) {
        Reservation reservation = reservationRepository.findById(requestId)
                .orElseThrow(() -> new CustomBusinessException("Reservation not found with id: " + requestId));

        // Assuming the processor is the currently logged-in admin user.
        // This would typically come from the security context.
        Users admin = userRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new CustomBusinessException("No admin user found to process request"));

        reservation.setStatus(RequestStatus.valueOf(request.status().toUpperCase()));
        reservation.setAdminComment(request.message());
        reservation.setProcessedBy(admin);
        reservation.setProcessedAt(LocalDateTime.now());

        Reservation updatedReservation = reservationRepository.save(reservation);
        ReservationResponseDTO responseDTO = ReservationResponseDTO.fromReservation(updatedReservation);

        // Notify all subscribed clients of the updated reservation
        messagingTemplate.convertAndSend(WS_RESERVATION_TOPIC, responseDTO);

        return responseDTO;
    }

    @Override
    public Page<ReservationResponseDTO> getReservations(String teacherId, Pageable page) {
        return reservationRepository.findByTeacherId(teacherId, page)
                .map(ReservationResponseDTO::fromReservation);
    }

    @Override
    public Page<ReservationResponseDTO> getAllRequests(String status, Pageable page) {
        if (status != null && !status.isBlank()) {
            return reservationRepository.findByStatusOrderByCreatedAt(RequestStatus.valueOf(status.toUpperCase()), page)
                    .map(ReservationResponseDTO::fromReservation);
        }
        return reservationRepository.findAll(page)
                .map(ReservationResponseDTO::fromReservation);
    }

    @Override
    public void deleteRequest(String id) {
        if (!reservationRepository.existsById(id)) {
            throw new CustomBusinessException("Reservation not found with id: " + id);
        }
        reservationRepository.deleteById(id);
    }
}