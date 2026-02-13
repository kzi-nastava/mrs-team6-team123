package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class GeocodingResult {
    @SerializedName("display_name")
    private String displayName;

    @SerializedName("lat")
    private String latitude;

    @SerializedName("lon")
    private String longitude;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public double getLatitudeDouble() {
        try {
            return Double.parseDouble(latitude);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public double getLongitudeDouble() {
        try {
            return Double.parseDouble(longitude);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public String toString() {
        return displayName != null ? displayName : "";
    }
}
