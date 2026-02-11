package com.example.mobile_application.service;

import com.example.mobile_application.dto.ChangePriceDTO;
import com.example.mobile_application.dto.PricingDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PricingService {
    @GET("api/pricing/get-pricing")
    Call<List<PricingDTO>> getPricing();

    @POST("api/pricing/change-price")
    Call<Void> changePrice(@Body ChangePriceDTO dto);
}
