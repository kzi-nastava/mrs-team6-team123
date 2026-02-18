
package com.example.mobile_application.service;

import com.example.mobile_application.dto.AdminRideHistoryDTO;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminRideHistoryApiService {

    @GET("api/admin/ride-history")
    Call<List<AdminRideHistoryDTO>> getAllRideHistory(
            @Query("from") String from,
            @Query("to") String to,
            @Query("sortBy") String sortBy,
            @Query("sortOrder") String sortOrder
    );

    @GET("api/admin/ride-history/user/{userId}")
    Call<List<AdminRideHistoryDTO>> getUserRideHistory(
            @Path("userId") Long userId,
            @Query("from") String from,
            @Query("to") String to,
            @Query("sortBy") String sortBy,
            @Query("sortOrder") String sortOrder
    );

    @GET("api/admin/ride-history/{rideId}")
    Call<AdminRideHistoryDTO> getRideDetails(
            @Path("rideId") Long rideId
    );
}