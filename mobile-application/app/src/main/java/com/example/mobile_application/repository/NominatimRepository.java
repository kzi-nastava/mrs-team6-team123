
package com.example.mobile_application.repository;

import com.example.mobile_application.dto.NominatimResultDTO;
import com.example.mobile_application.service.NominatimApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NominatimRepository {
    private final NominatimApiService service;

    public NominatimRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://nominatim.openstreetmap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(NominatimApiService.class);
    }

    public void search(String query, int limit,
                       Callback<List<NominatimResultDTO>> callback) {
        Call<List<NominatimResultDTO>> call =
                service.search(query, "json", limit, 1);
        call.enqueue(callback);
    }

    public void geocode(String address,
                        Callback<List<NominatimResultDTO>> callback) {
        search(address, 1, callback);
    }

    public void searchSuggestions(String query,
                                  Callback<List<NominatimResultDTO>> callback) {
        search(query, 5, callback);
    }
}