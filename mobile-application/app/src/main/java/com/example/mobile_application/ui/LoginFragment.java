
package com.example.mobile_application.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.auth.LoginRequestDTO;
import com.example.mobile_application.dto.auth.LoginResponseDTO;
import com.example.mobile_application.repository.AuthRepository;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.TokenManager;

import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView forgotPasswordLink, registerLink;
    private ProgressBar progressBar;
    private AuthRepository authRepository;

    // MainActivity should implement this
    public interface OnLoginSuccessListener {
        void onLoginSuccess(Long userId, String role);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);
        loginButton = view.findViewById(R.id.login_button);
        forgotPasswordLink = view.findViewById(R.id.forgot_password_link);
        registerLink = view.findViewById(R.id.register_link);
        progressBar = view.findViewById(R.id.progress_bar);

        authRepository = new AuthRepository();

        loginButton.setOnClickListener(v -> attemptLogin());

        forgotPasswordLink.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_container, new ForgotPasswordFragment())
                        .addToBackStack(null)
                        .commit()
        );

        registerLink.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_container, new RegisterFragment())
                        .addToBackStack(null)
                        .commit()
        );

        return view;
    }

    private void attemptLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }

        setLoading(true);

        authRepository.login(new LoginRequestDTO(email, password),
                new Callback<LoginResponseDTO>() {
                    @Override
                    public void onResponse(@NonNull Call<LoginResponseDTO> call,
                                           @NonNull Response<LoginResponseDTO> response) {
                        if (!isAdded()) return;
                        setLoading(false);

                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponseDTO body = response.body();

                            TokenManager tm = ApiClient.getTokenManager();
                            tm.saveAuthData(body.getToken(), body.getUserId(),
                                    body.getEmail(), body.getRole());

                            ApiClient.resetClient();
                            showToast("Login successful!");

                            if (getActivity() instanceof OnLoginSuccessListener) {
                                ((OnLoginSuccessListener) getActivity())
                                        .onLoginSuccess(body.getUserId(), body.getRole());
                            }
                        } else {
                            String err = "Login failed";
                            try {
                                if (response.errorBody() != null)
                                    err = response.errorBody().string();
                            } catch (IOException ignored) {}
                            showToast(err);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<LoginResponseDTO> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;
                        setLoading(false);
                        showToast("Connection error: " + t.getMessage());
                    }
                });
    }

    private void setLoading(boolean loading) {
        if (progressBar != null)
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!loading);
    }

    private void showToast(String msg) {
        if (isAdded()) Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}