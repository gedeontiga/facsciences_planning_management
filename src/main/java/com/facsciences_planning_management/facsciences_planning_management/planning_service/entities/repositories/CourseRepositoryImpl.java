package com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Course;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class CourseRepositoryImpl implements CourseRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public CourseRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Course> findByObsoleteFalseAndUeLevelId(String levelId) {
        // Check if the provided ID is a valid ObjectId string
        if (!ObjectId.isValid(levelId)) {
            return Collections.emptyList(); // Or throw an exception
        }

        TypedAggregation<Course> aggregation = Aggregation.newAggregation(Course.class,
                Aggregation.match(Criteria.where("obsolete").is(false)),
                Aggregation.lookup("ues", "ue", "_id", "joinedUe"),
                Aggregation.unwind("joinedUe"),
                Aggregation.lookup("levels", "joinedUe.level", "_id", "joinedLevel"),
                Aggregation.unwind("joinedLevel"),

                // --- THE FIX IS HERE ---
                // Match against a new ObjectId created from the input string
                Aggregation.match(Criteria.where("joinedLevel._id").is(new ObjectId(levelId))));

        AggregationResults<Course> results = mongoTemplate.aggregate(aggregation, "courses", Course.class);
        return results.getMappedResults();
    }
}
