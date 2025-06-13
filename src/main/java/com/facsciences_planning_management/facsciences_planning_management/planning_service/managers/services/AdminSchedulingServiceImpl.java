// package
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

// import java.util.Optional;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import
// com.facsciences_planning_management.facsciences_planning_management.entities.Users;
// import
// com.facsciences_planning_management.facsciences_planning_management.entities.repositories.UserRepository;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseAssignment;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Ue;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.CourseTeacherAssignmentRepository;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ExamSchedulingCreateRequest;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ExamSchedulingDTO;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ManualExamSchedulingRequestDTO;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ManualSchedulingDetailsDTO;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ManualSimpleSchedulingRequestDTO;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SimpleSchedulingCreateRequest;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SimpleSchedulingDTO;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.exceptions.ResourceConflictException;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.exceptions.ResourceNotFoundException;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.AdminSchedulingService;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.RoomService;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.SchedulingService;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.UeService;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class AdminSchedulingServiceImpl implements AdminSchedulingService {
// private final SchedulingService schedulingService;
// private final RoomService roomService;
// private final UeService ueService;
// private final UserRepository userRepository;
// private final CourseTeacherAssignmentRepository
// courseTeacherAssignmentRepository;

// @Override
// @Transactional
// public SimpleSchedulingDTO
// createManualSimpleScheduling(ManualSimpleSchedulingRequestDTO request) {
// // Validate and create scheduling
// if (!roomService.isRoomAvailable(request.getRoomId(), request.getStartTime(),
// request.getEndTime(),
// request.getDay())) {
// throw new ResourceConflictException("Room is not available");
// }

// SimpleSchedulingCreateRequest schedulingRequest =
// SimpleSchedulingCreateRequest.builder()
// .roomId(request.getRoomId())
// .ueId(request.getCourseId())
// .timetableId(request.getTimetableId())
// .teacherId(request.getTeacherId())
// .startTime(request.getStartTime())
// .endTime(request.getEndTime())
// .day(request.getDay())
// .sessionType(request.getSessionType())
// .build();

// return schedulingService.createSimpleScheduling(schedulingRequest);
// }

// @Override
// @Transactional
// public ExamSchedulingDTO
// createManualExamScheduling(ManualExamSchedulingRequestDTO request) {
// if (!roomService.isRoomAvailableForDate(request.getRoomId(),
// request.getStartTime(), request.getEndTime(),
// request.getSessionDate())) {
// throw new ResourceConflictException("Room is not available");
// }

// ExamSchedulingCreateRequest schedulingRequest =
// ExamSchedulingCreateRequest.builder()
// .roomId(request.getRoomId())
// .ueId(request.getCourseId())
// .timetableId(request.getTimetableId())
// .proctorId(request.getProctorId())
// .startTime(request.getStartTime())
// .endTime(request.getEndTime())
// .sessionDate(request.getSessionDate())
// .sessionType(request.getSessionType())
// .build();

// return schedulingService.createExamScheduling(schedulingRequest);
// }

// @Override
// @Transactional
// public void assignCourseToTeacher(String courseId, String teacherId,
// Optional<ManualSchedulingDetailsDTO> schedulingDetails) {
// Ue course = ueService.getUeEntityById(courseId);
// Users teacher = userRepository.findById(teacherId)
// .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

// CourseAssignment assignment = CourseAssignment.builder()
// .courseId(courseId)
// .teacherId(teacherId)
// .build();
// courseTeacherAssignmentRepository.save(assignment);

// if (schedulingDetails.isPresent()) {
// ManualSchedulingDetailsDTO details = schedulingDetails.get();
// if (details.isSimpleScheduling()) {
// ManualSimpleSchedulingRequestDTO request =
// ManualSimpleSchedulingRequestDTO.builder()
// .roomId(details.getRoomId())
// .courseId(courseId)
// .timetableId(details.getTimetableId())
// .teacherId(teacherId)
// .startTime(details.getStartTime())
// .endTime(details.getEndTime())
// .day(details.getDay())
// .sessionType(details.getSessionType())
// .build();
// createManualSimpleScheduling(request);
// } else {
// ManualExamSchedulingRequestDTO request =
// ManualExamSchedulingRequestDTO.builder()
// .roomId(details.getRoomId())
// .courseId(courseId)
// .timetableId(details.getTimetableId())
// .proctorId(teacherId)
// .startTime(details.getStartTime())
// .endTime(details.getEndTime())
// .sessionDate(details.getSessionDate())
// .sessionType(details.getSessionType())
// .build();
// createManualExamScheduling(request);
// }
// }
// }
// }
