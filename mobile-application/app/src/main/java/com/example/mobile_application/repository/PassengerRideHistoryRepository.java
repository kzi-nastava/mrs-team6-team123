
package com.example.mobile_application.repository;

import com.example.mobile_application.dto.PassengerRideHistoryDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.PassengerRideHistoryApiService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

public class PassengerRideHistoryRepository {
    private final PassengerRideHistoryApiService service;

    public PassengerRideHistoryRepository() {
        service = ApiClient.getInstance().create(PassengerRideHistoryApiService.class);
    }

    public void getRideHistory(Long passengerId, String from, String to,
                               String sortBy, String sortOrder,
                               Callback<List<PassengerRideHistoryDTO>> callback) {
        Call<List<PassengerRideHistoryDTO>> call =
                service.getRideHistory(passengerId, from, to, sortBy, sortOrder);
        call.enqueue(callback);
    }

    public void getRideDetails(Long passengerId, Long rideId,
                               Callback<PassengerRideHistoryDTO> callback) {
        Call<PassengerRideHistoryDTO> call =
                service.getRideDetails(passengerId, rideId);
        call.enqueue(callback);
    }
}