package com.example.mobile_application.ui;

import android.os.Bundle;
import android.util.Patterns;
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
import com.example.mobile_application.dto.auth.ForgotPasswordRequestDTO;
import com.example.mobile_application.repository.AuthRepository;

import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordFragment extends Fragment {

    private EditText emailInput;
    private Button sendResetButton;
    private TextView backToLoginLink;
    private ProgressBar progressBar;
    private AuthRepository authRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        emailInput = view.findViewById(R.id.email_input);
        sendResetButton = view.findViewById(R.id.send_reset_button);
        backToLoginLink = view.findViewById(R.id.back_to_login_link);
        progressBar = view.findViewById(R.id.progress_bar);

        authRepository = new AuthRepository();

        sendResetButton.setOnClickListener(v -> attemptForgotPassword());
        backToLoginLink.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        return view;
    }

    private void attemptForgotPassword() {
        String email = emailInput.getText().toString().trim();
        if (email.isEmpty()) { showToast("Please enter your email"); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email"); return;
        }

        setLoading(true);

        authRepository.forgotPassword(new ForgotPasswordRequestDTO(email),
                new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call,
                                           @NonNull Response<String> response) {
                        if (!isAdded()) return;
                        setLoading(false);
                        if (response.isSuccessful()) {
                            showToast("Password reset link sent to your email!");
                            requireActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            String err = "Failed to send reset link";
                            try {
                                if (response.errorBody() != null)
                                    err = response.errorBody().string();
                            } catch (IOException ignored) {}
                            showToast(err);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        setLoading(false);
                        showToast("Connection error: " + t.getMessage());
                    }
                });
    }

    private void setLoading(boolean loading) {
        if (progressBar != null)
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        sendResetButton.setEnabled(!loading);
    }

    private void showToast(String msg) {
        if (isAdded()) Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}