package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.ExamScheduling;

import java.util.List;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.ArrayList;

import org.bson.types.ObjectId;
import java.util.Collections;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ExamSchedulingRepositoryImpl implements ExamSchedulingRepositoryCustom {

	private final MongoTemplate mongoTemplate;

	private static class CountResult {
		long total;

		public long getTotal() {
			return total;
		}
	}

	@Override
	public List<ExamScheduling> findByTimetableUsedTrueAndUeLevelId(String levelId) {
		if (!ObjectId.isValid(levelId)) {
			return Collections.emptyList();
		}

		TypedAggregation<ExamScheduling> aggregation = Aggregation.newAggregation(ExamScheduling.class,
				Aggregation.lookup("timetables", "timetable", "_id", "joinedTimetable"),
				Aggregation.unwind("$joinedTimetable"),
				Aggregation.match(Criteria.where("joinedTimetable.used").is(true)),
				Aggregation.lookup("ues", "ue", "_id", "joinedUe"),
				Aggregation.unwind("$joinedUe"),
				Aggregation.lookup("levels", "joinedUe.level", "_id", "joinedLevel"),
				Aggregation.unwind("$joinedLevel"),
				// THE FIX: Match against an ObjectId
				Aggregation.match(Criteria.where("joinedLevel._id").is(new ObjectId(levelId))),
				Aggregation
						.project("id", "proctor", "sessionDate", "ue", "timeSlot", "room", "timetable", "createdAt",
								"updatedAt")
						.and("_id").as("id"));

		return mongoTemplate.aggregate(aggregation, ExamScheduling.class).getMappedResults();
	}

	@Override
	public Page<ExamScheduling> findByTimetableUsedTrueAndUeLevelBranchId(String branchId, Pageable pageable) {
		if (!ObjectId.isValid(branchId)) {
			return Page.empty(pageable);
		}

		List<AggregationOperation> basePipeline = List.of(
				Aggregation.lookup("timetables", "timetable", "_id", "joinedTimetable"),
				Aggregation.unwind("$joinedTimetable"),
				Aggregation.match(Criteria.where("joinedTimetable.used").is(true)),
				Aggregation.lookup("ues", "ue", "_id", "joinedUe"),
				Aggregation.unwind("$joinedUe"),
				Aggregation.lookup("levels", "joinedUe.level", "_id", "joinedLevel"),
				Aggregation.unwind("$joinedLevel"),
				Aggregation.lookup("branches", "joinedLevel.branch", "_id", "joinedBranch"),
				Aggregation.unwind("$joinedBranch"),
				// THE FIX: Match against an ObjectId
				Aggregation.match(Criteria.where("joinedBranch._id").is(new ObjectId(branchId))));

		List<AggregationOperation> dataPipeline = new ArrayList<>(basePipeline);
		dataPipeline.add(Aggregation.sort(pageable.getSort()));
		dataPipeline.add(Aggregation.skip(pageable.getOffset()));
		dataPipeline.add(Aggregation.limit(pageable.getPageSize()));
		dataPipeline.add(Aggregation.project("id", "proctor", "sessionDate", "ue", "timeSlot", "room", "timetable",
				"createdAt", "updatedAt").and("_id").as("id"));

		TypedAggregation<ExamScheduling> dataAggregation = Aggregation.newAggregation(ExamScheduling.class,
				dataPipeline);
		List<ExamScheduling> content = mongoTemplate.aggregate(dataAggregation, ExamScheduling.class)
				.getMappedResults();

		List<AggregationOperation> countPipeline = new ArrayList<>(basePipeline);
		countPipeline.add(Aggregation.count().as("total"));
		TypedAggregation<CountResult> countAggregation = Aggregation.newAggregation(CountResult.class, countPipeline);

		return PageableExecutionUtils.getPage(content, pageable,
				() -> Optional
						.ofNullable(mongoTemplate.aggregate(countAggregation, "exam_schedules", CountResult.class)
								.getUniqueMappedResult())
						.map(CountResult::getTotal)
						.orElse(0L));
	}
}