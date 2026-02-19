package com.example.mobile_application.repository;

import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.BlockUserService;

import retrofit2.Call;
import retrofit2.Callback;

public class BlockUserRepository {
    private final BlockUserService service;

    public BlockUserRepository() {
        this.service = ApiClient.getInstance().create(BlockUserService.class);
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
