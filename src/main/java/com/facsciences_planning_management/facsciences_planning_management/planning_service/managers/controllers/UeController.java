package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.controllers;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.UeCreateRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.UeDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.UeUpdateRequest;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.UeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ues")
public class UeController {
    
    private final UeService ueService;
    
    @GetMapping
    public ResponseEntity<List<UeDTO>> getAllUes() {
        List<UeDTO> ues = ueService.getAllUes();
        return ResponseEntity.ok(ues);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UeDTO> getUeById(@PathVariable String id) {
        UeDTO ue = ueService.getUeById(id);
        return ResponseEntity.ok(ue);
    }
    
    @PostMapping
    public ResponseEntity<UeDTO> createUe(@Valid @RequestBody UeCreateRequest request) {
        UeDTO createdUe = ueService.createUe(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUe);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UeDTO> updateUe(
            @PathVariable String id,
            @Valid @RequestBody UeUpdateRequest request) {
        UeDTO updatedUe = ueService.updateUe(id, request);
        return ResponseEntity.ok(updatedUe);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUe(@PathVariable String id) {
        ueService.deleteUe(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/level/{levelId}")
    public ResponseEntity<List<UeDTO>> getUesByLevel(@PathVariable String levelId) {
        List<UeDTO> ues = ueService.getUesByLevel(levelId);
        return ResponseEntity.ok(ues);
    }
}

