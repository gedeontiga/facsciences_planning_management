package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facsciences_planning_management.facsciences_planning_management.entities.Teacher;
import com.facsciences_planning_management.facsciences_planning_management.entities.repositories.TeacherRepository;
import com.facsciences_planning_management.facsciences_planning_management.exceptions.CustomBusinessException;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Course;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Timetable;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Ue;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.CourseRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.TimetableRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.UeRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.CourseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
	private final CourseRepository courseRepository;
	private final UeRepository ueRepository;
	private final TeacherRepository teacherRepository;
	private final TimetableRepository timetableRepository;

	@Override
	public Page<CourseDTO> getAllCourses(Pageable page) {
		return courseRepository.findAllByObsoleteFalse(page)
				.map(Course::toDTO);
	}

	@Override
	@Transactional
	public CourseDTO createCourse(CourseRequest request) {
		log.info("Creating course: {}", request);
		Teacher teacher = teacherRepository.findById(request.teacherId())
				.orElseThrow(() -> new CustomBusinessException("User not found for this Id"));
		Ue ue = ueRepository.findById(request.ueId())
				.orElseThrow(() -> new CustomBusinessException("Ue not found for this Id"));

		if (ue.getAssigned()) {
			throw new CustomBusinessException("Ue is already assigned");
		}

		if (request.departmentId() == null) {
			throw new CustomBusinessException("Department id is required");
		}

		if (!teacher.getDepartmentId().equals(request.departmentId())) {
			throw new CustomBusinessException(
					"Teacher does not belong to the specified department: " + request.departmentId());
		}
		Course course = courseRepository.save(Course.builder()
				.teacher(teacher)
				.ue(ue)
				.duration(Duration.ofHours(request.duration()))
				.obsolete(false)
				.build());

		ue.setAssigned(true);
		ueRepository.save(ue);

		return course.toDTO();
	}

	// Soft update: Mark old as obsolete, create a new one
	@Override
	@Transactional
	public CourseDTO updateCourseTeacher(String courseId, String teacherId, String departmentId) {
		Teacher newTeacher = teacherRepository.findById(teacherId)
				.orElseThrow(() -> new CustomBusinessException("User not found with id: " + teacherId));
		Course oldCourse = courseRepository.findById(courseId)
				.orElseThrow(() -> new CustomBusinessException(
						"Course not found with id: " + courseId));

		if (!newTeacher.getDepartmentId().equals(departmentId)) {
			throw new CustomBusinessException(
					"Teacher does not belong to the specified department: " + departmentId);
		}

		oldCourse.setObsolete(true);
		courseRepository.save(oldCourse);

		Course newCourse = Course.builder()
				.teacher(newTeacher)
				.ue(oldCourse.getUe())
				.duration(oldCourse.getDuration())
				.obsolete(false)
				.build();

		return courseRepository.save(newCourse).toDTO();
	}

	// Soft update: Mark old as obsolete, create a new one
	@Override
	@Transactional
	public CourseDTO updateCourseUe(String courseId, String ueId) {
		Ue newUe = ueRepository.findById(ueId)
				.orElseThrow(() -> new CustomBusinessException("Ue not found with id: " + ueId));
		Course oldCourse = courseRepository.findById(courseId)
				.orElseThrow(() -> new CustomBusinessException(
						"Course not found with id: " + courseId));

		oldCourse.setObsolete(true);
		courseRepository.save(oldCourse);

		Course newCourse = Course.builder()
				.teacher(oldCourse.getTeacher())
				.ue(newUe)
				.duration(oldCourse.getDuration())
				.obsolete(false)
				.build();

		return courseRepository.save(newCourse).toDTO();
	}

	// Hard update for duration
	@Override
	public CourseDTO updateCourseDuration(String courseId, Long duration) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new CustomBusinessException(
						"Course not found with id: " + courseId));
		course.setDuration(Duration.ofHours(duration));
		return courseRepository.save(course).toDTO();
	}

	@Override
	public CourseDTO getCourse(String courseId) {
		return courseRepository.findById(courseId)
				.map(Course::toDTO)
				.orElseThrow(() -> new CustomBusinessException(
						"Course not found with id: " + courseId));
	}

	@Override
	public CourseDTO getCourseByUe(String ueId) {
		return courseRepository.findByObsoleteFalseAndUeId(ueId)
				.map(Course::toDTO)
				.orElseThrow(() -> new CustomBusinessException(
						"Active course not found for UE id: " + ueId));
	}

	@Override
	public List<CourseDTO> getCourseByLevel(String levelId) {
		return courseRepository.findByObsoleteFalseAndUeLevelId(levelId)
				.stream()
				.map(Course::toDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<CourseDTO> getUnscheduledCourses(String levelId, String timetableId) {
		// 1. Get the timetable and its scheduled UEs
		Timetable timetable = timetableRepository.findById(timetableId)
				.orElseThrow(() -> new CustomBusinessException(
						"Timetable not found with id: " + timetableId));
		// 2. Get all non-obsolete courses for the level
		List<Course> allCoursesForLevel = courseRepository.findByObsoleteFalseAndUeLevelIdAndUeLevelSemester(levelId,
				timetable.getSemester());

		// 3. Extract the UE IDs of the courses already scheduled in this timetable
		Set<String> scheduledUeIds = timetable.getSchedules().stream()
				.filter(schedule -> schedule instanceof CourseScheduling)
				.map(schedule -> ((CourseScheduling) schedule).getAssignedCourse().getUe().getId())
				.collect(Collectors.toSet());

		// 4. Filter to find courses whose UE is not yet scheduled
		return allCoursesForLevel.stream()
				.filter(course -> !scheduledUeIds.contains(course.getUe().getId()))
				.map(Course::toDTO)
				.collect(Collectors.toList());
	}

	@Override
	public void deleteCourse(String courseId) {
		// This is a hard delete. Alternatively, could also be implemented as setting
		// obsolete=true.
		if (!courseRepository.existsById(courseId)) {
			throw new CustomBusinessException("Course not found with id: " + courseId);
		}
		courseRepository.deleteById(courseId);
	}
}