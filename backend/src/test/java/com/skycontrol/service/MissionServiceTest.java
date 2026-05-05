package com.skycontrol.service;

import com.skycontrol.dto.CreateMissionRequest;
import com.skycontrol.dto.MissionCompletionRequest;
import com.skycontrol.dto.MissionDTO;
import com.skycontrol.dto.UpdateMissionRequest;
import com.skycontrol.exception.ResourceNotFoundException;
import com.skycontrol.model.*;
import com.skycontrol.repository.DroneRepository;
import com.skycontrol.repository.MissionRepository;
import com.skycontrol.repository.PilotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MissionService
 * Tests mission lifecycle and assignment operations
 */
class MissionServiceTest {

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private DroneRepository droneRepository;

    @Mock
    private PilotRepository pilotRepository;

    @InjectMocks
    private MissionService missionService;

    private Mission testMission;
    private Drone testDrone;
    private Pilot testPilot;
    private CreateMissionRequest createRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test mission
        testMission = new Mission();
        testMission.setId(1L);
        testMission.setName("Test Mission");
        testMission.setType(MissionType.SURVEILLANCE);
        testMission.setStatus(MissionStatus.PLANNED);
        testMission.setScheduledStartTime(LocalDateTime.now().plusHours(1));

        // Setup test drone
        testDrone = new Drone("DRN-001", "Phantom 4", "DJI", DroneStatus.AVAILABLE);
        testDrone.setId(1L);

        // Setup test pilot
        testPilot = new Pilot("John", "Doe", "john@example.com", "PIL-001", PilotStatus.ACTIVE);
        testPilot.setId(1L);

