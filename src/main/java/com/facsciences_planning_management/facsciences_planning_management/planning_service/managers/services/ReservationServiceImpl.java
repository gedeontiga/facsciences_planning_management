package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseSchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ExamSchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationProcessingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationRequestDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationResponseDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.ReservationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

	private final ReservationRepository reservationRepository;
	private final UserRepository userRepository;
	private final RoomRepository roomRepository;
	private final CourseSchedulingServiceImpl courseSchedulingService;
	private final ExamSchedulingServiceImpl examSchedulingService;
	private final SchedulingConflictService schedulingConflictService;
	private final WebSocketUpdateService webSocketUpdateService;
	private static final String WS_RESERVATION_TOPIC = "/topic/reservations";

	@Override
	@Transactional
	public ReservationResponseDTO createRequest(ReservationRequestDTO request) {
		Users teacher = userRepository.findById(request.teacherId())
				.orElseThrow(() -> new CustomBusinessException("Teacher not found with id: " + request.teacherId()));

		Room room = roomRepository.findById(request.roomId())
				.orElseThrow(() -> new CustomBusinessException("Room not found with id: " + request.roomId()));

		schedulingConflictService.validateScheduling(teacher, room, LocalDate.parse(request.date()),
				LocalTime.parse(request.startTime()), LocalTime.parse(request.endTime()));

		Reservation reservation = Reservation.builder()
				.teacher(teacher)
				.sessionType(request.sessionType())
				.status(RequestStatus.PENDING)
				.room(room)
				.startTime(LocalTime.parse(request.startTime()))
				.endTime(LocalTime.parse(request.endTime()))
				.date(LocalDate.parse(request.date()))
				.createdAt(LocalDateTime.now())
				.build();

		Reservation savedReservation = reservationRepository.save(reservation);
		ReservationResponseDTO responseDTO = ReservationResponseDTO.fromReservation(savedReservation);

		webSocketUpdateService.sendUpdate(WS_RESERVATION_TOPIC, responseDTO);

		return responseDTO;
	}

	@Override
	@Transactional
	public ReservationResponseDTO processRequest(String requestId, ReservationProcessingDTO request) {
		Reservation reservation = reservationRepository.findById(requestId)
				.orElseThrow(() -> new CustomBusinessException("Reservation not found with id: " + requestId));

		String email = ((Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
		Users admin = userRepository.findByEmail(email)
				.orElseThrow(() -> new CustomBusinessException("Admin not found with email: " + email));

		if (request.status() == RequestStatus.APPROVED) {
			if (reservation.getSessionType().equals(SessionType.COURSE)) {
				courseSchedulingService.createScheduling(CourseSchedulingDTO.fromReservation(reservation));
			} else {
				examSchedulingService.createScheduling(ExamSchedulingDTO.fromReservation(reservation));
			}
		}

		reservation.setStatus(request.status());
		reservation.setAdminComment(request.message());
		reservation.setProcessedBy(admin);
		reservation.setProcessedAt(LocalDateTime.now());

		Reservation updatedReservation = reservationRepository.save(reservation);
		ReservationResponseDTO responseDTO = ReservationResponseDTO.fromReservation(updatedReservation);

		webSocketUpdateService.sendUpdate(WS_RESERVATION_TOPIC, responseDTO);

		return responseDTO;
	}

	@Override
	public Page<ReservationResponseDTO> getReservations(Pageable page) {
		String email = ((Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
		String teacherId = userRepository.findByEmail(email)
				.orElseThrow(() -> new CustomBusinessException("User not found")).getId();
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

	@Override
	public List<String> getAllReservationStatuses() {
		return Arrays.stream(RequestStatus.values()).map(RequestStatus::name).collect(Collectors.toList());
	}
}