package com.facsciencesuy1.planning_management.planning_service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facsciencesuy1.planning_management.dtos.ReservationResponseDTO;
import com.facsciencesuy1.planning_management.entities.CourseScheduling;
import com.facsciencesuy1.planning_management.entities.ExamScheduling;
import com.facsciencesuy1.planning_management.entities.Reservation;
import com.facsciencesuy1.planning_management.entities.Reservation.RequestStatus;
import com.facsciencesuy1.planning_management.entities.Room;
import com.facsciencesuy1.planning_management.entities.Ue;
import com.facsciencesuy1.planning_management.entities.Users;
import com.facsciencesuy1.planning_management.entities.types.SessionType;
import com.facsciencesuy1.planning_management.entities.types.TimeSlot;
import com.facsciencesuy1.planning_management.entities.types.TimeSlot.CourseTimeSlot;
import com.facsciencesuy1.planning_management.entities.types.TimeSlot.ExamTimeSlot;
import com.facsciencesuy1.planning_management.exceptions.CustomBusinessException;
import com.facsciencesuy1.planning_management.planning_service.repositories.CourseRepository;
import com.facsciencesuy1.planning_management.planning_service.repositories.ReservationRepository;
import com.facsciencesuy1.planning_management.planning_service.repositories.RoomRepository;
import com.facsciencesuy1.planning_management.planning_service.repositories.TimetableRepository;
import com.facsciencesuy1.planning_management.planning_service.repositories.UeRepository;
import com.facsciencesuy1.planning_management.planning_service.repositories.UserRepository;
import com.facsciencesuy1.planning_management.planning_service.services.interfaces.ReservationService;
import com.facsciencesuy1.planning_management.planning_service.utils.dtos.CourseSchedulingRequest;
import com.facsciencesuy1.planning_management.planning_service.utils.dtos.ExamSchedulingRequest;
import com.facsciencesuy1.planning_management.planning_service.utils.dtos.ReservationProcessingDTO;
import com.facsciencesuy1.planning_management.planning_service.utils.dtos.ReservationRequestDTO;

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
	private final CourseRepository courseRepository;
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

		String[] academicYear = timetableRepository.findById(request.timetableId())
				.orElseThrow(() -> new CustomBusinessException("Timetable not found with id: " + request.timetableId()))
				.getAcademicYear().split("-");

		Integer year1 = Integer.parseInt(academicYear[0]);
		Integer year2 = Integer.parseInt(academicYear[1]);
		LocalDate date = LocalDate.parse(request.date());
		if (date.getYear() != year1 && date.getYear() != year2) {
			throw new CustomBusinessException("Academic year does not match date: " + request.date());
		}
		Users teacher = userRepository.findById(request.teacherId())
				.orElseThrow(() -> new CustomBusinessException("Teacher not found with id: " + request.teacherId()));

		Room room = roomRepository.findById(request.roomId())
				.orElseThrow(() -> new CustomBusinessException("Room not found with id: " + request.roomId()));

		Ue ue = ueRepository.findById(request.ueId())
				.orElseThrow(() -> new CustomBusinessException("UE not found with id: " + request.ueId()));

		if (!ue.getAssigned()) {
			throw new CustomBusinessException("UE is not assigned to any course.");
		}

		if (result.isCourseTimeSlot()) {
			if (!courseRepository.existsByUeIdAndTeacherId(request.ueId(), request.teacherId())) {
				throw new CustomBusinessException(
						"Teacher is not a teacher of the course with id: " + request.ueId());
			}
			CourseTimeSlot courseSlot = result.asCourseTimeSlot();
			startTime = courseSlot.getStartTime();
			endTime = courseSlot.getEndTime();
			schedulingConflictService.validateScheduling(teacher, room,
					LocalDate.parse(request.date()).getDayOfWeek().name(),
					startTime, endTime);
		} else if (result.isExamTimeSlot()) {
			ExamTimeSlot examSlot = result.asExamTimeSlot();
			startTime = examSlot.getStartTime();
			endTime = examSlot.getEndTime();
			schedulingConflictService.validateScheduling(teacher, room, request.date(),
					startTime, endTime);
		} else {
			throw new CustomBusinessException("Invalid time slot type: " + request.timeSlotLabel());
		}
		if (reservationRepository.existsByTeacherIdAndDateAndStartTimeAndEndTime(
				request.teacherId(), LocalDate.parse(request.date()), startTime, endTime)) {
			throw new CustomBusinessException("A reservation already exists for this time slot.");
		}

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
				.date(date)
				.createdAt(LocalDateTime.now())
				.build();

		Reservation savedReservation = reservationRepository.save(reservation);
		ReservationResponseDTO responseDTO = ReservationResponseDTO.fromReservation(savedReservation);

		webSocketUpdateService.sendUpdate(WS_RESERVATION_TOPIC + "create/" + savedReservation.getId(), responseDTO);

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

		webSocketUpdateService.sendUpdate(WS_RESERVATION_TOPIC + "update/" + updatedReservation.getId(), responseDTO);

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
		reservationRepository.findById(id).ifPresent(
				reservation -> {
					if (reservation.getStatus().equals(RequestStatus.APPROVED)) {
						if (reservation.getSessionType().equals(SessionType.COURSE)) {
							courseSchedulingService.deleteScheduling(reservation.getScheduling().getId());
						} else {
							examSchedulingService.deleteScheduling(reservation.getScheduling().getId());
						}
					}
					reservationRepository.deleteById(id);
				});
		webSocketUpdateService.sendUpdate(WS_RESERVATION_TOPIC + "delete/" + id, Map.of("reservationId", id));
	}

	@Override
	public List<String> getAllReservationStatuses() {
		return Arrays.stream(RequestStatus.values()).map(RequestStatus::name).collect(Collectors.toList());
	}

	@Scheduled(cron = "@hourly")
	public void removeOldReservations() {
		reservationRepository.findByDateBeforeAndStatus(LocalDate.now(), RequestStatus.APPROVED).stream()
				.forEach(reservation -> {
					if (reservation.getSessionType().equals(SessionType.COURSE)) {
						courseSchedulingService.deleteScheduling(reservation.getScheduling().getId());
					} else {
						examSchedulingService.deleteScheduling(reservation.getScheduling().getId());
					}
				});
	}
}