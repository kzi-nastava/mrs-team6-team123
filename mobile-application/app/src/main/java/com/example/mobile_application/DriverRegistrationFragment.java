package com.example.mobile_application;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class DriverRegistrationFragment extends Fragment {

    // Driver fields
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText addressInput;
    private EditText phoneInput;

    // Vehicle fields
    private EditText vehicleModelInput;
    private Spinner vehicleTypeSpinner;
    private EditText licensePlateInput;
    private EditText seatsInput;
    private CheckBox babyFriendlyCheckbox;
    private CheckBox petFriendlyCheckbox;

    private Button createAccountButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_registration, container, false);

        // Initialize fields
        firstNameInput = view.findViewById(R.id.first_name_input);
        lastNameInput = view.findViewById(R.id.last_name_input);
        emailInput = view.findViewById(R.id.email_input);
        addressInput = view.findViewById(R.id.address_input);
        phoneInput = view.findViewById(R.id.phone_input);

        vehicleModelInput = view.findViewById(R.id.vehicle_model_input);
        vehicleTypeSpinner = view.findViewById(R.id.vehicle_type_spinner);
        licensePlateInput = view.findViewById(R.id.license_plate_input);
        seatsInput = view.findViewById(R.id.seats_input);
        babyFriendlyCheckbox = view.findViewById(R.id.baby_friendly_checkbox);
        petFriendlyCheckbox = view.findViewById(R.id.pet_friendly_checkbox);

        createAccountButton = view.findViewById(R.id.create_account_button);

        setupVehicleTypeSpinner();

        createAccountButton.setOnClickListener(v -> handleCreateAccount());

        return view;
    }

    private void setupVehicleTypeSpinner() {
        String[] vehicleTypes = { "Standard", "Luxury", "Van" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                vehicleTypes);
        vehicleTypeSpinner.setAdapter(adapter);
    }

    private void handleCreateAccount() {
        // Get driver data
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        // Get vehicle data
        String vehicleModel = vehicleModelInput.getText().toString().trim();
        String vehicleType = vehicleTypeSpinner.getSelectedItem().toString();
        String licensePlate = licensePlateInput.getText().toString().trim();
        String seatsStr = seatsInput.getText().toString().trim();
        boolean babyFriendly = babyFriendlyCheckbox.isChecked();
        boolean petFriendly = petFriendlyCheckbox.isChecked();

        // Success message
        Toast.makeText(getContext(), "Driver account created. Activation link sent.", Toast.LENGTH_LONG).show();

        clearForm();
    }

    private void clearForm() {
        firstNameInput.setText("");
        lastNameInput.setText("");
        emailInput.setText("");
        addressInput.setText("");
        phoneInput.setText("");
        vehicleModelInput.setText("");
        licensePlateInput.setText("");
        seatsInput.setText("");
        babyFriendlyCheckbox.setChecked(false);
        petFriendlyCheckbox.setChecked(false);
        vehicleTypeSpinner.setSelection(0);
    }
}
