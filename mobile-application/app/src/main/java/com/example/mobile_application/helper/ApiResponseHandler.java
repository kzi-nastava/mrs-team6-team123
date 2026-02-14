package com.example.mobile_application.helper;

import android.util.Log;

import retrofit2.Response;

/**
 * Utility class for handling API responses and error responses.
 * Centralizes error parsing and message extraction logic.
 */
public class ApiResponseHandler {
    public static String extractErrorMessage(Response<?> response, String defaultMessage, String tag) {
        if (response == null) {
            return defaultMessage;
        }

        String errorMessage = "Error " + response.code();

        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                if (errorBody != null && !errorBody.trim().isEmpty()) {
                    return errorBody;
                }
            }
        } catch (Exception e) {
            Log.e(tag, "Error parsing error body", e);
        }

        // Fallback to HTTP status message
        String message = response.message();
        if (message != null && !message.isEmpty()) {
            errorMessage = message;
        }

        return errorMessage;
    }

    public static <T> boolean isSuccessful(Response<T> response) {
        return response != null && response.isSuccessful() && response.body() != null;
    }

    public static int getResponseCode(Response<?> response) {
        return response != null ? response.code() : -1;
    }

    /**
     * Formats a generic API error message.
     */
    public static String formatErrorMessage(int statusCode, String message) {
        return "Error " + statusCode + (message != null ? " - " + message : "");
    }
}
