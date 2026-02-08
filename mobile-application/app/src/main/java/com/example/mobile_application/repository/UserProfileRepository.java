package com.example.mobile_application.repository;

import com.example.mobile_application.dto.ChangePasswordRequestDTO;
import com.example.mobile_application.dto.UserProfileDTO;
import com.example.mobile_application.dto.UserProfileRequestDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.UserProfileService;

import retrofit2.Call;
import retrofit2.Callback;

public class UserProfileRepository {
    private final UserProfileService service;

    public UserProfileRepository() {
        this.service = ApiClient.getInstance().create(UserProfileService.class);
    }

    public void getProfile(Long userId, Callback<UserProfileDTO> callback) {
        Call<UserProfileDTO> call = service.getProfile(userId);
        call.enqueue(callback);
    }

    public void updateProfile(Long userId, UserProfileRequestDTO request, Callback<UserProfileDTO> callback) {
        Call<UserProfileDTO> call = service.updateProfile(userId, request);
        call.enqueue(callback);
    }

    public void changePassword(Long userId, ChangePasswordRequestDTO request, Callback<Void> callback) {
        Call<Void> call = service.changePassword(userId, request);
        call.enqueue(callback);
    }
}
