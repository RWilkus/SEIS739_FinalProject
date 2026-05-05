package com.skycontrol.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "missions")
public class Mission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionStatus status;
    
    @Column(nullable = false)
    private LocalDateTime scheduledStartTime;
    
    private LocalDateTime actualStartTime;
    
    private LocalDateTime scheduledEndTime;
    
    private LocalDateTime actualEndTime;
    
    @ManyToOne
    @JoinColumn(name = "drone_id")
    private Drone drone;
    
    @ManyToOne
    @JoinColumn(name = "pilot_id")
    private Pilot pilot;
    
    private String location;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Mission() {}
    
    public Mission(String name, MissionType type, MissionStatus status, LocalDateTime scheduledStartTime) {
        this.name = name;
        this.type = type;
        this.status = status;
        this.scheduledStartTime = scheduledStartTime;
    }
    
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
    
    public Drone getDrone() {
        return drone;
    }
    
    public void setDrone(Drone drone) {
        this.drone = drone;
    }
    
    public Pilot getPilot() {
        return pilot;
    }
    
    public void setPilot(Pilot pilot) {
        this.pilot = pilot;
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
