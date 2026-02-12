package com.example.mobile_application.dto.auth;

public class LogoutResponseDTO {
    private boolean success;
    private String message;
    public LogoutResponseDTO() {}
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean s) { this.success = s; }
    public String getMessage() { return message; }
    public void setMessage(String m) { this.message = m; }
}