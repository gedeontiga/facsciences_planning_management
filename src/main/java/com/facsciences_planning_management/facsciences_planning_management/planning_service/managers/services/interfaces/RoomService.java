package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.AvailableRoomsRequestDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.RoomDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.models.types.RoomType;

public interface RoomService {
    List<RoomDTO> getAllRooms();

    RoomDTO getRoomById(String id);

    RoomDTO createRoom(RoomDTO roomDTO);

    RoomDTO updateRoom(String id, RoomDTO roomDTO);

    void deleteRoom(String id);

    List<RoomDTO> findAvailableRooms(AvailableRoomsRequestDTO request);

    List<RoomDTO> findRoomsByCapacity(Long minimumCapacity);

    List<RoomDTO> findRoomsByType(RoomType type);

    boolean isRoomAvailable(String roomId, LocalTime startTime, LocalTime endTime, DayOfWeek day);

    boolean isRoomAvailableForDate(String roomId, LocalTime startTime, LocalTime endTime, LocalDateTime date);

}