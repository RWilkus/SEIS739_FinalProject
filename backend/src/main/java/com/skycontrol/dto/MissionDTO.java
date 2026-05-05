package com.skycontrol.dto;

import com.skycontrol.model.MissionStatus;
import com.skycontrol.model.MissionType;
import java.time.LocalDateTime;

public class MissionDTO {
    private Long id;
    private String name;
    private String description;
    private MissionType type;
    private MissionStatus status;
    private LocalDateTime scheduledStartTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime scheduledEndTime;
    private LocalDateTime actualEndTime;
    private Long droneId;
    private String droneSerialNumber;
    private Long pilotId;
    private String pilotName;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public MissionDTO() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public MissionType getType() {
        return type;
    }
    
    public void setType(MissionType type) {
        this.type = type;
    }
    
    public MissionStatus getStatus() {
        return status;
    }
    
    public void setStatus(MissionStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getScheduledStartTime() {
        return scheduledStartTime;
    }
    
    public void setScheduledStartTime(LocalDateTime scheduledStartTime) {
        this.scheduledStartTime = scheduledStartTime;
    }
    
    public LocalDateTime getActualStartTime() {
        return actualStartTime;
    }
    
    public void setActualStartTime(LocalDateTime actualStartTime) {
        this.actualStartTime = actualStartTime;
    }
    
    public LocalDateTime getScheduledEndTime() {
        return scheduledEndTime;
    }
    
    public void setScheduledEndTime(LocalDateTime scheduledEndTime) {
        this.scheduledEndTime = scheduledEndTime;
    }
    
    public LocalDateTime getActualEndTime() {
        return actualEndTime;
    }
    
    public void setActualEndTime(LocalDateTime actualEndTime) {
        this.actualEndTime = actualEndTime;
    }
    
    public Long getDroneId() {
        return droneId;
    }
    
    public void setDroneId(Long droneId) {
        this.droneId = droneId;
    }
    
    public String getDroneSerialNumber() {
        return droneSerialNumber;
    }
    
    public void setDroneSerialNumber(String droneSerialNumber) {
        this.droneSerialNumber = droneSerialNumber;
    }
    
    public Long getPilotId() {
        return pilotId;
    }
    
    public void setPilotId(Long pilotId) {
        this.pilotId = pilotId;
    }
    
    public String getPilotName() {
        return pilotName;
    }
    
    public void setPilotName(String pilotName) {
        this.pilotName = pilotName;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
