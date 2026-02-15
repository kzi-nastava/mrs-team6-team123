
package com.example.mobile_application.repository;

import com.example.mobile_application.dto.AdminRideHistoryDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.AdminRideHistoryApiService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

public class AdminRideHistoryRepository {
    private final AdminRideHistoryApiService service;

    public AdminRideHistoryRepository() {
        service = ApiClient.getInstance().create(AdminRideHistoryApiService.class);
    }

    public void getAllRideHistory(String from, String to,
                                  String sortBy, String sortOrder,
                                  Callback<List<AdminRideHistoryDTO>> callback) {
        Call<List<AdminRideHistoryDTO>> call =
                service.getAllRideHistory(from, to, sortBy, sortOrder);
        call.enqueue(callback);
    }

    public void getUserRideHistory(Long userId, String from, String to,
                                   String sortBy, String sortOrder,
                                   Callback<List<AdminRideHistoryDTO>> callback) {
        Call<List<AdminRideHistoryDTO>> call =
                service.getUserRideHistory(userId, from, to, sortBy, sortOrder);
        call.enqueue(callback);
    }

    public void getRideDetails(Long rideId,
                               Callback<AdminRideHistoryDTO> callback) {
        Call<AdminRideHistoryDTO> call = service.getRideDetails(rideId);
        call.enqueue(callback);
    }
}