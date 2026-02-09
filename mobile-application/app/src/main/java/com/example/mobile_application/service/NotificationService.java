package com.example.mobile_application.service;

import com.example.mobile_application.dto.NotificationDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NotificationService {
    @GET("api/notifications/unread/{userId}")
    Call<List<NotificationDTO>> getUnreadNotifications(@Path("userId") Long userId);

    @GET("api/notifications/read/{userId}")
    Call<List<NotificationDTO>> getReadNotifications(@Path("userId") Long userId);

    @POST("api/notifications/mark-read/{notificationId}")
    Call<Void> markAsRead(@Path("notificationId") Long notificationId);
}
