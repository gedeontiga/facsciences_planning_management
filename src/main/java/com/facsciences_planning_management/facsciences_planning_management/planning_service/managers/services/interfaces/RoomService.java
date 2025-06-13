package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces;

import java.util.List;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.RoomDTO;

public interface RoomService {
    List<RoomDTO> getAllRooms();

    RoomDTO getRoomById(String id);

    RoomDTO createRoom(RoomDTO roomDTO);

    RoomDTO updateRoom(String id, RoomDTO roomDTO);

    void deleteRoom(String id);

    // List<RoomDTO> findAvailableRooms(AvailableRoomsRequestDTO request);

    List<RoomDTO> getRoomsByCapacity(Long minimumCapacity);

    List<RoomDTO> getRoomsByType(String type);

    // boolean isRoomAvailable(String roomId, LocalTime startTime, LocalTime
    // endTime, DayOfWeek day);

    // boolean isRoomAvailableForDate(String roomId, LocalTime startTime, LocalTime
    // endTime, LocalDateTime date);

}