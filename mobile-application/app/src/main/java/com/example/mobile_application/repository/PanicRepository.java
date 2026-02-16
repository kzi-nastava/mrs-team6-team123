
package com.example.mobile_application.repository;

import com.example.mobile_application.dto.PanicAlertRequestDTO;
import com.example.mobile_application.dto.PanicAlertResponseDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.PanicService;

import retrofit2.Call;
import retrofit2.Callback;

public class PanicRepository {
    private final PanicService service;

    public PanicRepository() {
        service = ApiClient.getInstance().create(PanicService.class);
    }

    public void triggerPanic(PanicAlertRequestDTO request,
                             Callback<PanicAlertResponseDTO> callback) {
        Call<PanicAlertResponseDTO> call = service.triggerPanic(request);

        android.util.Log.d("PANIC_DEBUG", "URL: " + call.request().url());
        android.util.Log.d("PANIC_DEBUG", "RideId: " + request.getRideId()
                + " UserId: " + request.getUserId());

        call.enqueue(callback);
    }
}