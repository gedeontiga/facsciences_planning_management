package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.facsciences_planning_management.facsciences_planning_management.entities.repositories.AcademicYearRepository;
import com.facsciences_planning_management.facsciences_planning_management.exceptions.CustomBusinessException;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.AcademicYear;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Course;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Level;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Room;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Scheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Timetable;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.CourseRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.CourseSchedulingRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.LevelRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.RoomRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.TimetableRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.Semester;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot.CourseTimeSlot;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimeSlotDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimetableDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.TimetableService;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimetableServiceImpl implements TimetableService {
	// ... (repository declarations)
	private final AcademicYearRepository academicYearRepository;
	private final TimetableRepository timetableRepository;
	private final TimetableSolverService timetableSolverService;
	private final CourseRepository courseRepository;
	private final RoomRepository roomRepository;
	private final LevelRepository levelRepository;
	private final CourseSchedulingRepository courseSchedulingRepository;

	@Override
	@Transactional
	public TimetableDTO generateTimetableForLevel(String academicYear, Semester semester, String levelId) {
		Level level = levelRepository.findById(levelId)
				.orElseThrow(() -> new CustomBusinessException("Level not found with id: " + levelId));

		Long headCount = level.getHeadCount();
		if (isExceededRoomCapacity(headCount)) {
			throw new CustomBusinessException(
					"Cannot generate timetable for Level '" + level.getCode() + "' due to room capacity limit.");

		}

		// Prevent regeneration if a timetable already exists and is in use for this
		// exact context
		Optional<Timetable> existingTimetable = timetableRepository
				.findByAcademicYearAndSemesterAndLevelIdAndSessionTypeAndUsedTrue(academicYear,
						semester, levelId, SessionType.COURSE);
		if (existingTimetable.isPresent()) {

			return existingTimetable.get().toDTO();
		}

		// 1. Get all active courses that MUST be scheduled for this level.
		List<Course> coursesToSchedule = courseRepository.findByObsoleteFalseAndUeLevelIdAndUeLevelSemester(levelId,
				semester);
		if (coursesToSchedule.isEmpty()) {
			throw new IllegalStateException("No active courses found for level " + level.getName() + " to schedule.");
		}

		// 2. Get all available rooms.
		List<Room> availableRooms = roomRepository
				.findByCapacityIsGreaterThanEqualOrderByCapacityAsc(headCount);
		if (availableRooms.isEmpty()) {
			throw new IllegalStateException("No available rooms in the system for scheduling.");
		}

		// 3. Get ALL existing, active course schedules from the ENTIRE system.
		// This is the crucial step to handle resource conflicts across different
		// levels.
		List<CourseScheduling> existingSchedules = courseSchedulingRepository.findByTimetableUsedTrue();

		List<DayOfWeek> days = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
				DayOfWeek.FRIDAY, DayOfWeek.SATURDAY);
		List<CourseTimeSlot> timeSlots = Arrays.asList(CourseTimeSlot.values());

		// 4. Call the solver with all required information.

		List<CourseScheduling> solvedSchedules = timetableSolverService.solve(
				coursesToSchedule, availableRooms, days, timeSlots, existingSchedules, level);

		// 5. Create and save the new timetable and its generated schedules.
		getOrCreateAcademicYear(academicYear);
		Timetable newTimetable = Timetable.builder()
				.academicYear(academicYear)
				.semester(semester)
				.level(level)
				.name("Course Planning for " + level.getCode())
				.description("Timetable for " + level.getName() + " | " + academicYear + " | " + semester.getLabel())
				.sessionType(SessionType.COURSE)
				.used(true) // New timetables are active by default.
				.build();

		Timetable savedTimetable = timetableRepository.save(newTimetable);

		Set<Scheduling> finalSchedules = new HashSet<>();
		for (CourseScheduling schedule : solvedSchedules) {
			schedule.setTimetable(savedTimetable);
			finalSchedules.add(courseSchedulingRepository.save(schedule));
		}

		savedTimetable.setSchedules(finalSchedules);

		return timetableRepository.save(savedTimetable).toDTO();
	}

	private AcademicYear getOrCreateAcademicYear(String label) {
		return academicYearRepository.findByLabel(label)
				.orElseGet(() -> academicYearRepository.save(new AcademicYear(label)));
	}

	@Override
	public String createAcademicYear(String label) {
		return getOrCreateAcademicYear(label).getLabel();
	}

	@Override
	public TimetableDTO createTimetableForCourse(String levelId, String academicYear, Semester semester,
			SessionType sessionType) {
		Level level = levelRepository.findById(levelId)
				.orElseThrow(() -> new CustomBusinessException("Level not found with id: " + levelId));
		getOrCreateAcademicYear(academicYear);
		return timetableRepository.save(Timetable.builder()
				.academicYear(academicYear)
				.semester(semester)
				.level(level)
				.name("Course Planning for " + level.getCode())
				.description("Timetable for " + level.getName() + " __" + academicYear + "__" + semester.getLabel())
				.sessionType(sessionType)
				.schedules(new HashSet<>())
				.build())
				.toDTO();
	}

	@Override
	public TimetableDTO createTimetableForExam(String levelId, String academicYear, Semester semester,
			SessionType sessionType) {
		Level level = levelRepository.findById(levelId)
				.orElseThrow(() -> new CustomBusinessException("Level not found with id: " + levelId));
		getOrCreateAcademicYear(academicYear);
		return timetableRepository.save(Timetable.builder()
				.academicYear(academicYear)
				.semester(semester)
				.level(level)
				.name("Exam Planning for " + level.getCode())
				.description("Timetable for " + level.getName() + " __" + academicYear + "__" + semester.getLabel())
				.sessionType(sessionType)
				.schedules(new HashSet<>())
				.build())
				.toDTO();
	}

	@Override
	public TimetableDTO getTimetableById(String id) {
		return timetableRepository.findById(id)
				.map(Timetable::toDTO)
				.orElseThrow(() -> new CustomBusinessException("Timetable not found with id: " + id));
	}

	@Override
	public Page<TimetableDTO> getTimetablesByBranch(String academicYear,
			String branchId, SessionType sessionType, Pageable page) {
		return timetableRepository
				.findByAcademicYearAndSessionTypeAndUsedTrueAndLevelBranchId(academicYear,
						sessionType, branchId, page)
				.map(Timetable::toDTO);
	}

	@Override
	public TimetableDTO getTimetableByLevelAndSemester(String academicYear, String levelId, String semester,
			SessionType sessionType) {
		return timetableRepository
				.findByAcademicYearAndSemesterAndLevelIdAndSessionTypeAndUsedTrue(
						academicYear, Semester.valueOf(semester),
						levelId,
						sessionType)
				.map(Timetable::toDTO)
				.orElse(null);
	}

	@Override
	public List<String> getAllAcademicYears() {
		return academicYearRepository.findAll().stream()
				.map(AcademicYear::getLabel)
				.collect(Collectors.toList());
	}

	@Override
	public List<String> getSessionTypes() {
		return Arrays.stream(SessionType.values()).map(Enum::name).collect(Collectors.toList());
	}

	@Override
	public List<TimeSlotDTO> getExamTimeSlots() {
		return Arrays.stream(TimeSlot.ExamTimeSlot.values()).map(TimeSlotDTO::fromTimeSlot)
				.collect(Collectors.toList());
	}

	@Override
	public List<TimeSlotDTO> getCoursesTimeSlots() {
		return Arrays.stream(TimeSlot.CourseTimeSlot.values()).map(TimeSlotDTO::fromTimeSlot)
				.collect(Collectors.toList());
	}

	@Override
	public List<String> getSemestersForCourseTimetable() {
		return List.of(Semester.SEMESTER_1.name(), Semester.SEMESTER_2.name());
	}

	@Override
	public List<String> getSemestersForExamTimetable() {
		return Arrays.stream(Semester.values()).map(Enum::name).collect(Collectors.toList());
	}

	private boolean isExceededRoomCapacity(Long headCount) {
		if (headCount == null) {
			return false;
		}
		Long roomCapacity = roomRepository.findTopByOrderByCapacityDesc()
				.map(Room::getCapacity)
				.orElse(0L);
		return headCount > roomCapacity;
	}
}