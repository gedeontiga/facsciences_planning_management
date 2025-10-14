package com.facsciencesuy1.planning_management.planning_service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facsciencesuy1.planning_management.components.JwtsHelper;
import com.facsciencesuy1.planning_management.dtos.TimetableDTO;
import com.facsciencesuy1.planning_management.entities.AcademicYear;
import com.facsciencesuy1.planning_management.entities.Course;
import com.facsciencesuy1.planning_management.entities.CourseScheduling;
import com.facsciencesuy1.planning_management.entities.ExamScheduling;
import com.facsciencesuy1.planning_management.entities.Level;
import com.facsciencesuy1.planning_management.entities.Room;
import com.facsciencesuy1.planning_management.entities.Scheduling;
import com.facsciencesuy1.planning_management.entities.Teacher;
import com.facsciencesuy1.planning_management.entities.Timetable;
import com.facsciencesuy1.planning_management.entities.types.RoleType;
import com.facsciencesuy1.planning_management.entities.types.Semester;
import com.facsciencesuy1.planning_management.entities.types.SessionType;
import com.facsciencesuy1.planning_management.entities.types.TimeSlot;
import com.facsciencesuy1.planning_management.entities.types.TimeSlot.CourseTimeSlot;
import com.facsciencesuy1.planning_management.exceptions.CustomBusinessException;
import com.facsciencesuy1.planning_management.planning_service.repositories.AcademicYearRepository;
import com.facsciencesuy1.planning_management.planning_service.repositories.CourseRepository;
import com.facsciencesuy1.planning_management.planning_service.repositories.CourseSchedulingRepository;
import com.facsciencesuy1.planning_management.planning_service.repositories.DepartmentRepository;
import com.facsciencesuy1.planning_management.planning_service.repositories.ExamSchedulingRepository;
import com.facsciencesuy1.planning_management.planning_service.repositories.LevelRepository;
import com.facsciencesuy1.planning_management.planning_service.repositories.RoomRepository;
import com.facsciencesuy1.planning_management.planning_service.repositories.TeacherRepository;
import com.facsciencesuy1.planning_management.planning_service.repositories.TimetableRepository;
import com.facsciencesuy1.planning_management.planning_service.services.interfaces.TimetableService;
import com.facsciencesuy1.planning_management.planning_service.utils.dtos.TimeSlotDTO;

