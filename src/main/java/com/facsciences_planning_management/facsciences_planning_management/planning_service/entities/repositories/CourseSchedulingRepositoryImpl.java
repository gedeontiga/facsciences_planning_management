package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.CourseScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.TimeSlot;

import lombok.RequiredArgsConstructor;

import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.ArrayList;

import org.bson.types.ObjectId;
import java.util.Collections;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CourseSchedulingRepositoryImpl implements CourseSchedulingRepositoryCustom {

	private final MongoTemplate mongoTemplate;

	// Helper class for count result
	private static class CountResult {
		long total;

		public long getTotal() {
			return total;
		}
	}

	@Override
	public List<CourseScheduling> findByTimetableUsedTrue() {
		TypedAggregation<CourseScheduling> aggregation = Aggregation.newAggregation(CourseScheduling.class,
				Aggregation.lookup("timetables", "timetable", "_id", "joinedTimetable"),
				Aggregation.unwind("$joinedTimetable"),
				Aggregation.match(Criteria.where("joinedTimetable.used").is(true))
		// Consider projecting only necessary fields if performance is an issue
		);
		return mongoTemplate.aggregate(aggregation, CourseScheduling.class).getMappedResults();
	}

	@Override
	public List<CourseScheduling> findByAssignedCourseObsoleteFalseAndAssignedCourseUeLevelIdAndDayAndTimeSlot(
			String levelId, DayOfWeek day, TimeSlot.CourseTimeSlot timeSlot) {
		if (!ObjectId.isValid(levelId)) {
			return Collections.emptyList();
		}

		TypedAggregation<CourseScheduling> aggregation = Aggregation.newAggregation(CourseScheduling.class,
				// First, match on the fields in the root document for efficiency
				Aggregation.match(Criteria.where("day").is(day).and("timeSlot").is(timeSlot)),
				// Then perform the joins and filtering on related collections
				Aggregation.lookup("courses", "assignedCourse", "_id", "joinedCourse"),
				Aggregation.unwind("joinedCourse"),
				Aggregation.match(Criteria.where("joinedCourse.obsolete").is(false)),
				Aggregation.lookup("ues", "joinedCourse.ue", "_id", "joinedUe"),
				Aggregation.unwind("joinedUe"),
				Aggregation.match(Criteria.where("joinedUe.level").is(new ObjectId(levelId))),
				Aggregation.project("id", "day", "assignedCourse", "timeSlot", "room", "timetable", "createdAt",
						"updatedAt").and("_id").as("id"));

		return mongoTemplate.aggregate(aggregation, CourseScheduling.class).getMappedResults();
	}

	@Override
	public List<CourseScheduling> findByAssignedCourseObsoleteFalseAndAssignedCourseUeLevelIdAndDay(
			String levelId, DayOfWeek day) {
		if (!ObjectId.isValid(levelId)) {
			return Collections.emptyList();
		}

		TypedAggregation<CourseScheduling> aggregation = Aggregation.newAggregation(CourseScheduling.class,
				Aggregation.match(Criteria.where("day").is(day)),
				Aggregation.lookup("courses", "assignedCourse", "_id", "joinedCourse"),
				Aggregation.unwind("joinedCourse"),
				Aggregation.match(Criteria.where("joinedCourse.obsolete").is(false)),
				Aggregation.lookup("ues", "joinedCourse.ue", "_id", "joinedUe"),
				Aggregation.unwind("joinedUe"),
				Aggregation.match(Criteria.where("joinedUe.level").is(new ObjectId(levelId)))
		// No projection needed if you just need the timeSlot, but returning the full
		// object is more consistent.
		);

		return mongoTemplate.aggregate(aggregation, CourseScheduling.class).getMappedResults();
	}

	@Override
	public Page<CourseScheduling> findByAssignedCourseObsoleteFalseAndAssignedCourseUeLevelBranchId(
			String branchId, Pageable pageable) {
		if (!ObjectId.isValid(branchId)) {
			return Page.empty(pageable);
		}

		// Base pipeline for reuse in both data and count queries
		List<AggregationOperation> basePipeline = List.of(
				Aggregation.lookup("courses", "assignedCourse", "_id", "joinedCourse"),
				Aggregation.unwind("joinedCourse"),
				Aggregation.match(Criteria.where("joinedCourse.obsolete").is(false)),
				Aggregation.lookup("ues", "joinedCourse.ue", "_id", "joinedUe"),
				Aggregation.unwind("joinedUe"),
				Aggregation.lookup("levels", "joinedUe.level", "_id", "joinedLevel"),
				Aggregation.unwind("joinedLevel"),
				Aggregation.lookup("branches", "joinedLevel.branch", "_id", "joinedBranch"),
				Aggregation.unwind("joinedBranch"),
				// THE FIX: Match against an ObjectId
				Aggregation.match(Criteria.where("joinedBranch._id").is(new ObjectId(branchId))));

		// Aggregation for fetching the page content
		List<AggregationOperation> dataPipeline = new ArrayList<>(basePipeline);
		dataPipeline.add(Aggregation.sort(pageable.getSort()));
		dataPipeline.add(Aggregation.skip(pageable.getOffset()));
		dataPipeline.add(Aggregation.limit(pageable.getPageSize()));
		dataPipeline.add(Aggregation
				.project("id", "day", "assignedCourse", "timeSlot", "room", "timetable", "createdAt", "updatedAt")
				.and("_id").as("id"));

		TypedAggregation<CourseScheduling> dataAggregation = Aggregation.newAggregation(CourseScheduling.class,
				dataPipeline);
		List<CourseScheduling> content = mongoTemplate.aggregate(dataAggregation, CourseScheduling.class)
				.getMappedResults();

		List<AggregationOperation> countPipeline = new ArrayList<>(basePipeline);
		countPipeline.add(Aggregation.count().as("total"));
		TypedAggregation<CountResult> countAggregation = Aggregation.newAggregation(CountResult.class, countPipeline);

		return PageableExecutionUtils.getPage(content, pageable,
				() -> Optional
						.ofNullable(mongoTemplate.aggregate(countAggregation, "course_schedules", CountResult.class)
								.getUniqueMappedResult())
						.map(CountResult::getTotal)
						.orElse(0L));
	}

	@Override
	public List<CourseScheduling> findByAssignedCourseObsoleteFalseAndAssignedCourseUeLevelIdAndTimeSlot(
			String levelId, TimeSlot.CourseTimeSlot timeSlot) {
		if (!ObjectId.isValid(levelId)) {
			return Collections.emptyList();
		}

		TypedAggregation<CourseScheduling> aggregation = Aggregation.newAggregation(CourseScheduling.class,
				Aggregation.match(Criteria.where("timeSlot").is(timeSlot)),
				Aggregation.lookup("courses", "assignedCourse", "_id", "joinedCourse"),
				Aggregation.unwind("joinedCourse"),
				Aggregation.match(Criteria.where("joinedCourse.obsolete").is(false)),
				Aggregation.lookup("ues", "joinedCourse.ue", "_id", "joinedUe"),
				Aggregation.unwind("joinedUe"),
				Aggregation.match(Criteria.where("joinedUe.level").is(new ObjectId(levelId))));

		return mongoTemplate.aggregate(aggregation, CourseScheduling.class).getMappedResults();
	}
}