package com.skycontrol.service;

import com.skycontrol.dto.*;
import com.skycontrol.exception.ResourceNotFoundException;
import com.skycontrol.model.*;
import com.skycontrol.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MissionService {
    
    private final MissionRepository missionRepository;
    private final DroneRepository droneRepository;
    private final PilotRepository pilotRepository;
    
    public MissionService(MissionRepository missionRepository, DroneRepository droneRepository, PilotRepository pilotRepository) {
        this.missionRepository = missionRepository;
        this.droneRepository = droneRepository;
        this.pilotRepository = pilotRepository;
    }
    
    public List<MissionDTO> getAllMissions() {
        return missionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public MissionDTO getMissionById(Long id) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found with id: " + id));
        return convertToDTO(mission);
    }
    
    public MissionDTO createMission(CreateMissionRequest request) {
        Mission mission = new Mission();
        mission.setName(request.getName());
        mission.setDescription(request.getDescription());
        mission.setType(request.getType());
        mission.setStatus(MissionStatus.PLANNED);
        mission.setScheduledStartTime(request.getScheduledStartTime());
        
        Mission savedMission = missionRepository.save(mission);
        return convertToDTO(savedMission);
    }
    
    public MissionDTO updateMission(Long id, UpdateMissionRequest request) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found with id: " + id));
        
        if (request.getName() != null) {
            mission.setName(request.getName());
        }
        if (request.getDescription() != null) {
            mission.setDescription(request.getDescription());
        }
        
        Mission updatedMission = missionRepository.save(mission);
        return convertToDTO(updatedMission);
    }
    
    public MissionDTO assignDrone(Long missionId, Long droneId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));
        Drone drone = droneRepository.findById(droneId)
                .orElseThrow(() -> new ResourceNotFoundException("Drone not found"));
        
        mission.setDrone(drone);
        Mission savedMission = missionRepository.save(mission);
        return convertToDTO(savedMission);
    }
    
    public MissionDTO assignPilot(Long missionId, Long pilotId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));
        Pilot pilot = pilotRepository.findById(pilotId)
                .orElseThrow(() -> new ResourceNotFoundException("Pilot not found"));
        
        mission.setPilot(pilot);
        Mission savedMission = missionRepository.save(mission);
        return convertToDTO(savedMission);
    }
    
    public MissionDTO startMission(Long id) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));
        mission.setStatus(MissionStatus.IN_PROGRESS);
        mission.setActualStartTime(LocalDateTime.now());
        Mission savedMission = missionRepository.save(mission);
        return convertToDTO(savedMission);
    }
    
    public MissionDTO completeMission(Long id, MissionCompletionRequest request) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));
        mission.setStatus(MissionStatus.COMPLETED);
        mission.setActualEndTime(LocalDateTime.now());
        Mission savedMission = missionRepository.save(mission);
        return convertToDTO(savedMission);
    }
    
    public void deleteMission(Long id) {
        if (!missionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mission not found");
        }
        missionRepository.deleteById(id);
    }
    
    public List<MissionDTO> getMissionsByDrone(Long droneId) {
        return missionRepository.findByDroneId(droneId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MissionDTO> getMissionsByPilot(Long pilotId) {
        return missionRepository.findByPilotId(pilotId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MissionDTO> getActiveMissions() {
        return missionRepository.findByStatus(MissionStatus.IN_PROGRESS).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MissionDTO> getCompletedMissions() {
        return missionRepository.findByStatus(MissionStatus.COMPLETED).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private MissionDTO convertToDTO(Mission mission) {
        MissionDTO dto = new MissionDTO();
        dto.setId(mission.getId());
        dto.setName(mission.getName());
        dto.setDescription(mission.getDescription());
        dto.setType(mission.getType());
        dto.setStatus(mission.getStatus());
        dto.setScheduledStartTime(mission.getScheduledStartTime());
        dto.setActualStartTime(mission.getActualStartTime());
        dto.setScheduledEndTime(mission.getScheduledEndTime());
        dto.setActualEndTime(mission.getActualEndTime());
        dto.setLocation(mission.getLocation());
        dto.setCreatedAt(mission.getCreatedAt());
        dto.setUpdatedAt(mission.getUpdatedAt());
        
        if (mission.getDrone() != null) {
            dto.setDroneId(mission.getDrone().getId());
            dto.setDroneSerialNumber(mission.getDrone().getSerialNumber());
        }
        if (mission.getPilot() != null) {
            dto.setPilotId(mission.getPilot().getId());
            dto.setPilotName(mission.getPilot().getFirstName() + " " + mission.getPilot().getLastName());
        }
        
        return dto;
    }
}
