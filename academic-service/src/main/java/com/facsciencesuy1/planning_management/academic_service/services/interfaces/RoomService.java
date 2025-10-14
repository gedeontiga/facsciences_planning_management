package com.facsciencesuy1.planning_management.academic_service.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.facsciencesuy1.planning_management.academic_service.utils.dtos.RoomRequest;
import com.facsciencesuy1.planning_management.dtos.RoomDTO;

public interface RoomService {
    Page<RoomDTO> getAllRooms(Pageable page);

    RoomDTO getRoomById(String id);

    RoomDTO createRoom(RoomRequest roomDTO);

    RoomDTO updateRoom(String id, RoomRequest roomDTO);

    void updateRoomAvailability(String id);

    void deleteRoom(String id);

    List<RoomDTO> getRoomsByCapacity(Long minimumCapacity);

    List<RoomDTO> getRoomsByType(String type);

    List<String> getAllRoomTypes();

}