package com.example.mobile_application.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.auth.ResetPasswordRequestDTO;
import com.example.mobile_application.repository.AuthRepository;

import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordFragment extends Fragment {

    private static final String ARG_TOKEN = "reset_token";
    private EditText newPasswordInput, confirmPasswordInput;
    private Button resetButton;
    private ProgressBar progressBar;
    private AuthRepository authRepository;
    private String resetToken;

    public static ResetPasswordFragment newInstance(String token) {
        ResetPasswordFragment f = new ResetPasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOKEN, token);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            resetToken = getArguments().getString(ARG_TOKEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);

        newPasswordInput = view.findViewById(R.id.new_password_input);
        confirmPasswordInput = view.findViewById(R.id.confirm_password_input);
        resetButton = view.findViewById(R.id.reset_button);
        progressBar = view.findViewById(R.id.progress_bar);

        authRepository = new AuthRepository();
        resetButton.setOnClickListener(v -> attemptReset());

        return view;
    }

    private void attemptReset() {
        String newPass = newPasswordInput.getText().toString().trim();
        String confirmPass = confirmPasswordInput.getText().toString().trim();

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            showToast("Please fill all fields"); return;
        }
        if (newPass.length() < 6) {
            showToast("Password must be at least 6 characters"); return;
        }
        if (!newPass.equals(confirmPass)) {
            showToast("Passwords do not match"); return;
        }

        setLoading(true);

        authRepository.resetPassword(
                new ResetPasswordRequestDTO(resetToken, newPass, confirmPass),
                new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call,
                                           @NonNull Response<String> response) {
                        if (!isAdded()) return;
                        setLoading(false);
                        if (response.isSuccessful()) {
                            showToast("Password reset successful! Please login.");
                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.main_container, new LoginFragment())
                                    .commit();
                        } else {
                            String err = "Reset failed";
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
        resetButton.setEnabled(!loading);
    }

    private void showToast(String msg) {
        if (isAdded()) Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}