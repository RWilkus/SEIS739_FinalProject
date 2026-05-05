package com.skycontrol.controller;

import com.skycontrol.dto.*;
import com.skycontrol.model.DroneStatus;
import com.skycontrol.service.DroneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drones")
@Tag(name = "Drone Management", description = "APIs for managing drones in the fleet")
public class DroneController {
    
    private final DroneService droneService;
    
    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }
    
    @GetMapping
    @Operation(summary = "Get all drones", description = "Retrieve a list of all drones in the fleet")
    public ResponseEntity<List<DroneDTO>> getAllDrones() {
        return ResponseEntity.ok(droneService.getAllDrones());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get drone by ID", description = "Retrieve a specific drone by its ID")
    public ResponseEntity<DroneDTO> getDroneById(@PathVariable Long id) {
        return ResponseEntity.ok(droneService.getDroneById(id));
    }
    
    @GetMapping("/serial/{serialNumber}")
    @Operation(summary = "Get drone by serial number")
    public ResponseEntity<DroneDTO> getDroneBySerialNumber(@PathVariable String serialNumber) {
        return ResponseEntity.ok(droneService.getDroneBySerialNumber(serialNumber));
    }
    
    @PostMapping
    @Operation(summary = "Create new drone", description = "Add a new drone to the fleet")
    public ResponseEntity<DroneDTO> createDrone(@Valid @RequestBody CreateDroneRequest request) {
        DroneDTO created = droneService.createDrone(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update drone", description = "Update an existing drone's information")
    public ResponseEntity<DroneDTO> updateDrone(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDroneRequest request) {
        return ResponseEntity.ok(droneService.updateDrone(id, request));
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update drone status")
    public ResponseEntity<DroneDTO> updateDroneStatus(
            @PathVariable Long id,
            @RequestParam DroneStatus status) {
        return ResponseEntity.ok(droneService.updateDroneStatus(id, status));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete drone", description = "Remove a drone from the fleet")
    public ResponseEntity<Void> deleteDrone(@PathVariable Long id) {
        droneService.deleteDrone(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/maintenance")
    @Operation(summary = "Get drone maintenance records")
    public ResponseEntity<List<MaintenanceRecordDTO>> getDroneMaintenanceRecords(@PathVariable Long id) {
        return ResponseEntity.ok(droneService.getDroneMaintenanceRecords(id));
    }
    
    @GetMapping("/{id}/flights")
    @Operation(summary = "Get drone flight logs")
    public ResponseEntity<List<FlightLogDTO>> getDroneFlightLogs(@PathVariable Long id) {
        return ResponseEntity.ok(droneService.getDroneFlightLogs(id));
    }
    
    @GetMapping("/{id}/maintenance-status")
    @Operation(summary = "Get drone maintenance status")
    public ResponseEntity<DroneMaintenanceStatusDTO> getDroneMaintenanceStatus(@PathVariable Long id) {
        return ResponseEntity.ok(droneService.getDroneMaintenanceStatus(id));
    }
    
    @GetMapping("/{id}/statistics")
    @Operation(summary = "Get drone statistics")
    public ResponseEntity<DroneStatisticsDTO> getDroneStatistics(@PathVariable Long id) {
        return ResponseEntity.ok(droneService.getDroneStatistics(id));
    }
}
