// package
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

// import org.bson.types.ObjectId;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.mongodb.core.MongoTemplate;
// import org.springframework.data.mongodb.core.aggregation.Aggregation;
// import
// org.springframework.data.mongodb.core.aggregation.AggregationOperation;
// import org.springframework.data.mongodb.core.aggregation.AggregationResults;
// import org.springframework.data.mongodb.core.query.Criteria;
// import org.springframework.data.support.PageableExecutionUtils;

// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Course;
// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.Semester;

// import lombok.extern.slf4j.Slf4j;

// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.List;

// import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
// import org.springframework.stereotype.Repository;

// @Slf4j
// @Repository
// public class CourseRepositoryImpl implements CourseRepositoryCustom {

// private final MongoTemplate mongoTemplate;

// public CourseRepositoryImpl(MongoTemplate mongoTemplate) {
// this.mongoTemplate = mongoTemplate;
// }

// @Override
// public Page<Course> findByObsoleteFalseAndUeLevelId(String levelId, Pageable
// page) {
// // Check if the provided ID is a valid ObjectId string
// if (!ObjectId.isValid(levelId)) {
// return Page.empty(page);
// }

// // Build the base aggregation pipeline
// List<AggregationOperation> operations = Arrays.asList(
// Aggregation.match(Criteria.where("obsolete").is(false)),
// Aggregation.lookup("ues", "ue", "_id", "joinedUe"),
// Aggregation.unwind("joinedUe"),
// Aggregation.lookup("levels", "joinedUe.level", "_id", "joinedLevel"),
// Aggregation.unwind("joinedLevel"),
// Aggregation.match(Criteria.where("joinedLevel._id").is(new
// ObjectId(levelId))));

// // Count total documents
// List<AggregationOperation> countOperations = new ArrayList<>(operations);
// countOperations.add(Aggregation.count().as("total"));

// TypedAggregation<Course> countAggregation =
// Aggregation.newAggregation(Course.class, countOperations);
// AggregationResults<org.bson.Document> countResults =
// mongoTemplate.aggregate(countAggregation, "courses",
// org.bson.Document.class);

// long total = countResults.getMappedResults().isEmpty() ? 0
// : countResults.getMappedResults().get(0).getInteger("total", 0);

// // Add pagination to main aggregation
// List<AggregationOperation> paginatedOperations = new ArrayList<>(operations);
// paginatedOperations.add(Aggregation.skip(page.getOffset()));
// paginatedOperations.add(Aggregation.limit(page.getPageSize()));

// TypedAggregation<Course> aggregation =
// Aggregation.newAggregation(Course.class, paginatedOperations);
// AggregationResults<Course> results = mongoTemplate.aggregate(aggregation,
// "courses", Course.class);

// return PageableExecutionUtils.getPage(results.getMappedResults(), page, () ->
// total);
// }

// @Override
// public List<Course> findByObsoleteFalseAndLevelIdAndSemester(String levelId,
// Semester semester) {
// // Check if the provided ID is a valid ObjectId string
// if (!ObjectId.isValid(levelId)) {
// return Collections.emptyList(); // Or throw an exception
// }

// TypedAggregation<Course> aggregation =
// Aggregation.newAggregation(Course.class,
// Aggregation.match(Criteria.where("obsolete").is(false)),
// Aggregation.lookup("ues", "ue", "_id", "joinedUe"),
// Aggregation.unwind("joinedUe"),
// Aggregation.lookup("levels", "joinedUe.level", "_id", "joinedLevel"),
// Aggregation.unwind("joinedLevel"),

// // --- THE FIX IS HERE ---
// // Match against a new ObjectId created from the input string
// Aggregation.match(Criteria.where("joinedLevel._id").is(new
// ObjectId(levelId)).and("joinedUe.semester")
// .is(semester)));

// AggregationResults<Course> results = mongoTemplate.aggregate(aggregation,
// "courses", Course.class);
// return results.getMappedResults();
// }
// }
