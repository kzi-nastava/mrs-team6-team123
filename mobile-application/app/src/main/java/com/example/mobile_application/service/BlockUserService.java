package com.example.mobile_application.service;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BlockUserService {
    @POST("api/users/{userId}/block")
    Call<Void> blockUser(@Path("userId") Long userId);

    @POST("api/users/{userId}/unblock")
    Call<Void> unblockUser(@Path("userId") Long userId);
}
