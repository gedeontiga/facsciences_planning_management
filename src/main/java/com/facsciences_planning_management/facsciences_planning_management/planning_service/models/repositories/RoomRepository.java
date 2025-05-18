package com.facsciences_planning_management.facsciences_planning_management.planning_service.models.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.Room;
import java.util.List;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.types.RoomType;

@Repository
public interface RoomRepository extends MongoRepository<Room, String> {
    List<Room> findByCapacityIsGreaterThanEqualAndAvailabilityTrue(Long capacity);

    List<Room> findByTypeAndAvailabilityTrue(RoomType type);

    List<Room> findByCapacityIsGreaterThanEqualAndTypeAndAvailabilityTrue(Long capacity, RoomType type);
}
