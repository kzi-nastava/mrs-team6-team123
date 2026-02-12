package com.example.mobile_application.dto.auth;

public class RegistrationRequestDTO {
    private String email;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String profilePicture;

    public RegistrationRequestDTO() {}
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String c) { this.confirmPassword = c; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String f) { this.firstName = f; }
    public String getLastName() { return lastName; }
    public void setLastName(String l) { this.lastName = l; }
    public String getAddress() { return address; }
    public void setAddress(String a) { this.address = a; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String p) { this.phoneNumber = p; }
    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String p) { this.profilePicture = p; }
}