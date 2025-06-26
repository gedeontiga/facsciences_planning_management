package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.RoomDTO;

public interface RoomService {
    Page<RoomDTO> getAllRooms(Pageable page);

    RoomDTO getRoomById(String id);

    RoomDTO createRoom(RoomDTO roomDTO);

    RoomDTO updateRoom(String id, RoomDTO roomDTO);

    void updateRoomAvailability(String id);

    void deleteRoom(String id);

    List<RoomDTO> getRoomsByCapacity(Long minimumCapacity);

    List<RoomDTO> getRoomsByType(String type);

}