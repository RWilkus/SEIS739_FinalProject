package com.skycontrol.service;

import com.skycontrol.dto.*;
import com.skycontrol.exception.DuplicateResourceException;
import com.skycontrol.exception.ResourceNotFoundException;
import com.skycontrol.model.Drone;
import com.skycontrol.model.DroneStatus;
import com.skycontrol.repository.DroneRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DroneService
 * Tests CRUD operations and business logic
 */
class DroneServiceTest {

    @Mock
    private DroneRepository droneRepository;

    @InjectMocks
    private DroneService droneService;

    private Drone testDrone;
    private CreateDroneRequest createRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup test drone using setters (no constructor assumptions)
        testDrone = new Drone();
        testDrone.setId(1L);
        testDrone.setSerialNumber("DRN-001");
        testDrone.setModel("Phantom 4");
        testDrone.setManufacturer("DJI");
        testDrone.setStatus(DroneStatus.AVAILABLE);
        testDrone.setBatteryLevel(100);
        testDrone.setFlightHours(50);

        // Setup create request
        createRequest = new CreateDroneRequest();
        createRequest.setSerialNumber("DRN-002");
        createRequest.setModel("Mavic Pro");
        createRequest.setManufacturer("DJI");
    }

    @Test
    @DisplayName("Should retrieve all drones successfully")
    void testGetAllDrones() {
        // Arrange
        Drone drone2 = new Drone();
        drone2.setId(2L);
        drone2.setSerialNumber("DRN-002");
        drone2.setModel("Mavic Pro");
        drone2.setManufacturer("DJI");
        drone2.setStatus(DroneStatus.IN_FLIGHT);
        
        List<Drone> drones = Arrays.asList(testDrone, drone2);
        when(droneRepository.findAll()).thenReturn(drones);

        // Act
        List<DroneDTO> result = droneService.getAllDrones();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(droneRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve drone by ID successfully")
    void testGetDroneById_Success() {
        // Arrange
        when(droneRepository.findById(1L)).thenReturn(Optional.of(testDrone));

        // Act
        DroneDTO result = droneService.getDroneById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("DRN-001", result.getSerialNumber());
        assertEquals("Phantom 4", result.getModel());
        verify(droneRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when drone not found by ID")
    void testGetDroneById_NotFound() {
        // Arrange
        when(droneRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            droneService.getDroneById(999L);
        });
        verify(droneRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create drone successfully")
    void testCreateDrone_Success() {
        // Arrange
        Drone newDrone = new Drone();
        newDrone.setId(2L);
        newDrone.setSerialNumber(createRequest.getSerialNumber());
        newDrone.setModel(createRequest.getModel());
        newDrone.setManufacturer(createRequest.getManufacturer());
        newDrone.setStatus(DroneStatus.AVAILABLE);

        when(droneRepository.existsBySerialNumber("DRN-002")).thenReturn(false);
        when(droneRepository.save(any(Drone.class))).thenReturn(newDrone);

        // Act
        DroneDTO result = droneService.createDrone(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals("DRN-002", result.getSerialNumber());
        assertEquals("Mavic Pro", result.getModel());
        verify(droneRepository, times(1)).existsBySerialNumber("DRN-002");
        verify(droneRepository, times(1)).save(any(Drone.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when creating drone with duplicate serial")
    void testCreateDrone_DuplicateSerial() {
        // Arrange
        when(droneRepository.existsBySerialNumber(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> {
            droneService.createDrone(createRequest);
        });
        verify(droneRepository, times(1)).existsBySerialNumber(anyString());
        verify(droneRepository, never()).save(any(Drone.class));
    }

    @Test
    @DisplayName("Should update drone successfully")
    void testUpdateDrone_Success() {
        // Arrange
        UpdateDroneRequest updateRequest = new UpdateDroneRequest();
        updateRequest.setModel("Phantom 5");
        updateRequest.setManufacturer("DJI");

        when(droneRepository.findById(1L)).thenReturn(Optional.of(testDrone));
        when(droneRepository.save(any(Drone.class))).thenReturn(testDrone);

        // Act
        DroneDTO result = droneService.updateDrone(1L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(droneRepository, times(1)).findById(1L);
        verify(droneRepository, times(1)).save(testDrone);
    }

    @Test
    @DisplayName("Should update drone status successfully")
    void testUpdateDroneStatus_Success() {
        // Arrange
        when(droneRepository.findById(1L)).thenReturn(Optional.of(testDrone));
        when(droneRepository.save(any(Drone.class))).thenReturn(testDrone);

        // Act
        DroneDTO result = droneService.updateDroneStatus(1L, DroneStatus.MAINTENANCE);

        // Assert
        assertNotNull(result);
        verify(droneRepository, times(1)).findById(1L);
        verify(droneRepository, times(1)).save(testDrone);
    }

    @Test
    @DisplayName("Should delete drone successfully")
    void testDeleteDrone_Success() {
        // Arrange
        when(droneRepository.existsById(1L)).thenReturn(true);
        doNothing().when(droneRepository).deleteById(1L);

        // Act
        droneService.deleteDrone(1L);

        // Assert
        verify(droneRepository, times(1)).existsById(1L);
        verify(droneRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent drone")
    void testDeleteDrone_NotFound() {
        // Arrange
        when(droneRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            droneService.deleteDrone(999L);
        });
        verify(droneRepository, times(1)).existsById(999L);
        verify(droneRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should retrieve drone by serial number successfully")
    void testGetDroneBySerialNumber_Success() {
        // Arrange
        when(droneRepository.findBySerialNumber("DRN-001")).thenReturn(Optional.of(testDrone));

        // Act
        DroneDTO result = droneService.getDroneBySerialNumber("DRN-001");

        // Assert
        assertNotNull(result);
        assertEquals("DRN-001", result.getSerialNumber());
        verify(droneRepository, times(1)).findBySerialNumber("DRN-001");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when drone not found by serial")
    void testGetDroneBySerialNumber_NotFound() {
        // Arrange
        when(droneRepository.findBySerialNumber("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            droneService.getDroneBySerialNumber("INVALID");
        });
        verify(droneRepository, times(1)).findBySerialNumber("INVALID");
    }
}
