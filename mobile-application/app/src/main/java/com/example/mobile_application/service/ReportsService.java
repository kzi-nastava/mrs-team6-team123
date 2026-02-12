package com.example.mobile_application.service;

import com.example.mobile_application.dto.StatisticsDTO;
import com.example.mobile_application.dto.UserBasicInfoDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ReportsService {

    @GET("api/reports/statistics")
    Call<StatisticsDTO> getStatistics(
            @Query(value = "userId", encoded = false) Long userId,
            @Query(value = "userType", encoded = false) String userType,
            @Query(value = "filteredUserId", encoded = false) Long filteredUserId,
            @Query(value = "filteredUserType", encoded = false) String filteredUserType,
            @Query(value = "fromDate", encoded = false) String fromDate,
            @Query(value = "toDate", encoded = false) String toDate
    );

    @GET("api/reports/users")
    Call<List<UserBasicInfoDTO>> getAllActiveUsers(
            @Query(value = "excludeUserId", encoded = false) Long excludeUserId
    );

    @GET("api/reports/passengers")
    Call<List<UserBasicInfoDTO>> getAllPassengers();
}
