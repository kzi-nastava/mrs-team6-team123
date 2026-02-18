package com.example.mobile_application.helper;

import android.util.Log;

import com.example.mobile_application.dto.RideOrderRequestDTO;
import com.example.mobile_application.dto.RideResponseDTO;
import com.example.mobile_application.dto.UserProfileDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.RideOrderService;
import com.example.mobile_application.service.UserProfileService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Centralized service for handling ride booking operations.
 * Manages API calls and passenger email resolution.
 */
public class RideBookingService {

    private static final String TAG = "RideBookingService";
    private final RideOrderService rideOrderService;
    private final UserProfileService userProfileService;

    public RideBookingService() {
        this.rideOrderService = ApiClient.getInstance().create(RideOrderService.class);
        this.userProfileService = ApiClient.getInstance().create(UserProfileService.class);
    }

    public void submitRideOrder(
            RideOrderRequestDTO request,
            Callback<RideResponseDTO> onSuccess,
            Callback<RideResponseDTO> onError,
            Callback<RideResponseDTO> onFailure) {

        Log.d(TAG, "Submitting ride order with " + request.getPassengerIds().size() + " passengers");

        Call<RideResponseDTO> call = rideOrderService.orderRide(request);

        call.enqueue(new Callback<RideResponseDTO>() {
            @Override
            public void onResponse(Call<RideResponseDTO> call, Response<RideResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    onSuccess.onResponse(call, response);
                } else {
                    onError.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<RideResponseDTO> call, Throwable t) {
                onFailure.onFailure(call, t);
            }
        });
    }

    /**
     * Resolves a list of passenger emails to their user IDs
     */
    public void resolvePassengerEmails(
            List<String> emails,
            List<Long> passengerIds,
            Runnable onSuccess,
            Runnable onError) {

        if (emails == null || emails.isEmpty()) {
            onSuccess.run();
            return;
        }

        resolveEmailsRecursively(emails, passengerIds, 0, onSuccess, onError);
    }

    private void resolveEmailsRecursively(
            List<String> emails,
            List<Long> passengerIds,
            int index,
            Runnable onSuccess,
            Runnable onError) {

        if (index >= emails.size()) {
            Log.d(TAG, "All passenger emails resolved: " + passengerIds.size() + " passengers");
            onSuccess.run();
            return;
        }

        String email = emails.get(index);
        Log.d(TAG, "Resolving passenger email: " + email);

        userProfileService.getUserByEmail(email).enqueue(new Callback<UserProfileDTO>() {
            @Override
            public void onResponse(Call<UserProfileDTO> call, Response<UserProfileDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileDTO user = response.body();
                    if (user.getId() != null && !passengerIds.contains(user.getId())) {
                        passengerIds.add(user.getId());
                        Log.d(TAG, "Resolved " + email + " to user ID: " + user.getId());
                    }
                    resolveEmailsRecursively(emails, passengerIds, index + 1, onSuccess, onError);
                } else {
                    Log.e(TAG, "Failed to resolve email: " + email + ", status: " + response.code());
                    onError.run();
                }
            }

            @Override
            public void onFailure(Call<UserProfileDTO> call, Throwable t) {
                Log.e(TAG, "Network error resolving email: " + email, t);
                onError.run();
            }
        });
    }
}
