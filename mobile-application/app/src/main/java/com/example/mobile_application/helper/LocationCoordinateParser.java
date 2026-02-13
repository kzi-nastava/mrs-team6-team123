package com.example.mobile_application.helper;

import android.util.Log;

/**
 * Utility class for parsing and validating geographic coordinates.
 * Handles conversion between string and double array formats.
 */
public class LocationCoordinateParser {

    private static final String TAG = "LocationCoordinateParser";

    public static double[] parseCoordinates(String coordinateString) {
        if (coordinateString == null || coordinateString.trim().isEmpty()) {
            Log.w(TAG, "Empty coordinate string provided");
            return null;
        }

        try {
            String[] parts = coordinateString.split(",");
            if (parts.length != 2) {
                Log.e(TAG, "Invalid coordinate format: " + coordinateString);
                return null;
            }

            double latitude = Double.parseDouble(parts[0].trim());
            double longitude = Double.parseDouble(parts[1].trim());

            return new double[] { latitude, longitude };
        } catch (NumberFormatException e) {
            Log.e(TAG, "Failed to parse coordinates: " + coordinateString, e);
            return null;
        }
    }

    public static boolean isValidCoordinate(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 &&
                longitude >= -180 && longitude <= 180;
    }

    public static String coordinatesToString(double latitude, double longitude) {
        return latitude + ", " + longitude;
    }

    public static boolean isValidCoordinateString(String coordinateString) {
        double[] parsed = parseCoordinates(coordinateString);
        if (parsed == null) {
            return false;
        }
        return isValidCoordinate(parsed[0], parsed[1]);
    }
}
