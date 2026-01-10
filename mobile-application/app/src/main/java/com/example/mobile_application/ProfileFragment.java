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
import android.text.InputType;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private static final String ARG_USER_ROLE = "userRole";

    private String mUserRole;

    private LinearLayout statsContainer;
    private TextView textActiveHours;
    private LinearLayout vehicleContainer;
    private EditText editFullName, editAddress, editPhone;
    private ImageView imageProfile;
    private ImageButton btnChangePhoto;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private boolean isDriver = false;

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
        // Inicijalizacija Image Picker-a
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageProfile.setImageURI(uri);
                    }
                });

        if (getArguments() != null) {
            mUserRole = getArguments().getString(ARG_USER_ROLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        statsContainer = view.findViewById(R.id.container_stats);
        textActiveHours = view.findViewById(R.id.text_active_hours);
        vehicleContainer = view.findViewById(R.id.container_vehicle);

        editFullName = view.findViewById(R.id.edit_first_name);
        editAddress = view.findViewById(R.id.edit_address);
        editPhone = view.findViewById(R.id.edit_phone);
        imageProfile = view.findViewById(R.id.image_profile);
        btnChangePhoto = view.findViewById(R.id.btn_change_photo);

        // Postavljanje listener-a za promenu slike profila
        imageProfile.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        btnChangePhoto.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        // Listener za dugme za promenu lozinke
        Button btnChangePassword = view.findViewById(R.id.btn_change_password);
        btnChangePassword.setOnClickListener(v -> {
            showChangePasswordDialog();
        });

        // Determine role
        isDriver = "driver".equalsIgnoreCase(mUserRole);
        updateRoleSpecificUI();

        // view-only mode
        applyViewModeStyle(editFullName);
        applyViewModeStyle(editAddress);
        applyViewModeStyle(editPhone);

        return view;
    }

    private void updateRoleSpecificUI() {
        if (isDriver) {
            // Show stats, active hours and vehicle info for drivers
            if (statsContainer != null)
                statsContainer.setVisibility(View.VISIBLE);
            if (textActiveHours != null)
                textActiveHours.setVisibility(View.VISIBLE);
            if (vehicleContainer != null)
                vehicleContainer.setVisibility(View.VISIBLE);
        } else {
            // Hide driver-specific sections for regular users
            if (statsContainer != null)
                statsContainer.setVisibility(View.GONE);
            if (textActiveHours != null)
                textActiveHours.setVisibility(View.GONE);
            if (vehicleContainer != null)
                vehicleContainer.setVisibility(View.GONE);
        }
    }

    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_change_password, null);

        EditText oldPass = dialogView.findViewById(R.id.edit_old_password);
        EditText newPass = dialogView.findViewById(R.id.edit_new_password);
        EditText confirmPass = dialogView.findViewById(R.id.edit_confirm_password);

        ImageView toggleOldPass = dialogView.findViewById(R.id.toggle_old_password);
        ImageView toggleNewPass = dialogView.findViewById(R.id.toggle_new_password);
        ImageView toggleConfirmPass = dialogView.findViewById(R.id.toggle_confirm_password);

        toggleOldPass.setOnClickListener(v -> togglePasswordVisibility(oldPass));
        toggleNewPass.setOnClickListener(v -> togglePasswordVisibility(newPass));
        toggleConfirmPass.setOnClickListener(v -> togglePasswordVisibility(confirmPass));

        new AlertDialog.Builder(requireContext())
                .setTitle("Change password")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    // todo : validacija i backend poziv
                    Toast.makeText(requireContext(), "Password changed", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void togglePasswordVisibility(EditText editText) {
        int currentType = editText.getInputType();
        if (currentType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        editText.setSelection(editText.getText().length());
    }

    private void applyViewModeStyle(EditText editText) {
        editText.setEnabled(false);
        editText.setBackground(null);
        editText.setTextColor(getResources().getColor(R.color.black));
    }
}