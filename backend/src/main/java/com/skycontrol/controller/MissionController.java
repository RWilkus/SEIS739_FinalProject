package com.skycontrol.controller;

import com.skycontrol.dto.*;
import com.skycontrol.service.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@Tag(name = "Mission Management", description = "APIs for managing drone missions")
public class MissionController {
    
    private final MissionService missionService;
    
    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }
    
    @GetMapping
    @Operation(summary = "Get all missions")
    public ResponseEntity<List<MissionDTO>> getAllMissions() {
        return ResponseEntity.ok(missionService.getAllMissions());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get mission by ID")
    public ResponseEntity<MissionDTO> getMissionById(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.getMissionById(id));
    }
    
    @PostMapping
    @Operation(summary = "Create new mission")
    public ResponseEntity<MissionDTO> createMission(@Valid @RequestBody CreateMissionRequest request) {
        MissionDTO created = missionService.createMission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update mission")
    public ResponseEntity<MissionDTO> updateMission(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMissionRequest request) {
        return ResponseEntity.ok(missionService.updateMission(id, request));
    }
    
    @PostMapping("/{missionId}/assign-drone/{droneId}")
    @Operation(summary = "Assign drone to mission")
    public ResponseEntity<MissionDTO> assignDrone(
            @PathVariable Long missionId,
            @PathVariable Long droneId) {
        return ResponseEntity.ok(missionService.assignDrone(missionId, droneId));
    }
    
    @PostMapping("/{missionId}/assign-pilot/{pilotId}")
    @Operation(summary = "Assign pilot to mission")
    public ResponseEntity<MissionDTO> assignPilot(
            @PathVariable Long missionId,
            @PathVariable Long pilotId) {
        return ResponseEntity.ok(missionService.assignPilot(missionId, pilotId));
    }
    
    @PostMapping("/{id}/start")
    @Operation(summary = "Start mission")
    public ResponseEntity<MissionDTO> startMission(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.startMission(id));
    }
    
    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete mission")
    public ResponseEntity<MissionDTO> completeMission(
            @PathVariable Long id,
            @RequestBody(required = false) MissionCompletionRequest request) {
        return ResponseEntity.ok(missionService.completeMission(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete mission")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        missionService.deleteMission(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/drone/{droneId}")
    @Operation(summary = "Get missions by drone")
    public ResponseEntity<List<MissionDTO>> getMissionsByDrone(@PathVariable Long droneId) {
        return ResponseEntity.ok(missionService.getMissionsByDrone(droneId));
    }
    
    @GetMapping("/pilot/{pilotId}")
    @Operation(summary = "Get missions by pilot")
    public ResponseEntity<List<MissionDTO>> getMissionsByPilot(@PathVariable Long pilotId) {
        return ResponseEntity.ok(missionService.getMissionsByPilot(pilotId));
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active missions")
    public ResponseEntity<List<MissionDTO>> getActiveMissions() {
        return ResponseEntity.ok(missionService.getActiveMissions());
    }
    
    @GetMapping("/completed")
    @Operation(summary = "Get completed missions")
    public ResponseEntity<List<MissionDTO>> getCompletedMissions() {
        return ResponseEntity.ok(missionService.getCompletedMissions());
    }
}
