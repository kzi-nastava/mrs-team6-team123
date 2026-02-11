package com.example.mobile_application.ui;

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

import com.example.mobile_application.R;
import com.example.mobile_application.dto.DriverRegistrationRequestDTO;
import com.example.mobile_application.dto.DriverResponseDTO;
import com.example.mobile_application.repository.DriverRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private DriverRepository driverRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_registration, container, false);

        // Initialize repository
        driverRepository = new DriverRepository();

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
        String[] vehicleTypes = { "STANDARD", "LUXURY", "VAN" };
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

        // Validate inputs
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                address.isEmpty() || phone.isEmpty() || vehicleModel.isEmpty() ||
                licensePlate.isEmpty() || seatsStr.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int seats;
        try {
            seats = Integer.parseInt(seatsStr);
            if (seats < 1 || seats > 8) {
                Toast.makeText(getContext(), "Seats must be between 1 and 8", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid number for seats", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create request DTO
        DriverRegistrationRequestDTO request = new DriverRegistrationRequestDTO(
                firstName, lastName, email, address, phone,
                vehicleModel, vehicleType, licensePlate, seats,
                babyFriendly, petFriendly);

        // Disable button while processing
        createAccountButton.setEnabled(false);
        createAccountButton.setText("Creating...");

        // Call API
        driverRepository.registerDriver(request, new Callback<DriverResponseDTO>() {
            @Override
            public void onResponse(Call<DriverResponseDTO> call, Response<DriverResponseDTO> response) {
                createAccountButton.setEnabled(true);
                createAccountButton.setText("Create account and send link");

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(),
                            "Driver account created successfully! Activation link sent to "
                                    + response.body().getEmail(),
                            Toast.LENGTH_LONG).show();
                    clearForm();
                } else {
                    Toast.makeText(getContext(),
                            "Failed to create driver account. Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DriverResponseDTO> call, Throwable t) {
                createAccountButton.setEnabled(true);
                createAccountButton.setText("Create account and send link");
                Toast.makeText(getContext(),
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
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
