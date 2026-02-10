package com.example.mobile_application.repository;

import com.example.mobile_application.dto.ChangePriceDTO;
import com.example.mobile_application.dto.PricingDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.PricingService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class PricingRepository {
    private final PricingService service;

    public PricingRepository() {
        this.service = ApiClient.getInstance().create(PricingService.class);
    }

    public void getPricing(Callback<List<PricingDTO>> callback) {
        Call<List<PricingDTO>> call = service.getPricing();
        call.enqueue(callback);
    }

    public void changePrice(ChangePriceDTO dto, Callback<Void> callback) {
        Call<Void> call = service.changePrice(dto);
        call.enqueue(callback);
    }
}
