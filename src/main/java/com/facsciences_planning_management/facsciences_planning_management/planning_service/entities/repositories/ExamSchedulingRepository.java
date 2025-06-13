// package
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories;

// import org.springframework.stereotype.Repository;

// import
// com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.ExamScheduling;

// import java.util.ArrayList;
// import java.util.List;
// import java.time.LocalDateTime;
// import java.time.LocalTime;

// @Repository
// public interface ExamSchedulingRepository extends
// SchedulingBaseRepository<ExamScheduling, String> {

// List<ExamScheduling> findByProctorId(String proctorId);

// List<ExamScheduling> findBySessionDateBetween(LocalDateTime start,
// LocalDateTime end);

// List<ExamScheduling>
// findByRoomIdAndSessionDateGreaterThanEqualAndSessionDateLessThanAndStartTimeGreaterThanEqualAndStartTimeLessThan(
// String roomId, LocalDateTime startDateTime, LocalDateTime endDateTime,
// LocalTime start,
// LocalTime end);

// List<ExamScheduling>
// findByRoomIdAndSessionDateGreaterThanEqualAndSessionDateLessThanAndEndTimeGreaterThanAndEndTimeLessThanEqual(
// String roomId, LocalDateTime startDateTime, LocalDateTime endDateTime,
// LocalTime start,
// LocalTime end);

// List<ExamScheduling>
// findByRoomIdAndSessionDateGreaterThanEqualAndSessionDateLessThanAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
// String roomId, LocalDateTime startDateTime, LocalDateTime endDateTime,
// LocalTime start,
// LocalTime end);

// default List<ExamScheduling> findConflicts(
// String roomId,
// LocalTime startTime,
// LocalTime endTime,
// LocalDateTime sessionDateStart,
// LocalDateTime sessionDateEnd) {

// List<ExamScheduling> conflicts = new ArrayList<>();

// conflicts.addAll(
// findByRoomIdAndSessionDateGreaterThanEqualAndSessionDateLessThanAndStartTimeGreaterThanEqualAndStartTimeLessThan(
// roomId, sessionDateStart, sessionDateEnd, startTime, endTime));

// conflicts.addAll(
// findByRoomIdAndSessionDateGreaterThanEqualAndSessionDateLessThanAndEndTimeGreaterThanAndEndTimeLessThanEqual(
// roomId, sessionDateStart, sessionDateEnd, startTime, endTime));

// conflicts.addAll(
// findByRoomIdAndSessionDateGreaterThanEqualAndSessionDateLessThanAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
// roomId, sessionDateStart, sessionDateEnd, startTime, endTime));

// return conflicts;
// }

// default List<ExamScheduling> findConflicts(
// String roomId,
// LocalTime startTime,
// LocalTime endTime,
// LocalDateTime sessionDate) {
// LocalDateTime startOfDay = sessionDate.toLocalDate().atStartOfDay();
// LocalDateTime endOfDay = startOfDay.plusDays(1);
// return findConflicts(roomId, startTime, endTime, startOfDay, endOfDay);
// }
// }
