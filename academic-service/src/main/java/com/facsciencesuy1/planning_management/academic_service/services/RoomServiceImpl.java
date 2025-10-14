package com.facsciencesuy1.planning_management.academic_service.services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.facsciencesuy1.planning_management.academic_service.repositories.FacultyRepository;
import com.facsciencesuy1.planning_management.academic_service.repositories.RoomRepository;
import com.facsciencesuy1.planning_management.academic_service.services.interfaces.RoomService;
import com.facsciencesuy1.planning_management.academic_service.utils.dtos.RoomRequest;
import com.facsciencesuy1.planning_management.dtos.RoomDTO;
import com.facsciencesuy1.planning_management.entities.Faculty;
import com.facsciencesuy1.planning_management.entities.Room;
import com.facsciencesuy1.planning_management.entities.types.RoomType;
import com.facsciencesuy1.planning_management.exceptions.CustomBusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final FacultyRepository facultyRepository;

    @Override
    public Page<RoomDTO> getAllRooms(Pageable page) {
        return roomRepository.findAll(page)
                .map(Room::toDTO);
    }

    @Override
    public RoomDTO getRoomById(String id) {
        return roomRepository.findById(id)
                .map(Room::toDTO)
                .orElseThrow(() -> new CustomBusinessException("Room not found with id: " + id));
    }

    @Override
    public RoomDTO createRoom(RoomRequest request) {
        Faculty faculty = facultyRepository.findById(request.facultyId())
                .orElseThrow(() -> new CustomBusinessException("Faculty not found with this id"));

        if (roomRepository.existsByCode(request.code())) {
            throw new CustomBusinessException(
                    "Room with code " + request.code() + " already exists in this faculty.");

        }
        Room room = roomRepository.save(Room.builder()
                .code(request.code())
                .type(request.type())
                .capacity(request.capacity())
                .building(request.building())
                .availability(true)
                .build());

        faculty.getRooms().add(room);
        facultyRepository.save(faculty);
        return room.toDTO();
    }

    @Override
    public RoomDTO updateRoom(String id, RoomRequest roomDTO) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new CustomBusinessException("Room not found with id: " + id));

        room.setCode(roomDTO.code());
        room.setType(roomDTO.type());
        room.setCapacity(roomDTO.capacity());
        room.setBuilding(roomDTO.building());

        return roomRepository.save(room).toDTO();
    }

    @Override
    public void updateRoomAvailability(String id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new CustomBusinessException("Room not found"));
        room.setAvailability(false);
        roomRepository.save(room);
    }

    @Override
    public void deleteRoom(String id) {
        roomRepository.deleteById(id);
    }

    @Override
    public List<RoomDTO> getRoomsByCapacity(Long minimumCapacity) {
        return roomRepository.findByCapacityIsGreaterThanEqual(minimumCapacity).stream()
                .map(Room::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomDTO> getRoomsByType(String type) {
        return roomRepository.findByType(RoomType.valueOf(type)).stream()
                .map(Room::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllRoomTypes() {
        return Arrays.stream(RoomType.values()).map(RoomType::name).collect(Collectors.toList());
    }
}