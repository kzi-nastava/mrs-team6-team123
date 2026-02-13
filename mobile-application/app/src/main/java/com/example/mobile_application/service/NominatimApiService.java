
package com.example.mobile_application.service;

import com.example.mobile_application.dto.NominatimResultDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NominatimApiService {
    @GET("search")
    Call<List<NominatimResultDTO>> search(
            @Query("q") String query,
            @Query("format") String format,
            @Query("limit") int limit,
            @Query("addressdetails") int addressDetails
    );
}
