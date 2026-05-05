package com.skycontrol.service;

import com.skycontrol.dto.CreatePilotRequest;
import com.skycontrol.dto.PilotDTO;
import com.skycontrol.dto.UpdatePilotRequest;
import com.skycontrol.exception.DuplicateResourceException;
import com.skycontrol.exception.ResourceNotFoundException;
import com.skycontrol.model.Pilot;
import com.skycontrol.model.PilotStatus;
import com.skycontrol.repository.PilotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PilotService
 * Tests pilot management operations
 */
class PilotServiceTest {

    @Mock
    private PilotRepository pilotRepository;

    @InjectMocks
    private PilotService pilotService;

    private Pilot testPilot;
    private CreatePilotRequest createRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test pilot
        testPilot = new Pilot("John", "Doe", "john@example.com", "PIL-001", PilotStatus.ACTIVE);
        testPilot.setId(1L);
        testPilot.setTotalFlightHours(150);

        // Setup create request
        createRequest = new CreatePilotRequest();
        createRequest.setFirstName("Jane");
        createRequest.setLastName("Smith");
        createRequest.setEmail("jane@example.com");
        createRequest.setLicenseNumber("PIL-002");
    }

    @Test
    @DisplayName("Should retrieve all pilots successfully")
    void testGetAllPilots() {
        // Arrange
        Pilot pilot2 = new Pilot("Jane", "Smith", "jane@example.com", "PIL-002", PilotStatus.ACTIVE);
        pilot2.setId(2L);
        List<Pilot> pilots = Arrays.asList(testPilot, pilot2);
        
        when(pilotRepository.findAll()).thenReturn(pilots);

        // Act
        List<PilotDTO> result = pilotService.getAllPilots();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());
        verify(pilotRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve pilot by ID successfully")
    void testGetPilotById_Success() {
        // Arrange
        when(pilotRepository.findById(1L)).thenReturn(Optional.of(testPilot));

        // Act
        PilotDTO result = pilotService.getPilotById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("PIL-001", result.getLicenseNumber());
        assertEquals(PilotStatus.ACTIVE, result.getStatus());
        assertEquals(150, result.getTotalFlightHours());
        verify(pilotRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when pilot not found by ID")
    void testGetPilotById_NotFound() {
        // Arrange
        when(pilotRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            pilotService.getPilotById(999L);
        });
        verify(pilotRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create pilot successfully")
    void testCreatePilot_Success() {
        // Arrange
        Pilot newPilot = new Pilot(
            createRequest.getFirstName(),
            createRequest.getLastName(),
            createRequest.getEmail(),
            createRequest.getLicenseNumber(),
            PilotStatus.ACTIVE
        );
        newPilot.setId(2L);

        when(pilotRepository.existsByLicenseNumber("PIL-002")).thenReturn(false);
        when(pilotRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(pilotRepository.save(any(Pilot.class))).thenReturn(newPilot);

        // Act
        PilotDTO result = pilotService.createPilot(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("jane@example.com", result.getEmail());
        assertEquals("PIL-002", result.getLicenseNumber());
        assertEquals(PilotStatus.ACTIVE, result.getStatus());
        verify(pilotRepository, times(1)).existsByLicenseNumber("PIL-002");
        verify(pilotRepository, times(1)).existsByEmail("jane@example.com");
        verify(pilotRepository, times(1)).save(any(Pilot.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when creating pilot with duplicate license")
    void testCreatePilot_DuplicateLicense() {
        // Arrange
        when(pilotRepository.existsByLicenseNumber("PIL-002")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> {
            pilotService.createPilot(createRequest);
        });
        verify(pilotRepository, times(1)).existsByLicenseNumber("PIL-002");
        verify(pilotRepository, never()).save(any(Pilot.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when creating pilot with duplicate email")
    void testCreatePilot_DuplicateEmail() {
        // Arrange
        when(pilotRepository.existsByLicenseNumber("PIL-002")).thenReturn(false);
        when(pilotRepository.existsByEmail("jane@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> {
            pilotService.createPilot(createRequest);
        });
        verify(pilotRepository, times(1)).existsByLicenseNumber("PIL-002");
        verify(pilotRepository, times(1)).existsByEmail("jane@example.com");
        verify(pilotRepository, never()).save(any(Pilot.class));
    }

    @Test
    @DisplayName("Should update pilot successfully")
    void testUpdatePilot_Success() {
        // Arrange
        UpdatePilotRequest updateRequest = new UpdatePilotRequest();
        updateRequest.setPhone("555-1234");
        updateRequest.setEmail("john.new@example.com");

        when(pilotRepository.findById(1L)).thenReturn(Optional.of(testPilot));
        when(pilotRepository.save(any(Pilot.class))).thenReturn(testPilot);

        // Act
        PilotDTO result = pilotService.updatePilot(1L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(pilotRepository, times(1)).findById(1L);
        verify(pilotRepository, times(1)).save(testPilot);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent pilot")
    void testUpdatePilot_NotFound() {
        // Arrange
        UpdatePilotRequest updateRequest = new UpdatePilotRequest();
        updateRequest.setPhone("555-1234");

        when(pilotRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            pilotService.updatePilot(999L, updateRequest);
        });
        verify(pilotRepository, times(1)).findById(999L);
        verify(pilotRepository, never()).save(any(Pilot.class));
    }

    @Test
    @DisplayName("Should delete pilot successfully")
    void testDeletePilot_Success() {
        // Arrange
        when(pilotRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pilotRepository).deleteById(1L);

        // Act
        pilotService.deletePilot(1L);

        // Assert
        verify(pilotRepository, times(1)).existsById(1L);
        verify(pilotRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent pilot")
    void testDeletePilot_NotFound() {
        // Arrange
        when(pilotRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            pilotService.deletePilot(999L);
        });
        verify(pilotRepository, times(1)).existsById(999L);
        verify(pilotRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should retrieve available pilots")
    void testGetAvailablePilots() {
        // Arrange
        Pilot pilot2 = new Pilot("Jane", "Smith", "jane@example.com", "PIL-002", PilotStatus.ACTIVE);
        pilot2.setId(2L);
        List<Pilot> activePilots = Arrays.asList(testPilot, pilot2);
        
        when(pilotRepository.findByStatus(PilotStatus.ACTIVE)).thenReturn(activePilots);

        // Act
        List<PilotDTO> result = pilotService.getAvailablePilots();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getStatus() == PilotStatus.ACTIVE));
        verify(pilotRepository, times(1)).findByStatus(PilotStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should return empty certifications list")
    void testGetPilotCertifications() {
        // Act
        var result = pilotService.getPilotCertifications(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty missions list")
    void testGetPilotMissions() {
        // Act
        var result = pilotService.getPilotMissions(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should add certification successfully")
    void testAddCertification() {
        // Arrange
        var certification = new com.skycontrol.dto.PilotCertificationDTO();
        certification.setType("Commercial Drone License");

        // Act
        var result = pilotService.addCertification(1L, certification);

        // Assert
        assertNotNull(result);
        assertEquals("Commercial Drone License", result.getType());
    }

    @Test
    @DisplayName("Should return pilot statistics")
    void testGetPilotStatistics() {
        // Act
        var result = pilotService.getPilotStatistics(1L);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalMissions());
        assertEquals(0, result.getTotalHours());
    }
}
