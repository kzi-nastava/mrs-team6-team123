package com.example.mobile_application;

import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

/**
 * ProfileFragment manages the user profile display
 * Supports two modes: view mode (read-only) and edit mode
 */
public class ProfileFragment extends Fragment {

    private static final String ARG_USER_ROLE = "userRole";

    private String mUserRole;

    // Driver-specific containers
    private LinearLayout statsContainer;
    private TextView textActiveHours;
    private LinearLayout vehicleContainer;

    // Personal info fields
    private EditText editFullName;
    private EditText editAddress;
    private EditText editPhone;

    // Profile image and controls
    private ImageView imageProfile;
    private ImageButton btnChangePhoto;
    private Button btnEdit;
    private Button btnSave;

    // Image picker for profile photo selection
    private ActivityResultLauncher<String> imagePickerLauncher;

    private boolean isDriver = false;
    private boolean isEditMode = false;

    // Store original paddings
    private int[] fullNamePadding;
    private int[] addressPadding;
    private int[] phonePadding;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(String userRole) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ROLE, userRole);
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
                    if (uri != null) {
                        imageProfile.setImageURI(uri);
                    }
                });
    }

    private void extractUserRoleFromArguments() {
        if (getArguments() != null) {
            mUserRole = getArguments().getString(ARG_USER_ROLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeUIComponents(view);
        captureOriginalPaddings();
        setupEventListeners();

        // Configure UI based on user role
        isDriver = "driver".equalsIgnoreCase(mUserRole);
        updateRoleSpecificUI();

        applyViewModeStyle(editFullName, fullNamePadding);
        applyViewModeStyle(editAddress, addressPadding);
        applyViewModeStyle(editPhone, phonePadding);

        return view;
    }

    private void initializeUIComponents(View view) {
        // Driver-specific containers
        statsContainer = view.findViewById(R.id.container_stats);
        textActiveHours = view.findViewById(R.id.text_active_hours);
        vehicleContainer = view.findViewById(R.id.container_vehicle);

        // Personal info fields
        editFullName = view.findViewById(R.id.edit_first_name);
        editAddress = view.findViewById(R.id.edit_address);
        editPhone = view.findViewById(R.id.edit_phone);

        // Profile image and controls
        imageProfile = view.findViewById(R.id.image_profile);
        btnChangePhoto = view.findViewById(R.id.btn_change_photo);
        btnEdit = view.findViewById(R.id.btn_edit);
        btnSave = view.findViewById(R.id.btn_save);
    }

    private void captureOriginalPaddings() {
        fullNamePadding = storePaddingValues(editFullName);
        addressPadding = storePaddingValues(editAddress);
        phonePadding = storePaddingValues(editPhone);
    }

    private int[] storePaddingValues(EditText editText) {
        return new int[] {
                editText.getPaddingLeft(),
                editText.getPaddingTop(),
                editText.getPaddingRight(),
                editText.getPaddingBottom()
        };
    }

    /**
     * Sets up click listeners for all interactive UI elements.
     */
    private void setupEventListeners() {
        imageProfile.setOnClickListener(v -> launchImagePicker());
        btnChangePhoto.setOnClickListener(v -> launchImagePicker());

        btnEdit.setOnClickListener(v -> toggleEditMode(true));
        btnSave.setOnClickListener(v -> toggleEditMode(false));

        Button btnChangePassword = imageProfile.getRootView().findViewById(R.id.btn_change_password);
        btnChangePassword.setOnClickListener(v -> PasswordChangeDialogHelper.showChangePasswordDialog(getContext()));
    }

    private void launchImagePicker() {
        imagePickerLauncher.launch("image/*");
    }

    private void updateRoleSpecificUI() {
        if (isDriver) {
            showDriverUI();
        } else {
            hideDriverUI();
        }
    }

    private void showDriverUI() {
        setVisibility(statsContainer, View.VISIBLE);
        setVisibility(textActiveHours, View.VISIBLE);
        setVisibility(vehicleContainer, View.VISIBLE);
    }

    private void hideDriverUI() {
        setVisibility(statsContainer, View.GONE);
        setVisibility(textActiveHours, View.GONE);
        setVisibility(vehicleContainer, View.GONE);
    }

    private void setVisibility(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    private void restorePadding(EditText editText, int[] paddingValues) {
        if (paddingValues != null && paddingValues.length == 4) {
            editText.setPadding(paddingValues[0], paddingValues[1], paddingValues[2], paddingValues[3]);
        }
    }

    private void toggleEditMode(boolean enabled) {
        isEditMode = enabled;

        if (enabled) {
            enterEditMode();
        } else {
            exitEditMode();
        }
    }

    private void enterEditMode() {
        enableEditFields(true);

        btnEdit.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);

        applyEditModeStyle(editFullName);
        applyEditModeStyle(editAddress);
        applyEditModeStyle(editPhone);
    }

    private void exitEditMode() {
        enableEditFields(false);

        btnEdit.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.GONE);

        applyViewModeStyle(editFullName, fullNamePadding);
        applyViewModeStyle(editAddress, addressPadding);
        applyViewModeStyle(editPhone, phonePadding);
    }

    private void enableEditFields(boolean enabled) {
        editFullName.setEnabled(enabled);
        editAddress.setEnabled(enabled);
        editPhone.setEnabled(enabled);
    }

    private void applyViewModeStyle(EditText editText, int[] originalPadding) {
        editText.setEnabled(false);
        editText.setBackground(null);
        editText.setTextColor(getResources().getColor(R.color.black));

        // Restore original paddings
        restorePadding(editText, originalPadding);
    }

    private void applyEditModeStyle(EditText editText) {
        editText.setBackground(getResources().getDrawable(R.drawable.editable_field_bg));
        editText.setTextColor(getResources().getColor(R.color.black));
    }
}