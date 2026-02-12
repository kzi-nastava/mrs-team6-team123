package com.example.mobile_application.dto.auth;

public class LogoutRequestDTO {
    private Long userId;
    public LogoutRequestDTO() {}
    public LogoutRequestDTO(Long userId) { this.userId = userId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long id) { this.userId = id; }
}