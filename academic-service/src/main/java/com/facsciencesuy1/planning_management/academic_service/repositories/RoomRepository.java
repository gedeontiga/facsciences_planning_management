package com.facsciencesuy1.planning_management.academic_service.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.Room;
import com.facsciencesuy1.planning_management.entities.types.RoomType;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends MongoRepository<Room, String> {
    boolean existsByCode(String code);

    List<Room> findAllByAvailabilityTrue();

    List<Room> findByCapacityIsGreaterThanEqualOrderByCapacityAsc(Long capacity);

    Page<Room> findAllByAvailabilityTrue(Pageable page);

    List<Room> findByCapacityIsGreaterThanEqual(Long capacity);

    Optional<Room> findByCode(String code);

    // List<Room> findByCapacityIsGreaterThanEqualAndAvailabilityIsTrue(Long
    // headCount);

    // Optional<Room> findTopByOrderByCapacityDesc();

    List<Room> findByType(RoomType type);

    List<Room> findByCapacityIsGreaterThanEqualAndType(Long capacity, RoomType type);
}
