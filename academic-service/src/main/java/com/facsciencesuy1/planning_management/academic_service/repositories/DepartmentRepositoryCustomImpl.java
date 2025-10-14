package com.facsciencesuy1.planning_management.academic_service.repositories;

import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import com.facsciencesuy1.planning_management.entities.Department;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DepartmentRepositoryCustomImpl implements DepartmentRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public List<Department> findByBranchFacultyId(String facultyId) {

        if (!ObjectId.isValid(facultyId)) {
            return Collections.emptyList();
        }

        TypedAggregation<Department> aggregation = Aggregation.newAggregation(Department.class,
                Aggregation.lookup("branches", "branch", "_id", "joinedBranch"),
                Aggregation.unwind("joinedBranch"),
                Aggregation.lookup("faculties", "joinedBranch.faculty", "_id", "joinedFaculty"),
                Aggregation.unwind("joinedFaculty"),

                Aggregation.match(Criteria.where("joinedFaculty._id").is(new ObjectId(facultyId))));

        AggregationResults<Department> results = mongoTemplate.aggregate(aggregation, "departments", Department.class);
        return results.getMappedResults();
    }
}
