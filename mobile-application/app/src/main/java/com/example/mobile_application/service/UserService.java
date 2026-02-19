package com.example.mobile_application.service;

import com.example.mobile_application.dto.UserDTO;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.DELETE;

public interface UserService {
    @GET("api/users/users")
    Call<List<UserDTO>> getAllActivatedUsers(@Query("excludeUserId") Long excludeUserId);

    @POST("api/users/{userId}/block")
    Call<Void> blockUser(@Path("userId") Long userId);

    @POST("api/users/{userId}/unblock")
    Call<Void> unblockUser(@Path("userId") Long userId);
}
