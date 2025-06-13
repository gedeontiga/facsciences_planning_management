// package
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;

// import org.springframework.data.domain.Sort;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import
// com.facsciences_planning_management.facsciences_planning_management.entities.repositories.UserRepository;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Reservation.RequestStatus;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.ReservationRepository;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ExamSchedulingCreateRequest;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SimpleSchedulingCreateRequest;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationCreateRequestDTO;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ReservationDTO;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.exceptions.ResourceConflictException;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.exceptions.ResourceInUseException;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.exceptions.ResourceNotFoundException;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.RoomService;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.SchedulingService;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.ReservationService;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.TimetableService;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.UeService;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class ReservationServiceImpl implements ReservationService {
// private final ReservationRepository teacherRequestRepository;
// private final RoomService roomService;
// private final UeService ueService;
// private final UserRepository userRepository;
// private final TimetableService timetableService;
// private final SchedulingService schedulingService;

// @Override
// @Transactional
// public ReservationDTO createRequest(ReservationCreateRequestDTO
// request) {
// // Validate inputs
// userRepository.findById(request.getTeacherId())
// .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
// ueService.getUeEntityById(request.getCourseId());
// timetableService.getTimetableEntityById(request.getTimetableId());

// // Check room availability if specified
// if (request.getRoomId() != null && request.getStartTime() != null &&
// request.getEndTime() != null) {
// boolean isAvailable = request.getDay() != null
// ? roomService.isRoomAvailable(request.getRoomId(), request.getStartTime(),
// request.getEndTime(),
// request.getDay())
// : roomService.isRoomAvailableForDate(request.getRoomId(),
// request.getStartTime(),
// request.getEndTime(), request.getDate());
// if (!isAvailable) {
// throw new ResourceConflictException("Requested room is not available");
// }
// }

// Reservation teacherRequest = Reservation.builder()
// .teacherId(request.getTeacherId())
// .courseId(request.getCourseId())
// .sessionType(request.getSessionType())
// .status(Reservation.RequestStatus.PENDING)
// .roomId(request.getRoomId())
// .startTime(request.getStartTime())
// .endTime(request.getEndTime())
// .day(request.getDay())
// .date(request.getDate())
// .timetableId(request.getTimetableId())
// .createdAt(LocalDateTime.now())
// .build();

// return teacherRequestRepository.save(teacherRequest).toDTO();
// }

// @Override
// @Transactional
// public ReservationDTO updateRequestStatus(String requestId,
// ReservationDTO request) {
// Reservation teacherRequest = teacherRequestRepository.findById(requestId)
// .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

// if (request.getStatus() == Reservation.RequestStatus.APPROVED) {
// // Create scheduling if approved
// if (teacherRequest.getSessionType() == SessionType.COURSE
// || teacherRequest.getSessionType() == SessionType.TUTORIAL) {
// SimpleSchedulingCreateRequest schedulingRequest =
// SimpleSchedulingCreateRequest.builder()
// .roomId(teacherRequest.getRoomId())
// .ueId(teacherRequest.getCourseId())
// .timetableId(teacherRequest.getTimetableId())
// .teacherId(teacherRequest.getTeacherId())
// .startTime(teacherRequest.getStartTime())
// .endTime(teacherRequest.getEndTime())
// .day(teacherRequest.getDay())
// .sessionType(teacherRequest.getSessionType())
// .build();
// schedulingService.createSimpleScheduling(schedulingRequest);
// } else {
// ExamSchedulingCreateRequest schedulingRequest =
// ExamSchedulingCreateRequest.builder()
// .roomId(teacherRequest.getRoomId())
// .ueId(teacherRequest.getCourseId())
// .timetableId(teacherRequest.getTimetableId())
// .proctorId(teacherRequest.getTeacherId())
// .startTime(teacherRequest.getStartTime())
// .endTime(teacherRequest.getEndTime())
// .sessionDate(teacherRequest.getDate())
// .sessionType(teacherRequest.getSessionType())
// .build();
// schedulingService.createExamScheduling(schedulingRequest);
// }
// }

// teacherRequest.setStatus(request.getStatus());
// if (request.getAdminComment() != null) {
// teacherRequest.setAdminComment(request.getAdminComment());
// }

// return teacherRequestRepository.save(teacherRequest).toDTO();
// }

// @Override
// public List<ReservationDTO> getReservations(String teacherId,
// Optional<Sort> sort) {
// Sort effectiveSort = sort.orElse(Sort.by(Sort.Direction.DESC, "createdAt"));
// return teacherRequestRepository.findByTeacherId(teacherId).stream()
// .sorted(effectiveSort)
// .map(Reservation::toDTO)
// .collect(Collectors.toList());
// }

// @Override
// public List<ReservationDTO> getAllRequests(Optional<RequestStatus> status,
// Optional<Sort> sort) {
// Sort effectiveSort = sort.orElse(Sort.by(Sort.Direction.DESC, "createdAt"));
// Stream<Reservation> stream = status.isPresent()
// ? teacherRequestRepository.findByStatus(status.get()).stream()
// : teacherRequestRepository.findAllByOrderByCreatedAtDesc().stream();
// return stream.sorted((a, b) ->
// effectiveSort.getOrderFor("createdAt").isAscending()
// ? a.getCreatedAt().compareTo(b.getCreatedAt())
// : b.getCreatedAt().compareTo(a.getCreatedAt()))
// .map(ReservationDTO::fromEntity)
// .collect(Collectors.toList());
// }

// @Override
// @Transactional
// public void deleteRequest(String id) {
// Reservation request = teacherRequestRepository.findById(id)
// .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
// if (request.getStatus() == Reservation.RequestStatus.APPROVED) {
// throw new ResourceInUseException("Cannot delete approved request");
// }
// teacherRequestRepository.deleteById(id);
// }
// }
