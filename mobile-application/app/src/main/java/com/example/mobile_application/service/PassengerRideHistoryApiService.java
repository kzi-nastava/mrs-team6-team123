
package com.example.mobile_application.service;

import com.example.mobile_application.dto.PassengerRideHistoryDTO;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PassengerRideHistoryApiService {

    @GET("api/passenger/{passengerId}/rides")
    Call<List<PassengerRideHistoryDTO>> getRideHistory(
            @Path("passengerId") Long passengerId,
            @Query("from") String from,
            @Query("to") String to,
            @Query("sortBy") String sortBy,
            @Query("sortOrder") String sortOrder
    );

    @GET("api/passenger/{passengerId}/rides/{rideId}")
    Call<PassengerRideHistoryDTO> getRideDetails(
            @Path("passengerId") Long passengerId,
            @Path("rideId") Long rideId
    );
}
