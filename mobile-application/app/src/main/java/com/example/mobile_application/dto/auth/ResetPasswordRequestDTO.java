package com.example.mobile_application.dto.auth;

public class ResetPasswordRequestDTO {
    private String token;
    private String newPassword;
    private String confirmPassword;

    public ResetPasswordRequestDTO() {}
    public ResetPasswordRequestDTO(String token, String np, String cp) {
        this.token = token; this.newPassword = np; this.confirmPassword = cp;
    }
    public String getToken() { return token; }
    public void setToken(String t) { this.token = t; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String p) { this.newPassword = p; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String c) { this.confirmPassword = c; }
}