package com.example.mobile_application.service;

import android.util.Log;

import com.example.mobile_application.dto.RideEstimationRequestDTO;
import com.example.mobile_application.dto.RideEstimationResponseDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Service for estimating ride price and distance before booking.
 * Communicates with backend /api/rides/estimate endpoint.
 */
public class RideEstimationService {

    private static final String TAG = "RideEstimationService";
    private final RideOrderService rideOrderService;

    public RideEstimationService() {
        this.rideOrderService = ApiClient.getInstance().create(RideOrderService.class);
    }

    public void estimateRide(
            RideEstimationRequestDTO request,
            Callback<RideEstimationResponseDTO> onSuccess,
            Callback<RideEstimationResponseDTO> onError) {

        Log.d(TAG, "Estimating ride from " + request.getStartLocation() + " to " + request.getEndLocation());

        Call<RideEstimationResponseDTO> call = rideOrderService.estimateRide(request);

        call.enqueue(new Callback<RideEstimationResponseDTO>() {
            @Override
            public void onResponse(Call<RideEstimationResponseDTO> call, Response<RideEstimationResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Estimation successful: distance=" + response.body().getEstimatedDistance() +
                            "km, price=" + response.body().getEstimatedPrice());
                    onSuccess.onResponse(call, response);
                } else {
                    Log.e(TAG, "Estimation failed: " + response.code());
                    onError.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<RideEstimationResponseDTO> call, Throwable t) {
                Log.e(TAG, "Estimation network error", t);
                // Create a failed response for error callback
                onError.onFailure(call, t);
            }
        });
    }
}
