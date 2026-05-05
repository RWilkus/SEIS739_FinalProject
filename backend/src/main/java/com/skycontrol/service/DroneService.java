package com.skycontrol.service;

import com.skycontrol.dto.*;
import com.skycontrol.exception.DuplicateResourceException;
import com.skycontrol.exception.ResourceNotFoundException;
import com.skycontrol.model.Drone;
import com.skycontrol.model.DroneStatus;
import com.skycontrol.repository.DroneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DroneService {
    
    private final DroneRepository droneRepository;
    
    public DroneService(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }
    
    public List<DroneDTO> getAllDrones() {
        return droneRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public DroneDTO getDroneById(Long id) {
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Drone not found with id: " + id));
        return convertToDTO(drone);
    }
    
    public DroneDTO createDrone(CreateDroneRequest request) {
        if (droneRepository.existsBySerialNumber(request.getSerialNumber())) {
            throw new DuplicateResourceException("Drone with serial number " + request.getSerialNumber() + " already exists");
        }
        
        Drone drone = new Drone(
            request.getSerialNumber(),
            request.getModel(),
            request.getManufacturer(),
            DroneStatus.AVAILABLE
        );
        
        Drone savedDrone = droneRepository.save(drone);
        return convertToDTO(savedDrone);
    }
    
    public DroneDTO updateDrone(Long id, UpdateDroneRequest request) {
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Drone not found with id: " + id));
        
        if (request.getModel() != null) {
            drone.setModel(request.getModel());
        }
        if (request.getManufacturer() != null) {
            drone.setManufacturer(request.getManufacturer());
        }
        
        Drone updatedDrone = droneRepository.save(drone);
        return convertToDTO(updatedDrone);
    }
    
    public DroneDTO updateDroneStatus(Long id, DroneStatus status) {
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Drone not found with id: " + id));
        drone.setStatus(status);
        Drone updatedDrone = droneRepository.save(drone);
        return convertToDTO(updatedDrone);
    }
    
    public void deleteDrone(Long id) {
        if (!droneRepository.existsById(id)) {
            throw new ResourceNotFoundException("Drone not found with id: " + id);
        }
        droneRepository.deleteById(id);
    }
    
    public List<MaintenanceRecordDTO> getDroneMaintenanceRecords(Long id) {
        return List.of();
    }
    
    public List<FlightLogDTO> getDroneFlightLogs(Long id) {
        return List.of();
    }
    
    public DroneDTO getDroneBySerialNumber(String serialNumber) {
        Drone drone = droneRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Drone not found with serial number: " + serialNumber));
        return convertToDTO(drone);
    }
    
    public DroneMaintenanceStatusDTO getDroneMaintenanceStatus(Long id) {
        DroneMaintenanceStatusDTO dto = new DroneMaintenanceStatusDTO();
        dto.setNeedsMaintenance(false);
        dto.setDaysSinceLastMaintenance(0);
        return dto;
    }
    
    public DroneStatisticsDTO getDroneStatistics(Long id) {
        DroneStatisticsDTO dto = new DroneStatisticsDTO();
        dto.setTotalFlights(0);
        dto.setTotalHours(0);
        return dto;
    }
    
    private DroneDTO convertToDTO(Drone drone) {
        DroneDTO dto = new DroneDTO();
        dto.setId(drone.getId());
        dto.setSerialNumber(drone.getSerialNumber());
        dto.setModel(drone.getModel());
        dto.setManufacturer(drone.getManufacturer());
        dto.setStatus(drone.getStatus());
        dto.setBatteryLevel(drone.getBatteryLevel());
        dto.setLatitude(drone.getLatitude());
        dto.setLongitude(drone.getLongitude());
        dto.setAltitude(drone.getAltitude());
        dto.setFlightHours(drone.getFlightHours());
        dto.setLastMaintenanceDate(drone.getLastMaintenanceDate());
        dto.setNextMaintenanceDate(drone.getNextMaintenanceDate());
        dto.setCreatedAt(drone.getCreatedAt());
        dto.setUpdatedAt(drone.getUpdatedAt());
        return dto;
    }
}
