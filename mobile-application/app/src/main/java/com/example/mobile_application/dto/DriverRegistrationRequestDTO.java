package com.example.mobile_application.dto;

public class DriverRegistrationRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phone;
    private String vehicleModel;
    private String vehicleType; // STANDARD | LUXURY | VAN
    private String licensePlate;
    private int seats;
    private boolean babyTransport;
    private boolean petTransport;

    public DriverRegistrationRequestDTO() {
    }

    public DriverRegistrationRequestDTO(String firstName, String lastName, String email,
            String address, String phone, String vehicleModel,
            String vehicleType, String licensePlate, int seats,
            boolean babyTransport, boolean petTransport) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.vehicleModel = vehicleModel;
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.seats = seats;
        this.babyTransport = babyTransport;
        this.petTransport = petTransport;
    }

    // Getters and setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public boolean isBabyTransport() {
        return babyTransport;
    }

    public void setBabyTransport(boolean babyTransport) {
        this.babyTransport = babyTransport;
    }

    public boolean isPetTransport() {
        return petTransport;
    }

    public void setPetTransport(boolean petTransport) {
        this.petTransport = petTransport;
    }
}
