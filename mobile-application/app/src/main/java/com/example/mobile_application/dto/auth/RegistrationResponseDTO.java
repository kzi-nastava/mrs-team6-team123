package com.example.mobile_application.dto.auth;

public class RegistrationResponseDTO {
    private String message;
    private Long userId;
    private String email;

    public RegistrationResponseDTO() {}
    public String getMessage() { return message; }
    public void setMessage(String m) { this.message = m; }
    public Long getUserId() { return userId; }
    public void setUserId(Long id) { this.userId = id; }
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
}