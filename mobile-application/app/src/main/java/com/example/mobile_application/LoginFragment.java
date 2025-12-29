package com.example.mobile_application;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {

    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView forgotPasswordLink;
    private TextView registerLink;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);
        loginButton = view.findViewById(R.id.login_button);
        forgotPasswordLink = view.findViewById(R.id.forgot_password_link);
        registerLink = view.findViewById(R.id.register_link);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Login clicked", Toast.LENGTH_SHORT).show();
            }
        });

        forgotPasswordLink.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Forgot password clicked", Toast.LENGTH_SHORT).show();
        });

        registerLink.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Register clicked", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}