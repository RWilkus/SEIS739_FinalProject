package com.skycontrol.dto;

import java.time.LocalDateTime;

public class MaintenanceRecordDTO {
    private Long id;
    private String description;
    private LocalDateTime date;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}
