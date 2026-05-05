package com.skycontrol.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateDroneRequest {
    
    @NotBlank(message = "Serial number is required")
    private String serialNumber;
    
    @NotBlank(message = "Model is required")
    private String model;
    
    @NotBlank(message = "Manufacturer is required")
    private String manufacturer;
    
    // Getters and Setters
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getManufacturer() {
        return manufacturer;
    }
    
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
}
