package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.controllers;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.types.RoomType;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.AvailableRoomsRequestDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.RoomDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.RoomService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomController {
    
    private final RoomService roomService;
    
    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        List<RoomDTO> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable String id) {
        RoomDTO room = roomService.getRoomById(id);
        return ResponseEntity.ok(room);
    }
    
    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@Valid @RequestBody RoomDTO roomDTO) {
        RoomDTO createdRoom = roomService.createRoom(roomDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> updateRoom(
            @PathVariable String id,
            @Valid @RequestBody RoomDTO roomDTO) {
        RoomDTO updatedRoom = roomService.updateRoom(id, roomDTO);
        return ResponseEntity.ok(updatedRoom);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable String id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/available")
    public ResponseEntity<List<RoomDTO>> findAvailableRooms(
            @Valid @RequestBody AvailableRoomsRequestDTO request) {
        List<RoomDTO> availableRooms = roomService.findAvailableRooms(request);
        return ResponseEntity.ok(availableRooms);
    }
    
    @GetMapping("/capacity/{minimumCapacity}")
    public ResponseEntity<List<RoomDTO>> findRoomsByCapacity(
            @PathVariable @Min(1) Long minimumCapacity) {
        List<RoomDTO> rooms = roomService.findRoomsByCapacity(minimumCapacity);
        return ResponseEntity.ok(rooms);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<RoomDTO>> findRoomsByType(@PathVariable RoomType type) {
        List<RoomDTO> rooms = roomService.findRoomsByType(type);
        return ResponseEntity.ok(rooms);
    }
    
    @GetMapping("/{roomId}/availability/weekly")
    public ResponseEntity<Boolean> isRoomAvailable(
            @PathVariable String roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam DayOfWeek day) {
        boolean available = roomService.isRoomAvailable(roomId, startTime, endTime, day);
        return ResponseEntity.ok(available);
    }
    
    @GetMapping("/{roomId}/availability/date")
    public ResponseEntity<Boolean> isRoomAvailableForDate(
            @PathVariable String roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        boolean available = roomService.isRoomAvailableForDate(roomId, startTime, endTime, date);
        return ResponseEntity.ok(available);
    }
}
