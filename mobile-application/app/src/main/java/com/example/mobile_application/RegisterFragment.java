package com.example.mobile_application;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class RegisterFragment extends Fragment {

    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText addressInput;
    private EditText phoneInput;
    private ImageView profileImage;
    private Button uploadImageButton;
    private Button registerButton;
    private TextView loginLink;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);
        confirmPasswordInput = view.findViewById(R.id.confirm_password_input);
        firstNameInput = view.findViewById(R.id.first_name_input);
        lastNameInput = view.findViewById(R.id.last_name_input);
        addressInput = view.findViewById(R.id.address_input);
        phoneInput = view.findViewById(R.id.phone_input);
        profileImage = view.findViewById(R.id.profile_image);
        uploadImageButton = view.findViewById(R.id.upload_image_button);
        registerButton = view.findViewById(R.id.register_button);
        loginLink = view.findViewById(R.id.login_link);

        uploadImageButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Upload image clicked", Toast.LENGTH_SHORT).show();
        });

        registerButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();
            String firstName = firstNameInput.getText().toString();
            String lastName = lastNameInput.getText().toString();
            String address = addressInput.getText().toString();
            String phone = phoneInput.getText().toString();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                    firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Register clicked", Toast.LENGTH_SHORT).show();
            }
        });

        loginLink.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Login clicked", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}