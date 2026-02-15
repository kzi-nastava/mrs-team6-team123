package com.example.mobile_application.service;

import com.example.mobile_application.dto.GeocodingResult;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeocodingService {
    private static final String NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org";
    private static GeocodingService instance;
    private final NominatimService nominatimService;

    private GeocodingService() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> chain.proceed(
                        chain.request().newBuilder()
                                .header("User-Agent", "MobileRideApp/1.0")
                                .build()))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NOMINATIM_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        nominatimService = retrofit.create(NominatimService.class);
    }

    public static synchronized GeocodingService getInstance() {
        if (instance == null) {
            instance = new GeocodingService();
        }
        return instance;
    }

    public void searchAddress(String query, Callback<List<GeocodingResult>> callback) {
        if (query == null || query.length() < 3) {
            return;
        }

        Call<List<GeocodingResult>> call = nominatimService.searchAddress(
                query,
                "json",
                5,
                1);
        call.enqueue(callback);
    }

    public void geocodeAddress(String address, Callback<List<GeocodingResult>> callback) {
        Call<List<GeocodingResult>> call = nominatimService.geocodeAddress(
                address,
                "json",
                1,
                1);
        call.enqueue(callback);
    }
}
