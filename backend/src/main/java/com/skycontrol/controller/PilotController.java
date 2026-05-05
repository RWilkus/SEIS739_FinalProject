package com.skycontrol.controller;

import com.skycontrol.dto.*;
import com.skycontrol.service.PilotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pilots")
@Tag(name = "Pilot Management", description = "APIs for managing drone pilots")
public class PilotController {
    
    private final PilotService pilotService;
    
    public PilotController(PilotService pilotService) {
        this.pilotService = pilotService;
    }
    
    @GetMapping
    @Operation(summary = "Get all pilots")
    public ResponseEntity<List<PilotDTO>> getAllPilots() {
        return ResponseEntity.ok(pilotService.getAllPilots());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get pilot by ID")
    public ResponseEntity<PilotDTO> getPilotById(@PathVariable Long id) {
        return ResponseEntity.ok(pilotService.getPilotById(id));
    }
    
    @PostMapping
    @Operation(summary = "Create new pilot")
    public ResponseEntity<PilotDTO> createPilot(@Valid @RequestBody CreatePilotRequest request) {
        PilotDTO created = pilotService.createPilot(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update pilot")
    public ResponseEntity<PilotDTO> updatePilot(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePilotRequest request) {
        return ResponseEntity.ok(pilotService.updatePilot(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete pilot")
    public ResponseEntity<Void> deletePilot(@PathVariable Long id) {
        pilotService.deletePilot(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/certifications")
    @Operation(summary = "Get pilot certifications")
    public ResponseEntity<List<PilotCertificationDTO>> getPilotCertifications(@PathVariable Long id) {
        return ResponseEntity.ok(pilotService.getPilotCertifications(id));
    }
    
    @GetMapping("/{id}/missions")
    @Operation(summary = "Get pilot missions")
    public ResponseEntity<List<MissionDTO>> getPilotMissions(@PathVariable Long id) {
        return ResponseEntity.ok(pilotService.getPilotMissions(id));
    }
    
    @GetMapping("/available")
    @Operation(summary = "Get available pilots")
    public ResponseEntity<List<PilotDTO>> getAvailablePilots() {
        return ResponseEntity.ok(pilotService.getAvailablePilots());
    }
    
    @PostMapping("/{id}/certifications")
    @Operation(summary = "Add certification to pilot")
    public ResponseEntity<PilotCertificationDTO> addCertification(
            @PathVariable Long id,
            @RequestBody PilotCertificationDTO certification) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pilotService.addCertification(id, certification));
    }
    
    @GetMapping("/{id}/statistics")
    @Operation(summary = "Get pilot statistics")
    public ResponseEntity<PilotStatisticsDTO> getPilotStatistics(@PathVariable Long id) {
        return ResponseEntity.ok(pilotService.getPilotStatistics(id));
    }
}
