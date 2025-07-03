package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.RoomDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.faculty.RoomRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.RoomService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<Page<RoomDTO>> getAllRooms(
            @PageableDefault(size = 10, sort = "capacity") Pageable page) {
        Page<RoomDTO> rooms = roomService.getAllRooms(page);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@NonNull @PathVariable String id) {
        RoomDTO room = roomService.getRoomById(id);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/type")
    public ResponseEntity<List<RoomDTO>> findRoomsByType(@NonNull @RequestParam String type) {
        List<RoomDTO> rooms = roomService.getRoomsByType(type);
        return ResponseEntity.ok(rooms);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RoomDTO> createRoom(@Valid @RequestBody RoomRequest request) {
        RoomDTO createdRoom = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RoomDTO> updateRoom(
            @NonNull @PathVariable String id,
            @Valid @RequestBody RoomDTO roomDTO) {
        RoomDTO updatedRoom = roomService.updateRoom(id, roomDTO);
        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteRoom(@NonNull @PathVariable String id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/capacity")
    public ResponseEntity<List<RoomDTO>> findRoomsByCapacity(
            @NonNull @RequestParam @Min(1) Long minCapacity) {
        List<RoomDTO> rooms = roomService.getRoomsByCapacity(minCapacity);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/types")
    public ResponseEntity<List<String>> getAllRoomTypes() {
        List<String> roomTypes = roomService.getAllRoomTypes();
        return ResponseEntity.ok(roomTypes);
    }
}
