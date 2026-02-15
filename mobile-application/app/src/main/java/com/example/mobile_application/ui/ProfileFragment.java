package com.example.mobile_application.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.UserProfileDTO;
import com.example.mobile_application.dto.UserProfileRequestDTO;
import com.example.mobile_application.repository.UserProfileRepository;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ProfileFragment extends Fragment {

    private static final String ARG_USER_ROLE = "userRole";
    private static final String ARG_USER_ID = "userId";
    private static final String API_BASE_URL = "http://10.0.2.2:8080";

    private String mUserRole;
    private Long mUserId;

    private ProfileViewBinder viewBinder;
    private ProfileImageLoader imageLoader;

    // Image picker for profile photo selection
    private ActivityResultLauncher<String> imagePickerLauncher;

    // Repository and data
    private UserProfileRepository profileRepository;
    private UserProfileDTO currentProfile;

    private boolean isDriver = false;
    private boolean isEditMode = false;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(String userRole) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ROLE, userRole);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProfileFragment newInstance(String userRole, Long userId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ROLE, userRole);
        args.putLong(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeImagePicker();
        extractUserRoleFromArguments();
    }

    private void initializeImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null && viewBinder != null) {
                        viewBinder.getImageProfile().setImageURI(uri);
                    }
                });
    }

    private void extractUserRoleFromArguments() {
        if (getArguments() != null) {
            mUserRole = getArguments().getString(ARG_USER_ROLE);
            if (getArguments().containsKey(ARG_USER_ID)) {
                mUserId = getArguments().getLong(ARG_USER_ID);
            }
        }

        if (mUserId == null || mUserId < 0 || mUserRole == null) {
            TokenManager tokenManager = ApiClient.getTokenManager();
            mUserId = tokenManager.getUserId();
            mUserRole = tokenManager.getRole();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileRepository = new UserProfileRepository();
        viewBinder = new ProfileViewBinder(view);
        imageLoader = new ProfileImageLoader(API_BASE_URL);
        setupEventListeners();

        // Configure UI based on user role
        isDriver = "driver".equalsIgnoreCase(mUserRole);
        updateRoleSpecificUI();
        viewBinder.setEditMode(false);

        // Load profile from backend
        if (mUserId == null || mUserId < 0) {
            showToast("Please log in to view your profile");
            return view;
        }

        loadProfile();

        return view;
    }

    /**
     * Sets up click listeners for all interactive UI elements.
     */
    private void setupEventListeners() {
        viewBinder.getImageProfile().setOnClickListener(v -> launchImagePicker());
        viewBinder.getChangePhotoButton().setOnClickListener(v -> launchImagePicker());

        viewBinder.getEditButton().setOnClickListener(v -> toggleEditMode(true));
        viewBinder.getSaveButton().setOnClickListener(v -> {
            saveProfileChanges();
            toggleEditMode(false);
        });

        viewBinder.getChangePasswordButton()
                .setOnClickListener(v -> PasswordChangeDialogHelper.showChangePasswordDialog(getContext(), mUserId));
    }

    private void launchImagePicker() {
        imagePickerLauncher.launch("image/*");
    }

    private void updateRoleSpecificUI() {
        viewBinder.setDriverMode(isDriver);
    }

    private void toggleEditMode(boolean enabled) {
        isEditMode = enabled;
        viewBinder.setEditMode(enabled);
    }

    /**
     * Loads the user profile from the backend
     */
    private void loadProfile() {
        profileRepository.getProfile(mUserId, new Callback<UserProfileDTO>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileDTO> call, @NonNull Response<UserProfileDTO> response) {
                if (!isAdded())
                    return;

                if (response.isSuccessful() && response.body() != null) {
                    currentProfile = response.body();

                    mUserRole = currentProfile.getUserRole();
                    isDriver = "driver".equalsIgnoreCase(mUserRole);
                    updateRoleSpecificUI();

                    populateProfileUI();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileDTO> call, @NonNull Throwable t) {
            }
        });
    }

    /**
     * Populates the UI with the loaded profile data
     */
    private void populateProfileUI() {
        if (currentProfile != null) {
            viewBinder.bindProfile(currentProfile, isDriver, this, imageLoader);
        }
    }

    /**
     * Saves the profile changes to the backend
     */
    private void saveProfileChanges() {
        String fullName = viewBinder.getFullNameInput();
        String address = viewBinder.getAddressInput();
        String phone = viewBinder.getPhoneInput();

        // Split full name into first and last name
        String[] nameParts = fullName.split(" ", 2);
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        // Validate required fields
        if (firstName.isEmpty() || lastName.isEmpty()) {
            showToast("First and last name are required");
            return;
        }

        // Create request DTO
        String email = currentProfile != null ? currentProfile.getEmail() : "";
        UserProfileRequestDTO request = new UserProfileRequestDTO(
                firstName,
                lastName,
                email,
                phone,
                address);

        // Send update request
        profileRepository.updateProfile(mUserId, request, new Callback<UserProfileDTO>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileDTO> call, @NonNull Response<UserProfileDTO> response) {
                if (!isAdded())
                    return;

                if (response.isSuccessful() && response.body() != null) {
                    currentProfile = response.body();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileDTO> call, @NonNull Throwable t) {
            }
        });
    }
    private void showToast(String message) {
        if (isAdded()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}