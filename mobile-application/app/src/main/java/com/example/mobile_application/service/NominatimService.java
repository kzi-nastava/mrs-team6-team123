package com.example.mobile_application.service;

import com.example.mobile_application.dto.GeocodingResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NominatimService {
    @GET("/search")
    Call<List<GeocodingResult>> searchAddress(
            @Query("q") String query,
            @Query("format") String format,
            @Query("limit") int limit,
            @Query("addressdetails") int addressDetails);

    @GET("/search")
    Call<List<GeocodingResult>> geocodeAddress(
            @Query("q") String address,
            @Query("format") String format,
            @Query("limit") int limit,
            @Query("addressdetails") int addressDetails);
}
