package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class CancelRideRequestDTO {
    @SerializedName("userId")
    private Long userId;
    @SerializedName("reason")
    private String reason;

    public CancelRideRequestDTO() {}

    public CancelRideRequestDTO(Long userId, String reason) {
        this.userId = userId;
        this.reason = reason;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}