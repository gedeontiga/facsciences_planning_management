// package
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

// import java.time.DayOfWeek;
// import java.time.LocalTime;
// import java.util.ArrayList;
// import java.util.List;

// import org.springframework.stereotype.Repository;

// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.SimpleScheduling;

// @Repository
// public interface SimpleSchedulingRepository extends
// SchedulingBaseRepository<SimpleScheduling, String> {

// List<SimpleScheduling> findByTeacherId(String teacherId);

// List<SimpleScheduling> findByDay(DayOfWeek day);

// List<SimpleScheduling>
// findByRoomIdAndDayAndStartTimeGreaterThanEqualAndStartTimeLessThan(
// String roomId, DayOfWeek day, LocalTime start, LocalTime end);

// List<SimpleScheduling>
// findByRoomIdAndDayAndEndTimeGreaterThanAndEndTimeLessThanEqual(
// String roomId, DayOfWeek day, LocalTime start, LocalTime end);

// List<SimpleScheduling>
// findByRoomIdAndDayAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
// String roomId, DayOfWeek day, LocalTime start, LocalTime end);

// default List<SimpleScheduling> findConflicts(String roomId, LocalTime
// startTime, LocalTime endTime,
// DayOfWeek day) {
// List<SimpleScheduling> conflicts = new ArrayList<>();

// conflicts.addAll(findByRoomIdAndDayAndStartTimeGreaterThanEqualAndStartTimeLessThan(
// roomId, day, startTime, endTime));

// conflicts.addAll(findByRoomIdAndDayAndEndTimeGreaterThanAndEndTimeLessThanEqual(
// roomId, day, startTime, endTime));

// conflicts.addAll(findByRoomIdAndDayAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
// roomId, day, startTime, endTime));

// return conflicts;
// }
// }