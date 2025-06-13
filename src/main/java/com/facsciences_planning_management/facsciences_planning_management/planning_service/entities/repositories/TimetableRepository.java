// package
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

// import java.time.Year;

// import org.springframework.data.mongodb.repository.MongoRepository;
// import org.springframework.stereotype.Repository;

// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Timetable;

// import java.util.List;

// @Repository
// public interface TimetableRepository extends MongoRepository<Timetable,
// String> {
// List<Timetable> findByAcademicYearAndSemester(Year academicYear, String
// semester);
// }