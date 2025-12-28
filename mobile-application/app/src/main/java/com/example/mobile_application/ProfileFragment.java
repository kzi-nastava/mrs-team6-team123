package com.example.mobile_application;

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

public class ProfileFragment extends Fragment {

    private static final String ARG_USER_ROLE = "userRole";

    private String mUserRole;

    private LinearLayout statsContainer;
    private TextView textActiveHours;
    private LinearLayout vehicleContainer;
    private EditText editFullName, editAddress, editPhone;

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

    private void applyViewModeStyle(EditText editText) {
        editText.setEnabled(false);
        editText.setBackground(null);
        editText.setTextColor(getResources().getColor(R.color.black));
    }
}