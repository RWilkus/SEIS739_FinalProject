package com.skycontrol.dto;

public class DashboardDTO {
    private int totalDrones;
    private int activeDrones;
    private int totalMissions;
    private int activePilots;
    
    public int getTotalDrones() { return totalDrones; }
    public void setTotalDrones(int totalDrones) { this.totalDrones = totalDrones; }
    public int getActiveDrones() { return activeDrones; }
    public void setActiveDrones(int activeDrones) { this.activeDrones = activeDrones; }
    public int getTotalMissions() { return totalMissions; }
    public void setTotalMissions(int totalMissions) { this.totalMissions = totalMissions; }
    public int getActivePilots() { return activePilots; }
    public void setActivePilots(int activePilots) { this.activePilots = activePilots; }
}
