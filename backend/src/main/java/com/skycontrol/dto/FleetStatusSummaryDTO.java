package com.skycontrol.dto;

public class FleetStatusSummaryDTO {
    private int available;
    private int inFlight;
    private int maintenance;
    
    public int getAvailable() { return available; }
    public void setAvailable(int available) { this.available = available; }
    public int getInFlight() { return inFlight; }
    public void setInFlight(int inFlight) { this.inFlight = inFlight; }
    public int getMaintenance() { return maintenance; }
    public void setMaintenance(int maintenance) { this.maintenance = maintenance; }
}
