package com.facsciencesuy1.planning_management.api_gateway.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.facsciencesuy1.planning_management.entities.Room;

import java.util.Optional;

@Repository
public interface RoomRepository extends MongoRepository<Room, String> {
    boolean existsByCode(String code);

    Optional<Room> findByCode(String code);
}
