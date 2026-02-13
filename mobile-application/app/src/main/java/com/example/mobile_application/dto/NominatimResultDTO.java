
package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class NominatimResultDTO {
    @SerializedName("display_name")
    private String displayName;
    @SerializedName("lat")
    private String lat;
    @SerializedName("lon")
    private String lon;

    public NominatimResultDTO() {}

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String s) { this.displayName = s; }
    public String getLat() { return lat; }
    public void setLat(String s) { this.lat = s; }
    public String getLon() { return lon; }
    public void setLon(String s) { this.lon = s; }

    public String toCoordinateString() {
        return lat + ", " + lon;
    }

    public double getLatitude() { return Double.parseDouble(lat); }
    public double getLongitude() { return Double.parseDouble(lon); }
}
