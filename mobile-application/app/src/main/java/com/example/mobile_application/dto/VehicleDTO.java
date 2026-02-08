package com.example.mobile_application.dto;

public class VehicleDTO {
    private String model;
    private String type;
    private String licensePlate;
    private int capacity;
    private boolean babiesAllowed;
    private boolean petsAllowed;

    public VehicleDTO() {
    }

    public VehicleDTO(String model, String type, String licensePlate, int capacity,
            boolean babiesAllowed, boolean petsAllowed) {
        this.model = model;
        this.type = type;
        this.licensePlate = licensePlate;
        this.capacity = capacity;
        this.babiesAllowed = babiesAllowed;
        this.petsAllowed = petsAllowed;
    }

    // Getters and Setters
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isBabiesAllowed() {
        return babiesAllowed;
    }

    public void setBabiesAllowed(boolean babiesAllowed) {
        this.babiesAllowed = babiesAllowed;
    }

    public boolean isPetsAllowed() {
        return petsAllowed;
    }

    public void setPetsAllowed(boolean petsAllowed) {
        this.petsAllowed = petsAllowed;
    }
}
