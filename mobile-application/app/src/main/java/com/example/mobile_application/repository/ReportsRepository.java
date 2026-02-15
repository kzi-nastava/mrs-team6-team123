package com.example.mobile_application.repository;

import com.example.mobile_application.dto.StatisticsDTO;
import com.example.mobile_application.dto.UserBasicInfoDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.ReportsService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ReportsRepository {
    private final ReportsService service;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public ReportsRepository() {
        service = ApiClient.getInstance().create(ReportsService.class);
    }

    public void getStatistics(
            Long userId,
            String userType,
            Long filteredUserId,
            String filteredUserType,
            LocalDate fromDate,
            LocalDate toDate,
            Callback<StatisticsDTO> callback) {
        
        String fromDateStr = fromDate != null ? fromDate.format(DATE_FORMATTER) : null;
        String toDateStr = toDate != null ? toDate.format(DATE_FORMATTER) : null;
        
        Call<StatisticsDTO> call = service.getStatistics(
                userId,
                userType,
                filteredUserId,
                filteredUserType,
                fromDateStr,
                toDateStr
        );
        call.enqueue(callback);
    }

    public void getAllActiveUsers(Long excludeUserId, Callback<List<UserBasicInfoDTO>> callback) {
        Call<List<UserBasicInfoDTO>> call = service.getAllActiveUsers(excludeUserId);
        call.enqueue(callback);
    }

    public void getAllPassengers(Callback<List<UserBasicInfoDTO>> callback) {
        Call<List<UserBasicInfoDTO>> call = service.getAllPassengers();
        call.enqueue(callback);
    }
}
