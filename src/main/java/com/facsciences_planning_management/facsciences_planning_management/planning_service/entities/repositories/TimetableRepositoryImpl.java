package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Timetable;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.SessionType;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId; // <-- IMPORTANT
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TimetableRepositoryImpl implements TimetableRepositoryCustom {

	private final MongoTemplate mongoTemplate;

	private static class CountResult {
		long total;

		public long getTotal() {
			return total;
		}
	}

	@Override
	public Page<Timetable> findByAcademicYearAndSessionTypeAndUsedTrueAndLevelBranchId(
			String academicYear, SessionType sessionType, String branchId, Pageable pageable) {

		if (!ObjectId.isValid(branchId)) {
			return Page.empty(pageable);
		}

		List<AggregationOperation> basePipeline = List.of(
				Aggregation.match(
						Criteria.where("academicYear").is(academicYear)
								.and("sessionType").is(sessionType)
								.and("used").is(true)),
				Aggregation.lookup("levels", "level", "_id", "joinedLevel"),
				Aggregation.unwind("$joinedLevel"),
				Aggregation.lookup("branches", "joinedLevel.branch", "_id", "joinedBranch"),
				Aggregation.unwind("$joinedBranch"),
				// THE FIX: Match against an ObjectId
				Aggregation.match(Criteria.where("joinedBranch._id").is(new ObjectId(branchId))));

		List<AggregationOperation> dataPipeline = new ArrayList<>(basePipeline);
		dataPipeline.add(Aggregation.sort(pageable.getSort()));
		dataPipeline.add(Aggregation.skip(pageable.getOffset()));
		dataPipeline.add(Aggregation.limit(pageable.getPageSize()));
		// dataPipeline.add(Aggregation
		// .project("id", "academicYear", "semester", "sessionType", "used", "level",
		// "createdAt", "updatedAt")
		// .and("_id").as("id"));

		TypedAggregation<Timetable> dataAggregation = Aggregation.newAggregation(Timetable.class, dataPipeline);
		List<Timetable> content = mongoTemplate.aggregate(dataAggregation, Timetable.class).getMappedResults();

		List<AggregationOperation> countPipeline = new ArrayList<>(basePipeline);
		countPipeline.add(Aggregation.count().as("total"));
		TypedAggregation<CountResult> countAggregation = Aggregation.newAggregation(CountResult.class,
				countPipeline);

		return PageableExecutionUtils.getPage(content, pageable,
				() -> Optional
						.ofNullable(mongoTemplate
								.aggregate(countAggregation, "timetables",
										CountResult.class)
								.getUniqueMappedResult())
						.map(CountResult::getTotal)
						.orElse(0L));
	}
}