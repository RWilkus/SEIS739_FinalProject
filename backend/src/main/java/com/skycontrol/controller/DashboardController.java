package com.skycontrol.controller;

import com.skycontrol.dto.DashboardDTO;
import com.skycontrol.dto.FleetStatusSummaryDTO;
import com.skycontrol.dto.OverallStatisticsDTO;
import com.skycontrol.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Dashboard and statistics APIs")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    
    @GetMapping
    @Operation(summary = "Get dashboard data", description = "Retrieve overall dashboard statistics")
    public ResponseEntity<DashboardDTO> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardData());
    }
    
    @GetMapping("/fleet-status")
    @Operation(summary = "Get fleet status summary")
    public ResponseEntity<FleetStatusSummaryDTO> getFleetStatus() {
        return ResponseEntity.ok(dashboardService.getFleetStatusSummary());
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "Get overall statistics")
    public ResponseEntity<OverallStatisticsDTO> getStatistics() {
        return ResponseEntity.ok(dashboardService.getOverallStatistics());
    }
}
