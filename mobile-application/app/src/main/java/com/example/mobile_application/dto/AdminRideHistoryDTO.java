
package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AdminRideHistoryDTO implements Serializable {
    @SerializedName("rideId")
    private Long rideId;
    @SerializedName("startLocation")
    private String startLocation;
    @SerializedName("endLocation")
    private String endLocation;
    @SerializedName("startedAt")
    private String startedAt;
    @SerializedName("endedAt")
    private String endedAt;
    @SerializedName("date")
    private String date;
    @SerializedName("price")
    private double price;
    @SerializedName("totalDistance")
    private double totalDistance;
    @SerializedName("startLat")
    private double startLat;
    @SerializedName("startLng")
    private double startLng;
    @SerializedName("endLat")
    private double endLat;
    @SerializedName("endLng")
    private double endLng;
    @SerializedName("driverId")
    private Long driverId;
    @SerializedName("driverName")
    private String driverName;
    @SerializedName("driverPhoto")
    private String driverPhoto;
    @SerializedName("creatorId")
    private Long creatorId;
    @SerializedName("creatorName")
    private String creatorName;
    @SerializedName("passengers")
    private List<PassengerInfoDTO> passengers = new ArrayList<>();
    @SerializedName("cancelled")
    private boolean cancelled;
    @SerializedName("cancelledByUserId")
    private Long cancelledByUserId;
    @SerializedName("cancelledByName")
    private String cancelledByName;
    @SerializedName("cancelledByRole")
    private String cancelledByRole;
    @SerializedName("panicTriggered")
    private boolean panicTriggered;
    @SerializedName("driverRating")
    private double driverRating;
    @SerializedName("vehicleRating")
    private double vehicleRating;
    @SerializedName("rated")
    private boolean rated;
    @SerializedName("inconsistencyReports")
    private List<String> inconsistencyReports = new ArrayList<>();
    @SerializedName("routeId")
    private Long routeId;

    public AdminRideHistoryDTO() {}

    public Long getRideId() { return rideId; }
    public void setRideId(Long v) { this.rideId = v; }
    public String getStartLocation() { return startLocation; }
    public void setStartLocation(String v) { this.startLocation = v; }
    public String getEndLocation() { return endLocation; }
    public void setEndLocation(String v) { this.endLocation = v; }
    public String getStartedAt() { return startedAt; }
    public void setStartedAt(String v) { this.startedAt = v; }
    public String getEndedAt() { return endedAt; }
    public void setEndedAt(String v) { this.endedAt = v; }
    public String getDate() { return date; }
    public void setDate(String v) { this.date = v; }
    public double getPrice() { return price; }
    public void setPrice(double v) { this.price = v; }
    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double v) { this.totalDistance = v; }
    public double getStartLat() { return startLat; }
    public void setStartLat(double v) { this.startLat = v; }
    public double getStartLng() { return startLng; }
    public void setStartLng(double v) { this.startLng = v; }
    public double getEndLat() { return endLat; }
    public void setEndLat(double v) { this.endLat = v; }
    public double getEndLng() { return endLng; }
    public void setEndLng(double v) { this.endLng = v; }
    public Long getDriverId() { return driverId; }
    public void setDriverId(Long v) { this.driverId = v; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String v) { this.driverName = v; }
    public String getDriverPhoto() { return driverPhoto; }
    public void setDriverPhoto(String v) { this.driverPhoto = v; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long v) { this.creatorId = v; }
    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String v) { this.creatorName = v; }
    public List<PassengerInfoDTO> getPassengers() { return passengers; }
    public void setPassengers(List<PassengerInfoDTO> v) { this.passengers = v; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean v) { this.cancelled = v; }
    public Long getCancelledByUserId() { return cancelledByUserId; }
    public void setCancelledByUserId(Long v) { this.cancelledByUserId = v; }
    public String getCancelledByName() { return cancelledByName; }
    public void setCancelledByName(String v) { this.cancelledByName = v; }
    public String getCancelledByRole() { return cancelledByRole; }
    public void setCancelledByRole(String v) { this.cancelledByRole = v; }
    public boolean isPanicTriggered() { return panicTriggered; }
    public void setPanicTriggered(boolean v) { this.panicTriggered = v; }
    public double getDriverRating() { return driverRating; }
    public void setDriverRating(double v) { this.driverRating = v; }
    public double getVehicleRating() { return vehicleRating; }
    public void setVehicleRating(double v) { this.vehicleRating = v; }
    public boolean isRated() { return rated; }
    public void setRated(boolean v) { this.rated = v; }
    public List<String> getInconsistencyReports() { return inconsistencyReports; }
    public void setInconsistencyReports(List<String> v) { this.inconsistencyReports = v; }
    public Long getRouteId() { return routeId; }
    public void setRouteId(Long v) { this.routeId = v; }
}
