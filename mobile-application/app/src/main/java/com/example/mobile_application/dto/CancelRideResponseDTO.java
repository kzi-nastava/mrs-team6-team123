
package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class CancelRideResponseDTO {
    @SerializedName("rideId")
    private Long rideId;
    @SerializedName("cancelledBy")
    private Long cancelledBy;
    @SerializedName("reason")
    private String reason;
    @SerializedName("message")
    private String message;

    public CancelRideResponseDTO() {}

    public Long getRideId() { return rideId; }
    public void setRideId(Long rideId) { this.rideId = rideId; }
    public Long getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(Long cancelledBy) { this.cancelledBy = cancelledBy; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}