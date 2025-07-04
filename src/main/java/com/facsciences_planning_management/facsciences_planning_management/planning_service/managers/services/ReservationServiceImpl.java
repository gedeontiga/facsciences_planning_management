package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facsciences_planning_management.facsciences_planning_management.entities.Users;
import com.facsciences_planning_management.facsciences_planning_management.entities.repositories.UserRepository;
import com.facsciences_planning_management.facsciences_planning_management.exceptions.CustomBusinessException;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.ExamScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation.RequestStatus;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Room;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Ue;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.ReservationRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.RoomRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.TimetableRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.UeRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.CourseTimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.ExamTimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseSchedulingRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ExamSchedulingRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationProcessingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationRequestDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationResponseDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.ReservationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

	private final ReservationRepository reservationRepository;
	private final UserRepository userRepository;
	private final RoomRepository roomRepository;
	private final UeRepository ueRepository;
	private final TimetableRepository timetableRepository;
	private final CourseSchedulingServiceImpl courseSchedulingService;
	private final ExamSchedulingServiceImpl examSchedulingService;
	private final SchedulingConflictService schedulingConflictService;
	private final WebSocketUpdateService webSocketUpdateService;

	private static final String WS_RESERVATION_TOPIC = "/topic/reservations/";

	@Override
	@Transactional
	public ReservationResponseDTO createRequest(ReservationRequestDTO request) {
		TimeSlot.TimeSlotResult result = TimeSlot.getTimeSlotWithType(request.timeSlotLabel());
		LocalTime startTime;
		LocalTime endTime;
		if (result.isCourseTimeSlot()) {
			CourseTimeSlot courseSlot = result.asCourseTimeSlot();
			startTime = courseSlot.getStartTime();
			endTime = courseSlot.getEndTime();
		} else if (result.isExamTimeSlot()) {
			ExamTimeSlot examSlot = result.asExamTimeSlot();
			startTime = examSlot.getStartTime();
			endTime = examSlot.getEndTime();
		} else {
			throw new CustomBusinessException("Invalid time slot type: " + request.timeSlotLabel());
		}
		if (reservationRepository.existsByTeacherIdAndDateAndStartTimeAndEndTime(
				request.teacherId(), LocalDate.parse(request.date()), startTime, endTime)) {
			throw new CustomBusinessException("A reservation already exists for this time slot.");
		}
		if (!timetableRepository.existsById(request.timetableId())) {
			throw new CustomBusinessException("Timetable not found with id: " + request.timetableId());
		}
		Users teacher = userRepository.findById(request.teacherId())
				.orElseThrow(() -> new CustomBusinessException("Teacher not found with id: " + request.teacherId()));

		Room room = roomRepository.findById(request.roomId())
				.orElseThrow(() -> new CustomBusinessException("Room not found with id: " + request.roomId()));

		Ue ue = ueRepository.findById(request.ueId())
				.orElseThrow(() -> new CustomBusinessException("UE not found with id: " + request.ueId()));

		schedulingConflictService.validateScheduling(teacher, room, request.date(),
				startTime, endTime);

		// log.info("Creating reservation request: {}", teacher.getUsername());
		Reservation reservation = Reservation.builder()
				.teacher(teacher)
				.sessionType(request.sessionType())
				.status(RequestStatus.PENDING)
				.timetableId(request.timetableId())
				.ue(ue)
				.room(room)
				.timeSlotLabel(request.timeSlotLabel())
				.startTime(startTime)
				.endTime(endTime)
				.date(LocalDate.parse(request.date()))
				.createdAt(LocalDateTime.now())
				.build();

		Reservation savedReservation = reservationRepository.save(reservation);
		ReservationResponseDTO responseDTO = ReservationResponseDTO.fromReservation(savedReservation);

		webSocketUpdateService.sendUpdate(WS_RESERVATION_TOPIC + savedReservation.getId(), responseDTO);

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

		log.info("Processing reservation request: {}", reservation.getId());

		reservation.setStatus(request.status());
		reservation.setAdminComment(request.message());
		reservation.setProcessedBy(admin);

		if (request.status() == RequestStatus.APPROVED) {
			if (reservation.getSessionType().equals(SessionType.COURSE)) {
				CourseScheduling scheduling = courseSchedulingService.createCourseScheduling(
						CourseSchedulingRequest.fromReservation(ReservationRequestDTO.fromReservation(reservation)));
				reservation.setScheduling(scheduling);
			} else {
				ExamScheduling scheduling = examSchedulingService.createExamScheduling(
						ExamSchedulingRequest.fromReservation(ReservationRequestDTO.fromReservation(reservation)));
				reservation.setScheduling(scheduling);
			}
		}

		Reservation updatedReservation = reservationRepository.save(reservation);
		ReservationResponseDTO responseDTO = ReservationResponseDTO.fromReservation(updatedReservation);

		webSocketUpdateService.sendUpdate(WS_RESERVATION_TOPIC + updatedReservation.getId(), responseDTO);

		return responseDTO;
	}

	@Override
	public Page<ReservationResponseDTO> getReservationByTeacher(String teacherId, Pageable page) {
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

	@Scheduled(cron = "@hourly")
	public void removeOldReservations() {
		reservationRepository.findByDateBefore(LocalDate.now()).stream()
				.forEach(reservation -> {
					log.info("Removing old reservation: {}", reservation.getId());
					reservationRepository.deleteById(reservation.getId());
					if (reservation.getSessionType().equals(SessionType.COURSE)) {
						courseSchedulingService.deleteScheduling(reservation.getScheduling().getId());
					} else {
						examSchedulingService.deleteScheduling(reservation.getScheduling().getId());
					}
					webSocketUpdateService.sendUpdate(WS_RESERVATION_TOPIC + reservation.getId(),
							Map.of("reservationId", reservation.getId()));
				});
	}
}