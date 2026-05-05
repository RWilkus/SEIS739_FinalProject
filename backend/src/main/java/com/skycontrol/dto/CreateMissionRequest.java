package com.skycontrol.dto;

import com.skycontrol.model.MissionType;
import java.time.LocalDateTime;

public class CreateMissionRequest {
    private String name;
    private String description;
    private MissionType type;
    private LocalDateTime scheduledStartTime;
    private Long droneId;
    private Long pilotId;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public MissionType getType() { return type; }
    public void setType(MissionType type) { this.type = type; }
    public LocalDateTime getScheduledStartTime() { return scheduledStartTime; }
    public void setScheduledStartTime(LocalDateTime scheduledStartTime) { this.scheduledStartTime = scheduledStartTime; }
    public Long getDroneId() { return droneId; }
    public void setDroneId(Long droneId) { this.droneId = droneId; }
    public Long getPilotId() { return pilotId; }
    public void setPilotId(Long pilotId) { this.pilotId = pilotId; }
}
