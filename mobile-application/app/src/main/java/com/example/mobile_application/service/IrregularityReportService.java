package com.example.mobile_application.service;

import com.example.mobile_application.dto.IrregularityReportDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IrregularityReportService {
    @POST("api/drivers/report")
    Call<Void> reportDriver(@Body IrregularityReportDTO dto);
}
