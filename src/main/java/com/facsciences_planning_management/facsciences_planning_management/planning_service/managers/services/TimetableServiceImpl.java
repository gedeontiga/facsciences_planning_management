// package
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

// import java.time.LocalDateTime;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;
// import java.util.stream.Collectors;

// import org.springframework.stereotype.Service;

// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.ExamScheduling;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Timetable;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.SimpleScheduling;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.ExamSchedulingRepository;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.TimetableRepository;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.SimpleSchedulingRepository;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimetableCreateRequest;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimetableDTO;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimetableUpdateRequest;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SchedulingDTO;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.exceptions.ResourceInUseException;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.exceptions.ResourceNotFoundException;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.TimetableService;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class TimetableServiceImpl implements TimetableService {
// private final TimetableRepository planningRepository;
// private final SimpleSchedulingRepository simpleSchedulingRepository;
// private final ExamSchedulingRepository examSchedulingRepository;

// @Override
// public TimetableDTO createTimetable(TimetableCreateRequest request) {
// Timetable planning = Timetable.builder()
// .academicYear(request.academicYear())
// .semester(request.semester())
// .createdAt(LocalDateTime.now())
// .build();

// return planningRepository.save(planning).toDTO();
// }

// @Override
// public TimetableDTO getTimetableById(String id) {
// return planningRepository.findById(id)
// .map(Timetable::toDTO)
// .orElseThrow(() -> new ResourceNotFoundException("Planning not found with id:
// " + id));
// }

// @Override
// public Timetable getTimetableEntityById(String id) {
// return planningRepository.findById(id)
// .orElseThrow(() -> new ResourceNotFoundException("Planning not found with id:
// " + id));
// }

// @Override
// public List<TimetableDTO> getAllTimetables() {
// return planningRepository.findAll().stream()
// .map(Timetable::toDTO)
// .collect(Collectors.toList());
// }

// @Override
// public TimetableDTO updateTimetable(String id, TimetableUpdateRequest
// request) {
// Timetable planning = planningRepository.findById(id)
// .orElseThrow(() -> new ResourceNotFoundException("Planning not found with id:
// " + id));

// if (request.academicYear() != null) {
// planning.setAcademicYear(request.academicYear());
// }

// if (request.semester() != null) {
// planning.setSemester(request.semester());
// }

// return planningRepository.save(planning).toDTO();
// }

// @Override
// public void deleteTimetable(String id) {

// if (!simpleSchedulingRepository.findByTimetableId(id).isEmpty() ||
// !examSchedulingRepository.findByTimetableId(id).isEmpty()) {
// throw new ResourceInUseException("Cannot delete planning with associated
// schedules");
// }

// planningRepository.deleteById(id);
// }

// @Override
// public TimetableDTO getDetailedTimetableById(String id) {
// Timetable planning = planningRepository.findById(id)
// .orElseThrow(() -> new ResourceNotFoundException("Planning not found with id:
// " + id));

// List<SimpleScheduling> simpleSchedules =
// simpleSchedulingRepository.findByTimetableId(id);
// List<ExamScheduling> examSchedules =
// examSchedulingRepository.findByTimetableId(id);

// Set<SchedulingDTO> allSchedules = new HashSet<>();
// allSchedules.addAll(simpleSchedules.stream().map(SimpleScheduling::toDTO).collect(Collectors.toSet()));
// allSchedules.addAll(examSchedules.stream().map(ExamScheduling::toDTO).collect(Collectors.toSet()));

// return TimetableDTO.fromTimetable(planning);
// }
// }