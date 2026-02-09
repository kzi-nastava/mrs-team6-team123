
package com.example.mobile_application.ui;

import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.auth.RegistrationRequestDTO;
import com.example.mobile_application.dto.auth.RegistrationResponseDTO;
import com.example.mobile_application.repository.AuthRepository;

import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private EditText emailInput, passwordInput, confirmPasswordInput;
    private EditText firstNameInput, lastNameInput, addressInput, phoneInput;
    private ImageView profileImage;
    private Button uploadImageButton, registerButton;
    private TextView loginLink;
    private ProgressBar progressBar;
    private AuthRepository authRepository;
    private Uri selectedImageUri;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        profileImage.setImageURI(uri);
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        progressBar = view.findViewById(R.id.progress_bar);

        authRepository = new AuthRepository();

        uploadImageButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        registerButton.setOnClickListener(v -> attemptRegister());
        loginLink.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_container, new LoginFragment())
                        .addToBackStack(null)
                        .commit()
        );

        return view;
    }

    private void attemptRegister() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty() ||
                address.isEmpty() || phone.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email");
            return;
        }
        if (password.length() < 6) {
            showToast("Password must be at least 6 characters");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showToast("Passwords do not match");
            return;
        }

        setLoading(true);

        RegistrationRequestDTO req = new RegistrationRequestDTO();
        req.setEmail(email);
        req.setPassword(password);
        req.setConfirmPassword(confirmPassword);
        req.setFirstName(firstName);
        req.setLastName(lastName);
        req.setAddress(address);
        req.setPhoneNumber(phone);
        // profilePicture left null - backend assigns default

        authRepository.register(req, new Callback<RegistrationResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<RegistrationResponseDTO> call,
                                   @NonNull Response<RegistrationResponseDTO> response) {
                if (!isAdded()) return;
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    showToast("Registration successful! Check your email to activate.");
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_container, new LoginFragment())
                            .commit();
                } else {
                    String err = "Registration failed";
                    try {
                        if (response.errorBody() != null)
                            err = response.errorBody().string();
                    } catch (IOException ignored) {}
                    showToast(err);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegistrationResponseDTO> call,
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
        registerButton.setEnabled(!loading);
    }

    private void showToast(String msg) {
        if (isAdded()) Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}