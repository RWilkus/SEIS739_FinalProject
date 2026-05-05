package com.skycontrol.dto;

public class DroneMaintenanceStatusDTO {
    private boolean needsMaintenance;
    private int daysSinceLastMaintenance;
    
    public boolean isNeedsMaintenance() { return needsMaintenance; }
    public void setNeedsMaintenance(boolean needsMaintenance) { this.needsMaintenance = needsMaintenance; }
    public int getDaysSinceLastMaintenance() { return daysSinceLastMaintenance; }
    public void setDaysSinceLastMaintenance(int daysSinceLastMaintenance) { this.daysSinceLastMaintenance = daysSinceLastMaintenance; }
}
