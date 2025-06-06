package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.ExamScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.Room;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.SimpleScheduling;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.ExamSchedulingRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.RoomRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.SimpleSchedulingRepository;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.RoomType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.AvailableRoomsRequestDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.RoomDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.exceptions.ResourceNotFoundException;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.RoomService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final ExamSchedulingRepository examSchedulingRepository;
    private final SimpleSchedulingRepository simpleSchedulingRepository;

    @Override
    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(Room::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoomDTO getRoomById(String id) {
        return roomRepository.findById(id)
                .map(Room::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
    }

    @Override
    public RoomDTO createRoom(RoomDTO roomDTO) {
        Room room = Room.builder()
                .code(roomDTO.code())
                .type(roomDTO.type())
                .capacity(roomDTO.capacity())
                .building(roomDTO.building())
                .availability(roomDTO.availability())
                .build();

        return roomRepository.save(room).toDTO();
    }

    @Override
    public RoomDTO updateRoom(String id, RoomDTO roomDTO) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));

        room.setCode(roomDTO.code());
        room.setType(roomDTO.type());
        room.setCapacity(roomDTO.capacity());
        room.setBuilding(roomDTO.building());
        room.setAvailability(roomDTO.availability());

        return roomRepository.save(room).toDTO();
    }

    @Override
    public void deleteRoom(String id) {
        roomRepository.deleteById(id);
    }

    @Override
    public List<RoomDTO> findAvailableRooms(AvailableRoomsRequestDTO request) {
        List<Room> allMatchingRooms = roomRepository.findByCapacityIsGreaterThanEqualAndTypeAndAvailabilityTrue(
                request.minimumCapacity(), request.roomType());
        return allMatchingRooms.stream()
                .filter(room -> isRoomAvailableInternal(room.getId(), request.startTime(), request.endTime(),
                        request.day(), request.date()))
                .map(Room::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomDTO> findRoomsByCapacity(Long minimumCapacity) {
        return roomRepository.findByCapacityIsGreaterThanEqualAndAvailabilityTrue(minimumCapacity).stream()
                .map(Room::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomDTO> findRoomsByType(RoomType type) {
        return roomRepository.findByTypeAndAvailabilityTrue(type).stream()
                .map(Room::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isRoomAvailable(String roomId, LocalTime startTime, LocalTime endTime, DayOfWeek day) {
        return isRoomAvailableInternal(roomId, startTime, endTime, day, null);
    }

    @Override
    public boolean isRoomAvailableForDate(String roomId, LocalTime startTime, LocalTime endTime, LocalDateTime date) {
        return isRoomAvailableInternal(roomId, startTime, endTime, null, date);
    }

    private boolean isRoomAvailableInternal(String roomId, LocalTime startTime, LocalTime endTime,
            DayOfWeek day, LocalDateTime date) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));

        if (!room.getAvailability()) {
            return false;
        }

        if (day != null) {
            List<SimpleScheduling> conflictingSchedules = simpleSchedulingRepository
                    .findConflicts(roomId, startTime, endTime, day);

            return conflictingSchedules.isEmpty();
        }

        if (date != null) {

            List<ExamScheduling> examConflicts = examSchedulingRepository
                    .findConflicts(roomId, startTime, endTime, date);

            if (!examConflicts.isEmpty()) {
                return false;
            }

            DayOfWeek dateDay = date.getDayOfWeek();
            List<SimpleScheduling> recurringConflicts = simpleSchedulingRepository
                    .findConflicts(roomId, startTime, endTime, dateDay);

            return recurringConflicts.isEmpty();
        }

        throw new IllegalArgumentException("Either day or date must be provided");
    }
}