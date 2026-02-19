package com.example.mobile_application.repository;

import com.example.mobile_application.dto.ChangePasswordRequestDTO;
import com.example.mobile_application.dto.UserProfileDTO;
import com.example.mobile_application.dto.UserProfileRequestDTO;
import com.example.mobile_application.dto.UserDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.UserProfileService;
import com.example.mobile_application.service.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import java.util.List;

public class UserRepository {
    private UserService service;

    public UserRepository() {
        this.service = ApiClient.getInstance().create(UserService.class);
    }

    public void getAllActivatedUsers(Long excludeUserId, Callback<List<UserDTO>> callback) {
        Call<List<UserDTO>> call = service.getAllActivatedUsers(excludeUserId);
        call.enqueue(callback);
    }

    public void blockUser(Long userId, Callback<Void> callback) {
        Call<Void> call = service.blockUser(userId);
        call.enqueue(callback);
    }

    public void unblockUser(Long userId, Callback<Void> callback) {
        Call<Void> call = service.unblockUser(userId);
        call.enqueue(callback);
    }
}
