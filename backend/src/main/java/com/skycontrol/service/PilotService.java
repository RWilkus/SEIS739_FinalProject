package com.skycontrol.service;

import com.skycontrol.dto.*;
import com.skycontrol.exception.DuplicateResourceException;
import com.skycontrol.exception.ResourceNotFoundException;
import com.skycontrol.model.Pilot;
import com.skycontrol.model.PilotStatus;
import com.skycontrol.repository.PilotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PilotService {
    
    private final PilotRepository pilotRepository;
    
    public PilotService(PilotRepository pilotRepository) {
        this.pilotRepository = pilotRepository;
    }
    
    public List<PilotDTO> getAllPilots() {
        return pilotRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public PilotDTO getPilotById(Long id) {
        Pilot pilot = pilotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pilot not found with id: " + id));
        return convertToDTO(pilot);
    }
    
    public PilotDTO createPilot(CreatePilotRequest request) {
        if (pilotRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateResourceException("Pilot with license number " + request.getLicenseNumber() + " already exists");
        }
        if (pilotRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Pilot with email " + request.getEmail() + " already exists");
        }
        
        Pilot pilot = new Pilot(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getLicenseNumber(),
            PilotStatus.ACTIVE
        );
        
        Pilot savedPilot = pilotRepository.save(pilot);
        return convertToDTO(savedPilot);
    }
    
    public PilotDTO updatePilot(Long id, UpdatePilotRequest request) {
        Pilot pilot = pilotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pilot not found with id: " + id));
        
        if (request.getPhone() != null) {
            pilot.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            pilot.setEmail(request.getEmail());
        }
        
        Pilot updatedPilot = pilotRepository.save(pilot);
        return convertToDTO(updatedPilot);
    }
    
    public void deletePilot(Long id) {
        if (!pilotRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pilot not found with id: " + id);
        }
        pilotRepository.deleteById(id);
    }
    
    public List<PilotCertificationDTO> getPilotCertifications(Long id) {
        // Simplified implementation
        return List.of();
    }
    
    public List<MissionDTO> getPilotMissions(Long id) {
        // Simplified implementation
        return List.of();
    }
    
    public List<PilotDTO> getAvailablePilots() {
        return pilotRepository.findByStatus(PilotStatus.ACTIVE).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public PilotCertificationDTO addCertification(Long id, PilotCertificationDTO certification) {
        // Simplified implementation
        return certification;
    }
    
    public PilotStatisticsDTO getPilotStatistics(Long id) {
        // Simplified implementation
        PilotStatisticsDTO dto = new PilotStatisticsDTO();
        dto.setTotalMissions(0);
        dto.setTotalHours(0);
        return dto;
    }
    
    private PilotDTO convertToDTO(Pilot pilot) {
        PilotDTO dto = new PilotDTO();
        dto.setId(pilot.getId());
        dto.setFirstName(pilot.getFirstName());
        dto.setLastName(pilot.getLastName());
        dto.setEmail(pilot.getEmail());
        dto.setPhone(pilot.getPhone());
        dto.setLicenseNumber(pilot.getLicenseNumber());
        dto.setLicenseExpiryDate(pilot.getLicenseExpiryDate());
        dto.setStatus(pilot.getStatus());
        dto.setTotalFlightHours(pilot.getTotalFlightHours());
        dto.setCertificationDate(pilot.getCertificationDate());
        dto.setCreatedAt(pilot.getCreatedAt());
        dto.setUpdatedAt(pilot.getUpdatedAt());
        return dto;
    }
}
