package com.example.mobile_application.dto.auth;

public class LoginResponseDTO {
    private String token;
    private Long userId;
    private String email;
    private String role;

    public LoginResponseDTO() {}
    public String getToken() { return token; }
    public void setToken(String t) { this.token = t; }
    public Long getUserId() { return userId; }
    public void setUserId(Long id) { this.userId = id; }
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public String getRole() { return role; }
    public void setRole(String r) { this.role = r; }
}
