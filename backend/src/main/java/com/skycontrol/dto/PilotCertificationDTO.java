package com.skycontrol.dto;

import java.time.LocalDate;

public class PilotCertificationDTO {
    private String type;
    private LocalDate expiryDate;
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
}
