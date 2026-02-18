package com.example.mobile_application.repository;

import com.example.mobile_application.dto.IrregularityReportDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.IrregularityReportService;

import retrofit2.Call;
import retrofit2.Callback;

public class IrregularityReportRepository {
    private final IrregularityReportService service;

    public IrregularityReportRepository() {
        this.service = ApiClient.getInstance().create(IrregularityReportService.class);
    }

    public void reportDriver(IrregularityReportDTO dto, Callback<Void> callback) {
        Call<Void> call = service.reportDriver(dto);
        call.enqueue(callback);
    }
}
