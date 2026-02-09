package com.example.mobile_application.dto.auth;

public class LoginRequestDTO {
    private String email;
    private String password;

    public LoginRequestDTO() {}
    public LoginRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
}