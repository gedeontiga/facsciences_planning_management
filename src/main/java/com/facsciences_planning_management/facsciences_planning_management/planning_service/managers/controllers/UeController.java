package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.controllers;

import java.util.List;

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

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.UeDTO;
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

    @GetMapping("/code/{code}")
    public ResponseEntity<UeDTO> getUeByCode(@PathVariable String code) {
        UeDTO ue = ueService.getUeByCode(code);
        return ResponseEntity.ok(ue);
    }

    @GetMapping("/level/{levelId}")
    public ResponseEntity<List<UeDTO>> getUesByLevel(@PathVariable String levelId) {
        List<UeDTO> ues = ueService.getUesByLevel(levelId);
        return ResponseEntity.ok(ues);
    }

    @GetMapping("/category/{category}/level/{levelId}")
    public ResponseEntity<List<UeDTO>> getUesByCategoryAndLevel(
            @PathVariable String category,
            @PathVariable String levelId) {
        List<UeDTO> ues = ueService.getUesByCategoryAndLevel(category, levelId);
        return ResponseEntity.ok(ues);
    }

    @GetMapping("/credits/{credits}")
    public ResponseEntity<List<UeDTO>> getUesByCreditsAndLevel(
            @PathVariable Integer credits,
            @RequestParam String levelId) {
        List<UeDTO> ues = ueService.getUesByCreditsAndLevel(credits, levelId);
        return ResponseEntity.ok(ues);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UeDTO> createUe(@Valid @RequestBody UeDTO request) {
        UeDTO createdUe = ueService.createUe(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUe);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UeDTO> updateUe(
            @PathVariable String id,
            @Valid @RequestBody UeDTO request) {
        UeDTO updatedUe = ueService.updateUe(id, request);
        return ResponseEntity.ok(updatedUe);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUe(@PathVariable String id) {
        ueService.deleteUe(id);
        return ResponseEntity.noContent().build();
    }
}
