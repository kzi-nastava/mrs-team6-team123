package com.example.mobile_application.repository;

import com.example.mobile_application.dto.auth.*;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.AuthApiService;
import retrofit2.Callback;

public class AuthRepository {
    private final AuthApiService service;

    public AuthRepository() {
        this.service = ApiClient.getInstance().create(AuthApiService.class);
    }

    public void login(LoginRequestDTO req, Callback<LoginResponseDTO> cb) {
        service.login(req).enqueue(cb);
    }

    public void register(RegistrationRequestDTO req, Callback<RegistrationResponseDTO> cb) {
        service.register(req).enqueue(cb);
    }

    public void logout(LogoutRequestDTO req, Callback<LogoutResponseDTO> cb) {
        service.logout(req).enqueue(cb);
    }

    public void forgotPassword(ForgotPasswordRequestDTO req, Callback<String> cb) {
        service.forgotPassword(req).enqueue(cb);
    }

    public void resetPassword(ResetPasswordRequestDTO req, Callback<String> cb) {
        service.resetPassword(req).enqueue(cb);
    }
}