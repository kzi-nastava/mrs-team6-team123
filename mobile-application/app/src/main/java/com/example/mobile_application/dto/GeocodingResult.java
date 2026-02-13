package com.example.mobile_application.dto;

import com.google.gson.annotations.SerializedName;

public class GeocodingResult {
    @SerializedName("display_name")
    private String displayName;

    @SerializedName("lat")
    private String latitude;

    @SerializedName("lon")
    private String longitude;

    @SerializedName("address")
    private AddressComponents addressComponents;

    public static class AddressComponents {
        @SerializedName("road")
        public String road;

        @SerializedName("house_number")
        public String houseNumber;

        @SerializedName("city")
        public String city;

        @SerializedName("town")
        public String town;

        @SerializedName("village")
        public String village;

        @SerializedName("county")
        public String county;

        @SerializedName("postcode")
        public String postcode;

        @SerializedName("country")
        public String country;

        /**
         * Get city, preferring 'city' over 'town' over 'village'
         */
        public String getCity() {
            if (city != null && !city.isEmpty()) {
                return city;
            }
            if (town != null && !town.isEmpty()) {
                return town;
            }
            if (village != null && !village.isEmpty()) {
                return village;
            }
            return null;
        }
    }

    public String getDisplayName() {
        // Try to format using structured components first
        if (addressComponents != null) {
            return formatFromComponents();
        }
        // Fallback to parsing the display_name string
        return formatDisplayName(displayName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the raw unformatted display name from Nominatim
     */
    public String getRawDisplayName() {
        return displayName;
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

    public AddressComponents getAddressComponents() {
        return addressComponents;
    }

    public void setAddressComponents(AddressComponents addressComponents) {
        this.addressComponents = addressComponents;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    /**
     * Format address using structured components like GraphHopper does in Angular
     * Result: "Street Number, City"
     */
    private String formatFromComponents() {
        String street = "";
        String city = "";

        if (addressComponents.road != null) {
            street = addressComponents.road;
            if (addressComponents.houseNumber != null) {
                street = street + " " + addressComponents.houseNumber;
            }
        }

        city = addressComponents.getCity();

        // Build result
        if (street != null && !street.isEmpty() && city != null && !city.isEmpty()) {
            return street + ", " + city;
        } else if (street != null && !street.isEmpty()) {
            return street;
        } else if (city != null && !city.isEmpty()) {
            return city;
        }

        return displayName;
    }

    private String formatDisplayName(String fullDisplayName) {
        try {
            if (fullDisplayName == null || fullDisplayName.trim().isEmpty()) {
                return fullDisplayName;
            }

            // Split by comma to get address components
            String[] parts = fullDisplayName.split(",");
            if (parts.length < 2) {
                return fullDisplayName;
            }

            // Clean up parts (trim whitespace)
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }

            String city = "";
            for (int i = parts.length - 1; i >= Math.max(0, parts.length - 3); i--) {
                String part = parts[i];
                // Skip very short parts and numbers-only parts
                if (part.length() > 2 && !part.matches("\\d+")) {
                    // Check if it's not a country code
                    if (!part.equals("Србија") && !part.equals("Serbia") && part.length() < 30) {
                        city = part;
                        break;
                    }
                }
            }

            // Usually street name is followed by a number
            String street = "";
            for (int i = 0; i < parts.length - 1; i++) {
                String part = parts[i];
                String nextPart = (i + 1 < parts.length) ? parts[i + 1] : "";

                // Look for patterns like "Street Name" followed by "number"
                if (part.length() > 2 && !part.matches("\\d+")) {
                    // Check if next part is a number or if current part contains a number
                    if (nextPart.matches("\\d+") || part.matches(".*\\d+.*")) {
                        street = part;
                        if (nextPart.matches("\\d+")) {
                            street = street + " " + nextPart;
                        }
                        break;
                    }
                }
            }

            // If we couldn't find street properly, use the earliest non-numeric,
            // non-country part
            if (street.isEmpty()) {
                for (int i = 0; i < parts.length; i++) {
                    String part = parts[i];
                    if (part.length() > 2 && !part.matches("\\d+") && !part.equals(city)
                            && !part.equals("Србија") && !part.equals("Serbia")) {
                        street = part;
                        break;
                    }
                }
            }

            // Build result: street, city
            if (!street.isEmpty() && !city.isEmpty()) {
                return street + ", " + city;
            } else if (!street.isEmpty()) {
                return street;
            } else if (!city.isEmpty()) {
                return city;
            }

            return fullDisplayName;
        } catch (Exception e) {
            // If any error occurs, return the original display name
            return fullDisplayName;
        }
    }
}