        // Setup create request
        createRequest = new CreateMissionRequest();
        createRequest.setName("New Mission");
        createRequest.setType(MissionType.DELIVERY);
        createRequest.setScheduledStartTime(LocalDateTime.now().plusDays(1));
    }

    @Test
    @DisplayName("Should retrieve all missions successfully")
    void testGetAllMissions() {
        // Arrange
        Mission mission2 = new Mission();
        mission2.setId(2L);
        mission2.setName("Mission 2");
        mission2.setType(MissionType.INSPECTION);
        mission2.setStatus(MissionStatus.COMPLETED);
        
        List<Mission> missions = Arrays.asList(testMission, mission2);
        when(missionRepository.findAll()).thenReturn(missions);

        // Act
        List<MissionDTO> result = missionService.getAllMissions();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Mission", result.get(0).getName());
        assertEquals("Mission 2", result.get(1).getName());
        verify(missionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve mission by ID successfully")
    void testGetMissionById_Success() {
        // Arrange
        when(missionRepository.findById(1L)).thenReturn(Optional.of(testMission));

        // Act
        MissionDTO result = missionService.getMissionById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Mission", result.getName());
        assertEquals(MissionType.SURVEILLANCE, result.getType());
        assertEquals(MissionStatus.PLANNED, result.getStatus());
        verify(missionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when mission not found")
    void testGetMissionById_NotFound() {
        // Arrange
        when(missionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            missionService.getMissionById(999L);
        });
        verify(missionRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create mission successfully")
    void testCreateMission_Success() {
        // Arrange
        Mission newMission = new Mission();
        newMission.setId(2L);
        newMission.setName(createRequest.getName());
        newMission.setType(createRequest.getType());
        newMission.setStatus(MissionStatus.PLANNED);

        when(missionRepository.save(any(Mission.class))).thenReturn(newMission);

        // Act
        MissionDTO result = missionService.createMission(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals("New Mission", result.getName());
        assertEquals(MissionType.DELIVERY, result.getType());
        assertEquals(MissionStatus.PLANNED, result.getStatus());
        verify(missionRepository, times(1)).save(any(Mission.class));
    }

    @Test
    @DisplayName("Should update mission successfully")
    void testUpdateMission_Success() {
        // Arrange
        UpdateMissionRequest updateRequest = new UpdateMissionRequest();
        updateRequest.setName("Updated Mission");
        updateRequest.setDescription("Updated description");

        when(missionRepository.findById(1L)).thenReturn(Optional.of(testMission));
        when(missionRepository.save(any(Mission.class))).thenReturn(testMission);

        // Act
        MissionDTO result = missionService.updateMission(1L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(missionRepository, times(1)).findById(1L);
        verify(missionRepository, times(1)).save(testMission);
    }

    @Test
    @DisplayName("Should assign drone to mission successfully")
    void testAssignDrone_Success() {
        // Arrange
        when(missionRepository.findById(1L)).thenReturn(Optional.of(testMission));
        when(droneRepository.findById(1L)).thenReturn(Optional.of(testDrone));
        when(missionRepository.save(any(Mission.class))).thenReturn(testMission);

        // Act
        MissionDTO result = missionService.assignDrone(1L, 1L);

        // Assert
        assertNotNull(result);
        verify(missionRepository, times(1)).findById(1L);
        verify(droneRepository, times(1)).findById(1L);
        verify(missionRepository, times(1)).save(testMission);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when assigning drone to non-existent mission")
    void testAssignDrone_MissionNotFound() {
        // Arrange
        when(missionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            missionService.assignDrone(999L, 1L);
        });
        verify(missionRepository, times(1)).findById(999L);
        verify(droneRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when assigning non-existent drone")
    void testAssignDrone_DroneNotFound() {
        // Arrange
        when(missionRepository.findById(1L)).thenReturn(Optional.of(testMission));
        when(droneRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            missionService.assignDrone(1L, 999L);
        });
        verify(missionRepository, times(1)).findById(1L);
        verify(droneRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should assign pilot to mission successfully")
    void testAssignPilot_Success() {
        // Arrange
        when(missionRepository.findById(1L)).thenReturn(Optional.of(testMission));
        when(pilotRepository.findById(1L)).thenReturn(Optional.of(testPilot));
        when(missionRepository.save(any(Mission.class))).thenReturn(testMission);

        // Act
        MissionDTO result = missionService.assignPilot(1L, 1L);

        // Assert
        assertNotNull(result);
        verify(missionRepository, times(1)).findById(1L);
        verify(pilotRepository, times(1)).findById(1L);
        verify(missionRepository, times(1)).save(testMission);
    }

    @Test
    @DisplayName("Should start mission successfully")
    void testStartMission_Success() {
        // Arrange
        when(missionRepository.findById(1L)).thenReturn(Optional.of(testMission));
        when(missionRepository.save(any(Mission.class))).thenReturn(testMission);

        // Act
        MissionDTO result = missionService.startMission(1L);

        // Assert
        assertNotNull(result);
        assertEquals(MissionStatus.IN_PROGRESS, testMission.getStatus());
        assertNotNull(testMission.getActualStartTime());
        verify(missionRepository, times(1)).findById(1L);
        verify(missionRepository, times(1)).save(testMission);
    }

    @Test
    @DisplayName("Should complete mission successfully")
    void testCompleteMission_Success() {
        // Arrange
        testMission.setStatus(MissionStatus.IN_PROGRESS);
        MissionCompletionRequest completionRequest = new MissionCompletionRequest();
        completionRequest.setNotes("Mission completed successfully");

        when(missionRepository.findById(1L)).thenReturn(Optional.of(testMission));
        when(missionRepository.save(any(Mission.class))).thenReturn(testMission);

        // Act
        MissionDTO result = missionService.completeMission(1L, completionRequest);

        // Assert
        assertNotNull(result);
        assertEquals(MissionStatus.COMPLETED, testMission.getStatus());
        assertNotNull(testMission.getActualEndTime());
        verify(missionRepository, times(1)).findById(1L);
        verify(missionRepository, times(1)).save(testMission);
    }

    @Test
    @DisplayName("Should delete mission successfully")
    void testDeleteMission_Success() {
        // Arrange
        when(missionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(missionRepository).deleteById(1L);

        // Act
        missionService.deleteMission(1L);

        // Assert
        verify(missionRepository, times(1)).existsById(1L);
        verify(missionRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent mission")
    void testDeleteMission_NotFound() {
        // Arrange
        when(missionRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            missionService.deleteMission(999L);
        });
        verify(missionRepository, times(1)).existsById(999L);
        verify(missionRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should retrieve missions by drone")
    void testGetMissionsByDrone() {
        // Arrange
        testMission.setDrone(testDrone);
        List<Mission> missions = Arrays.asList(testMission);
        
        when(missionRepository.findByDroneId(1L)).thenReturn(missions);

        // Act
        List<MissionDTO> result = missionService.getMissionsByDrone(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(missionRepository, times(1)).findByDroneId(1L);
    }

    @Test
    @DisplayName("Should retrieve missions by pilot")
    void testGetMissionsByPilot() {
        // Arrange
        testMission.setPilot(testPilot);
        List<Mission> missions = Arrays.asList(testMission);
        
        when(missionRepository.findByPilotId(1L)).thenReturn(missions);

        // Act
        List<MissionDTO> result = missionService.getMissionsByPilot(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(missionRepository, times(1)).findByPilotId(1L);
    }

    @Test
    @DisplayName("Should retrieve active missions")
    void testGetActiveMissions() {
        // Arrange
        testMission.setStatus(MissionStatus.IN_PROGRESS);
        List<Mission> missions = Arrays.asList(testMission);
        
        when(missionRepository.findByStatus(MissionStatus.IN_PROGRESS)).thenReturn(missions);

        // Act
        List<MissionDTO> result = missionService.getActiveMissions();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(MissionStatus.IN_PROGRESS, result.get(0).getStatus());
        verify(missionRepository, times(1)).findByStatus(MissionStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Should retrieve completed missions")
    void testGetCompletedMissions() {
        // Arrange
        testMission.setStatus(MissionStatus.COMPLETED);
        List<Mission> missions = Arrays.asList(testMission);
        
        when(missionRepository.findByStatus(MissionStatus.COMPLETED)).thenReturn(missions);

        // Act
        List<MissionDTO> result = missionService.getCompletedMissions();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(MissionStatus.COMPLETED, result.get(0).getStatus());
        verify(missionRepository, times(1)).findByStatus(MissionStatus.COMPLETED);
    }
}
