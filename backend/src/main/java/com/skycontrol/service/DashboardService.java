package com.skycontrol.service;

import com.skycontrol.dto.DashboardDTO;
import com.skycontrol.dto.FleetStatusSummaryDTO;
import com.skycontrol.dto.OverallStatisticsDTO; 
import com.skycontrol.repository.DroneRepository;
import com.skycontrol.repository.MissionRepository;
import com.skycontrol.repository.PilotRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    
    private final DroneRepository droneRepository;
    private final MissionRepository missionRepository;
    private final PilotRepository pilotRepository;
    
    public DashboardService(DroneRepository droneRepository, MissionRepository missionRepository, PilotRepository pilotRepository) {
        this.droneRepository = droneRepository;
        this.missionRepository = missionRepository;
        this.pilotRepository = pilotRepository;
    }
    
    public DashboardDTO getDashboardData() {
        DashboardDTO dto = new DashboardDTO();
        dto.setTotalDrones((int) droneRepository.count());
        dto.setActiveDrones(0); // Simplified
        dto.setTotalMissions((int) missionRepository.count());
        dto.setActivePilots((int) pilotRepository.count());
        return dto;
    }
    
    public FleetStatusSummaryDTO getFleetStatusSummary() {
        FleetStatusSummaryDTO dto = new FleetStatusSummaryDTO();
        dto.setAvailable(0);
        dto.setInFlight(0);
        dto.setMaintenance(0);
        return dto;
    }
    
    public OverallStatisticsDTO getOverallStatistics() {
        OverallStatisticsDTO dto = new OverallStatisticsDTO();
        dto.setTotalFlightHours(0);
        dto.setCompletedMissions(0);
        return dto;
    }
}
