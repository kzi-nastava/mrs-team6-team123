package com.example.mobile_application.repository;

import com.example.mobile_application.dto.NotificationDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.NotificationService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class NotificationRepository {
    private final NotificationService service;

    public NotificationRepository() {
        service = ApiClient.getInstance().create(NotificationService.class);
    }

    public void getUnreadNotifications(Long userId, Callback<List<NotificationDTO>> callback) {
        Call<List<NotificationDTO>> call = service.getUnreadNotifications(userId);
        call.enqueue(callback);
    }

    public void getReadNotifications(Long userId, Callback<List<NotificationDTO>> callback) {
        Call<List<NotificationDTO>> call = service.getReadNotifications(userId);
        call.enqueue(callback);
    }

    public void markAsRead(Long notificationId, Callback<Void> callback) {
        Call<Void> call = service.markAsRead(notificationId);
        call.enqueue(callback);
    }
}
