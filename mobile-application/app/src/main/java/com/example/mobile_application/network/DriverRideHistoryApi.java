package com.example.mobile_application.network;

import com.example.mobile_application.model.DriverRideHistoryDTO;

import java.time.LocalDate;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DriverRideHistoryApi {

    @GET("api/ride-history/{driverId}/rides")
    Call<List<DriverRideHistoryDTO>> getDriverRideHistory(
            @Path("driverId") Long driverId,
            @Query("from") LocalDate from,
            @Query("to") LocalDate to
    );
}
