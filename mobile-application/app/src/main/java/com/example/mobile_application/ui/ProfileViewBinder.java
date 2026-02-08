package com.example.mobile_application.ui;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.UserProfileDTO;

public class ProfileViewBinder {
    private static final String TAG = "ProfileViewBinder";

    private final LinearLayout statsContainer;
    private final TextView textActiveHours;
    private final LinearLayout vehicleContainer;
    private final TextView textTotalRides;
    private final TextView textRating;
    private final TextView textVehicleModel;
    private final TextView textVehicleType;
    private final TextView textLicense;
    private final TextView textCapacity;

    private final TextView textName;
    private final TextView textEmail;

    private final EditText editFullName;
    private final EditText editAddress;
    private final EditText editPhone;

    private final ImageView imageProfile;
    private final ImageButton btnChangePhoto;
    private final Button btnEdit;
    private final Button btnSave;
    private final Button btnChangePassword;

    private final int[] fullNamePadding;
    private final int[] addressPadding;
    private final int[] phonePadding;

    public ProfileViewBinder(View root) {
        statsContainer = root.findViewById(R.id.container_stats);
        textActiveHours = root.findViewById(R.id.text_active_hours);
        vehicleContainer = root.findViewById(R.id.container_vehicle);
        textTotalRides = root.findViewById(R.id.text_total_rides);
        textRating = root.findViewById(R.id.text_rating);
        textVehicleModel = root.findViewById(R.id.text_vehicle_model);
        textVehicleType = root.findViewById(R.id.text_vehicle_type);
        textLicense = root.findViewById(R.id.text_license);
        textCapacity = root.findViewById(R.id.text_capacity);

        textName = root.findViewById(R.id.text_name);
        textEmail = root.findViewById(R.id.text_email);

        editFullName = root.findViewById(R.id.edit_first_name);
        editAddress = root.findViewById(R.id.edit_address);
        editPhone = root.findViewById(R.id.edit_phone);

        imageProfile = root.findViewById(R.id.image_profile);
        btnChangePhoto = root.findViewById(R.id.btn_change_photo);
        btnEdit = root.findViewById(R.id.btn_edit);
        btnSave = root.findViewById(R.id.btn_save);
        btnChangePassword = root.findViewById(R.id.btn_change_password);

        fullNamePadding = storePaddingValues(editFullName);
        addressPadding = storePaddingValues(editAddress);
        phonePadding = storePaddingValues(editPhone);
    }

    public void bindProfile(UserProfileDTO profile, boolean isDriver, Fragment fragment,
            ProfileImageLoader imageLoader) {
        if (profile == null) {
            Log.w(TAG, "Profile is null");
            return;
        }

        String fullName = profile.getFirstName() + " " + profile.getLastName();
        String email = profile.getEmail();

        textName.setText(fullName);
        textEmail.setText(email);

        editFullName.setText(fullName);
        editAddress.setText(profile.getAddress() != null ? profile.getAddress() : "");
        editPhone.setText(profile.getPhone() != null ? profile.getPhone() : "");

        if (isDriver) {
            Integer totalRides = profile.getTotalRides();
            Double rating = profile.getRating();
            String hoursActive = profile.getHoursActive();

            textTotalRides.setText(totalRides != null ? String.valueOf(totalRides) : "-");
            textRating.setText(rating != null ? String.format(java.util.Locale.US, "%.1f", rating) : "-");

            if (hoursActive != null && !hoursActive.isEmpty()) {
                textActiveHours.setText("Hours active: " + hoursActive);
                textActiveHours.setVisibility(View.VISIBLE);
            } else {
                textActiveHours.setVisibility(View.GONE);
            }

            if (profile.getVehicle() != null) {
                textVehicleModel.setText("Model: " + profile.getVehicle().getModel());
                textVehicleType.setText("Type: " + profile.getVehicle().getType());
                textLicense.setText("License Plate: " + profile.getVehicle().getLicensePlate());
                textCapacity.setText("Capacity: " + profile.getVehicle().getCapacity());
            }
        }

        imageLoader.load(fragment, imageProfile, profile.getProfileImage());
        adjustEmailFontSize();
    }

    public void setDriverMode(boolean isDriver) {
        if (isDriver) {
            setVisibility(statsContainer, View.VISIBLE);
            setVisibility(textActiveHours, View.VISIBLE);
            setVisibility(vehicleContainer, View.VISIBLE);
        } else {
            setVisibility(statsContainer, View.GONE);
            setVisibility(textActiveHours, View.GONE);
            setVisibility(vehicleContainer, View.GONE);
        }
    }

    public void setEditMode(boolean enabled) {
        if (enabled) {
            enableEditFields(true);
            btnEdit.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
            applyEditModeStyle(editFullName);
            applyEditModeStyle(editAddress);
            applyEditModeStyle(editPhone);
        } else {
            enableEditFields(false);
            btnEdit.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
            applyViewModeStyle(editFullName, fullNamePadding);
            applyViewModeStyle(editAddress, addressPadding);
            applyViewModeStyle(editPhone, phonePadding);
        }
    }

    public String getFullNameInput() {
        return editFullName.getText().toString().trim();
    }

    public String getAddressInput() {
        return editAddress.getText().toString().trim();
    }

    public String getPhoneInput() {
        return editPhone.getText().toString().trim();
    }

    public ImageView getImageProfile() {
        return imageProfile;
    }

    public ImageButton getChangePhotoButton() {
        return btnChangePhoto;
    }

    public Button getEditButton() {
        return btnEdit;
    }

    public Button getSaveButton() {
        return btnSave;
    }

    public Button getChangePasswordButton() {
        return btnChangePassword;
    }

    private void enableEditFields(boolean enabled) {
        editFullName.setEnabled(enabled);
        editAddress.setEnabled(enabled);
        editPhone.setEnabled(enabled);
    }

    private void applyViewModeStyle(EditText editText, int[] originalPadding) {
        editText.setEnabled(false);
        editText.setBackground(null);
        editText.setTextColor(editText.getResources().getColor(R.color.black));
        restorePadding(editText, originalPadding);
    }

    private void applyEditModeStyle(EditText editText) {
        editText.setBackground(editText.getResources().getDrawable(R.drawable.editable_field_bg));
        editText.setTextColor(editText.getResources().getColor(R.color.black));
    }

    private void restorePadding(EditText editText, int[] paddingValues) {
        if (paddingValues != null && paddingValues.length == 4) {
            editText.setPadding(paddingValues[0], paddingValues[1], paddingValues[2], paddingValues[3]);
        }
    }

    private int[] storePaddingValues(EditText editText) {
        return new int[] {
                editText.getPaddingLeft(),
                editText.getPaddingTop(),
                editText.getPaddingRight(),
                editText.getPaddingBottom()
        };
    }

    private void setVisibility(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    private void adjustEmailFontSize() {
        textEmail.post(() -> {
            int lines = textEmail.getLineCount();
            if (lines > 1) {
                for (float size = 22; size >= 14; size -= 2) {
                    textEmail.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
                    if (textEmail.getLineCount() <= 1) {
                        Log.d(TAG, "Adjusted email font size to: " + size + "sp");
                        break;
                    }
                }
            }
        });
    }
}