import java.time.DayOfWeek;
import java.util.ArrayList;
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

	private final ExamSchedulingRepository examSchedulingRepository;
	private final AcademicYearRepository academicYearRepository;
	private final TimetableRepository timetableRepository;
	private final TimetableSolverService timetableSolverService;
	private final CourseRepository courseRepository;
	private final DepartmentRepository departmentRepository;
	private final TeacherRepository teacherRepository;
	private final RoomRepository roomRepository;
	private final LevelRepository levelRepository;
	private final CourseSchedulingRepository courseSchedulingRepository;
	private final JwtsHelper jwtsHelper;

	@Override
	@Transactional
	public TimetableDTO generateTimetableForLevel(String academicYear, Semester semester, String levelId) {
		Level level = levelRepository.findById(levelId)
				.orElseThrow(() -> new CustomBusinessException("Level not found with id: " + levelId));

		Long headCount = level.getHeadCount();
		// if (isExceededRoomCapacity(headCount)) {
		// throw new CustomBusinessException(
		// "Cannot generate timetable for Level '" + level.getCode() + "' due to room
		// capacity limit.");

		// }

		// 2. Get all available rooms.
		List<Room> availableRooms = roomRepository
				.findByCapacityIsGreaterThanEqualOrderByCapacityAsc(headCount);
		if (availableRooms.isEmpty()) {
			throw new IllegalStateException("No available rooms in the system for this head count:" + headCount);
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
		List<Course> coursesToSchedule = courseRepository.findByObsoleteFalseAndLevelIdAndSemester(levelId,
				semester);
		if (coursesToSchedule.isEmpty()) {
			throw new CustomBusinessException("No active courses found for level " + level.getName() + " to schedule.");
		}

		// 3. Get ALL existing, active course schedules from the ENTIRE system.
		// This is the crucial step to handle resource conflicts across different
		// levels.
		List<CourseScheduling> existingSchedules = courseSchedulingRepository
				.findByHeadCountIsGreaterThanEqualAndActiveIsTrue(headCount);

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
				.branchId(level.getBranch().getId())
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
				.branchId(level.getBranch().getId())
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
				.branchId(level.getBranch().getId())
				.name("Exam Planning for " + level.getCode())
				.description("Timetable for " + level.getName() + " __" + academicYear + "__ " + semester.getLabel())
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
	public Page<TimetableDTO> getTimetableForCurrentUser(Pageable page, SessionType sessionType) {
		RoleType role = RoleType.valueOf(jwtsHelper.getRoleFromToken());

		switch (role) {
			case STUDENT -> {
				return timetableRepository.findByLevelIdAndSessionTypeAndUsedTrue(jwtsHelper.getMetadataFromToken(),
						sessionType, page)
						.map(Timetable::toDTO);
			}
			case DEPARTMENT_HEAD -> {
				String branchId = departmentRepository.findById(jwtsHelper.getMetadataFromToken())
						.orElseThrow(() -> new CustomBusinessException("Department not found for this id")).getBranch()
						.getId();
				return timetableRepository.findByBranchIdAndSessionTypeAndUsedTrue(branchId,
						sessionType, page)
						.map(Timetable::toDTO);
			}
			case TEACHER -> {
				Teacher teacher = teacherRepository.findByEmail(jwtsHelper.getEmailFromToken())
						.orElseThrow(() -> new CustomBusinessException("Teacher not found for this id"));
				String branchId = departmentRepository.findById(teacher.getDepartmentId())
						.orElseThrow(() -> new CustomBusinessException("Department not found for this id")).getBranch()
						.getId();
				Page<Timetable> timetables = timetableRepository.findByBranchIdAndSessionTypeAndUsedTrue(branchId,
						sessionType, page);

				List<TimetableDTO> timetableDTOs = new ArrayList<>();

				timetables.getContent().stream()
						.filter(timetable1 -> timetable1.getSchedules().stream()
								.anyMatch(scheduling -> isTeacherPresent(scheduling, teacher.getId())))
						.forEach(timetable1 -> timetableDTOs.add(timetable1.toDTO()));

				return new PageImpl<>(timetableDTOs, timetables.getPageable(), timetables.getTotalElements());
			}

			default -> {
				return timetableRepository.findBySessionTypeAndUsedTrue(sessionType, page)
						.map(Timetable::toDTO);
			}
		}
	}

	@Override
	public Page<TimetableDTO> getTimetableForCurrentUser(Pageable page, SessionType sessionType, Semester semester) {
		RoleType role = RoleType.valueOf(jwtsHelper.getRoleFromToken());

		switch (role) {
			case STUDENT -> {
				return timetableRepository
						.findByLevelIdAndSemesterAndSessionTypeAndUsedTrue(jwtsHelper.getMetadataFromToken(),
								semester,
								sessionType, page)
						.map(Timetable::toDTO);
			}
			case DEPARTMENT_HEAD -> {
				String branchId = departmentRepository.findById(jwtsHelper.getMetadataFromToken())
						.orElseThrow(() -> new CustomBusinessException("Department not found for this id")).getBranch()
						.getId();
				return timetableRepository.findByBranchIdAndSemesterAndSessionTypeAndUsedTrue(branchId,
						semester,
						sessionType, page)
						.map(Timetable::toDTO);
			}
			case TEACHER -> {
				Teacher teacher = teacherRepository.findByEmail(jwtsHelper.getEmailFromToken())
						.orElseThrow(() -> new CustomBusinessException("Teacher not found for this id"));
				String branchId = departmentRepository.findById(teacher.getDepartmentId())
						.orElseThrow(() -> new CustomBusinessException("Department not found for this id")).getBranch()
						.getId();
				Page<Timetable> timetables = timetableRepository.findByBranchIdAndSemesterAndSessionTypeAndUsedTrue(
						branchId,
						semester,
						sessionType, page);

				List<TimetableDTO> timetableDTOs = new ArrayList<>();

				timetables.getContent().stream()
						.filter(timetable1 -> timetable1.getSchedules().stream()
								.anyMatch(scheduling -> isTeacherPresent(scheduling, teacher.getId())))
						.forEach(timetable1 -> timetableDTOs.add(timetable1.toDTO()));

				return new PageImpl<>(timetableDTOs, timetables.getPageable(), timetables.getTotalElements());
			}
			default -> {
				return timetableRepository.findBySemesterAndSessionTypeAndUsedTrue(semester, sessionType, page)
						.map(Timetable::toDTO);
			}
		}
	}

	private boolean isTeacherPresent(Scheduling scheduling, String teacherId) {
		if (scheduling instanceof CourseScheduling) {
			CourseScheduling courseScheduling = ((CourseScheduling) scheduling);
			return courseScheduling.getAssignedCourse().getTeacher().getId().equals(teacherId);
		} else if (scheduling instanceof ExamScheduling) {
			ExamScheduling examScheduling = ((ExamScheduling) scheduling);
			return examScheduling.getProctor().getId().equals(teacherId);
		}
		return false;
	}

	@Override
	public Page<TimetableDTO> getTimetablesByBranch(String academicYear, Semester semester,
			String branchId, SessionType sessionType, Pageable page) {
		return timetableRepository
				.findByAcademicYearAndSemesterAndSessionTypeAndUsedTrueAndBranchId(academicYear,
						semester,
						sessionType, branchId, page)
				.map(Timetable::toDTO);
	}

	@Override
	public TimetableDTO getTimetableByLevelAndSemester(String academicYear, String levelId, Semester semester,
			SessionType sessionType) {
		return timetableRepository
				.findByAcademicYearAndSemesterAndLevelIdAndSessionTypeAndUsedTrue(
						academicYear, semester,
						levelId,
						sessionType)
				.map(Timetable::toDTO)
				.orElse(null);
	}

	@Override
	public void deleteTimetable(String id) {
		timetableRepository.findById(id)
				.ifPresentOrElse(timetable -> {
					timetable.setUsed(false);
					timetable.getSchedules().forEach(scheduling -> {
						scheduling.setActive(false);
						if (scheduling instanceof CourseScheduling) {
							CourseScheduling courseScheduling = ((CourseScheduling) scheduling);
							Course course = courseScheduling.getAssignedCourse();
							course.setObsolete(true);
							courseRepository.save(course);
							courseSchedulingRepository.save(courseScheduling);
						} else if (scheduling instanceof ExamScheduling) {
							examSchedulingRepository.save((ExamScheduling) scheduling);
						}
					});
					timetableRepository.save(timetable);
				}, () -> new CustomBusinessException("Timetable not found with id: " + id));
	}

	public List<String> getAllAcademicYears() {
		return academicYearRepository.findAll().stream()
				.map(AcademicYear::getLabel)
				.collect(Collectors.toList());
	}

	public List<String> getSessionTypes() {
		return Arrays.stream(SessionType.values()).map(Enum::name).collect(Collectors.toList());
	}

	public List<TimeSlotDTO> getExamTimeSlots() {
		return Arrays.stream(TimeSlot.ExamTimeSlot.values()).map(TimeSlotDTO::fromTimeSlot)
				.collect(Collectors.toList());
	}

	public List<TimeSlotDTO> getCoursesTimeSlots() {
		return Arrays.stream(TimeSlot.CourseTimeSlot.values()).map(TimeSlotDTO::fromTimeSlot)
				.collect(Collectors.toList());
	}

	public List<String> getSemestersForCourseTimetable() {
		return List.of(Semester.SEMESTER_1.name(), Semester.SEMESTER_2.name());
	}

	public List<String> getSemestersForExamTimetable() {
		return Arrays.stream(Semester.values()).map(Enum::name).collect(Collectors.toList());
	}

	// private boolean isExceededRoomCapacity(Long headCount) {
	// if (headCount == null) {
	// return true;
	// }
	// Long roomCapacity = roomRepository.findTopByOrderByCapacityDesc()
	// .map(Room::getCapacity)
	// .orElse(0L);
	// return headCount > roomCapacity;
	// }
}