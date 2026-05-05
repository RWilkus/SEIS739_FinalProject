package com.skycontrol.dto;

public class OverallStatisticsDTO {
    private int totalFlightHours;
    private int completedMissions;
    
    public int getTotalFlightHours() { return totalFlightHours; }
    public void setTotalFlightHours(int totalFlightHours) { this.totalFlightHours = totalFlightHours; }
    public int getCompletedMissions() { return completedMissions; }
    public void setCompletedMissions(int completedMissions) { this.completedMissions = completedMissions; }
}
