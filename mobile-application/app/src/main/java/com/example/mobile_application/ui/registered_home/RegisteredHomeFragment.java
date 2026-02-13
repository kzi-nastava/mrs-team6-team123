package com.example.mobile_application.ui.registered_home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.GeocodingResult;
import com.example.mobile_application.ui.AddressAutocompleteAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.Calendar;

public class RegisteredHomeFragment extends Fragment {

    private MapView mapView;
    private BottomSheetBehavior<MaterialCardView> bottomSheetBehavior;

    // Input fields with autocomplete
    private AutoCompleteTextView startingPointInput;
    private AutoCompleteTextView destinationInput;
    private TextInputEditText additionalInstructionsInput;

    // Coordinates storage
    private String startCoordinates = "";
    private String endCoordinates = "";

    // Autocomplete adapters
    private AddressAutocompleteAdapter startAdapter;
    private AddressAutocompleteAdapter endAdapter;

    // Containers
    private LinearLayout stopsContainer;
    private LinearLayout passengersContainer;
    private LinearLayout timePickerContainer;
    private LinearLayout priceContainer;

    // Buttons
    private Button btnAddStop;
    private Button btnAddPassenger;
    private Button btnBookRide;

    // Spinners
    private Spinner vehicleTypeSpinner;
    private Spinner scheduleTypeSpinner;

    // Switches
    private SwitchMaterial switchPets;
    private SwitchMaterial switchBabies;

    // Number Pickers
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;

    // TextViews
    private TextView estimatedPriceText;

    // Time restriction variables
    private int currentHour;
    private int currentMinute;
    private String[] availableHours;
    private String[] availableMinutes;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registered_home, container, false);

        initializeViews(view);
        setupMap();
        setupSpinners();
        setupNumberPickers();
        setupBottomSheet(view);
        setupListeners();

        return view;
    }

    private void initializeViews(View view) {
        // Map
        mapView = view.findViewById(R.id.map);

        // Input fields - cast to AutoCompleteTextView for autocomplete
        startingPointInput = (AutoCompleteTextView) view.findViewById(R.id.starting_point_input);
        destinationInput = (AutoCompleteTextView) view.findViewById(R.id.destination_input);
        additionalInstructionsInput = view.findViewById(R.id.additional_instructions_input);

        // Setup autocomplete adapters
        startAdapter = new AddressAutocompleteAdapter(requireContext());
        endAdapter = new AddressAutocompleteAdapter(requireContext());

        startingPointInput.setAdapter(startAdapter);
        destinationInput.setAdapter(endAdapter);

        startingPointInput.setThreshold(3);
        destinationInput.setThreshold(3);

        // Handle selection of autocomplete items
        startingPointInput.setOnItemClickListener((parent, view1, position, id) -> {
            GeocodingResult result = startAdapter.getItem(position);
            if (result != null) {
                startCoordinates = result.getLatitude() + ", " + result.getLongitude();
                startingPointInput.setText(result.getDisplayName());
            }
        });

        destinationInput.setOnItemClickListener((parent, view1, position, id) -> {
            GeocodingResult result = endAdapter.getItem(position);
            if (result != null) {
                endCoordinates = result.getLatitude() + ", " + result.getLongitude();
                destinationInput.setText(result.getDisplayName());
            }
        });

        // Containers
        stopsContainer = view.findViewById(R.id.stops_container);
        passengersContainer = view.findViewById(R.id.passengers_container);
        timePickerContainer = view.findViewById(R.id.time_picker_container);
        priceContainer = view.findViewById(R.id.price_container);

        // Buttons
        btnAddStop = view.findViewById(R.id.btn_add_stop);
        btnAddPassenger = view.findViewById(R.id.btn_add_passenger);
        btnBookRide = view.findViewById(R.id.btn_book_ride);

        // Spinners
        vehicleTypeSpinner = view.findViewById(R.id.vehicle_type_spinner);
        scheduleTypeSpinner = view.findViewById(R.id.schedule_type_spinner);

        // Switches
        switchPets = view.findViewById(R.id.switch_pets);
        switchBabies = view.findViewById(R.id.switch_babies);

        // Number Pickers
        hourPicker = view.findViewById(R.id.hour_picker);
        minutePicker = view.findViewById(R.id.minute_picker);

        // TextViews
        estimatedPriceText = view.findViewById(R.id.estimated_price_text);
    }

    private void setupMap() {
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(true);

        // Set default location to Novi Sad
        GeoPoint centerPoint = new GeoPoint(45.2576, 19.8442);
        mapView.getController().setZoom(16.0);
        mapView.getController().setCenter(centerPoint);
    }

    private void setupSpinners() {
        // Vehicle Type Spinner
        ArrayAdapter<CharSequence> vehicleAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.vehicle_types,
                android.R.layout.simple_spinner_item);
        vehicleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleTypeSpinner.setAdapter(vehicleAdapter);

        // Schedule Type Spinner
        ArrayAdapter<CharSequence> scheduleAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.schedule_types,
                android.R.layout.simple_spinner_item);
        scheduleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scheduleTypeSpinner.setAdapter(scheduleAdapter);
    }

    private void setupNumberPickers() {
        // Get current time
        Calendar now = Calendar.getInstance();
        currentHour = now.get(Calendar.HOUR_OF_DAY);
        currentMinute = now.get(Calendar.MINUTE);

        // Generate available hours (current hour + next 5 hours = 6 hours total)
        availableHours = new String[6];
        for (int i = 0; i < 6; i++) {
            int hour = (currentHour + i) % 24;
            availableHours[i] = String.format("%02d", hour);
        }

        // Set up hour picker
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(availableHours.length - 1);
        hourPicker.setDisplayedValues(availableHours);
        hourPicker.setValue(0);
        hourPicker.setWrapSelectorWheel(false);

        // Set up minute picker for current hour
        updateAvailableMinutes(0);

        // Add listener to hour picker to update minutes
        hourPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            updateAvailableMinutes(newVal);
        });
    }

    private void updateAvailableMinutes(int selectedHourIndex) {
        int selectedHour = (currentHour + selectedHourIndex) % 24;

        if (selectedHour == currentHour) {
            // For current hour, only show minutes from current minute onwards
            int minutesCount = 60 - currentMinute;
            availableMinutes = new String[minutesCount];
            for (int i = 0; i < minutesCount; i++) {
                availableMinutes[i] = String.format("%02d", currentMinute + i);
            }
        } else {
            // For future hours, show all 60 minutes
            availableMinutes = new String[60];
            for (int i = 0; i < 60; i++) {
                availableMinutes[i] = String.format("%02d", i);
            }
        }

        // Update minute picker
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(availableMinutes.length - 1);
        minutePicker.setDisplayedValues(availableMinutes);
        minutePicker.setValue(0);
        minutePicker.setWrapSelectorWheel(false);
    }

    private void setupBottomSheet(View view) {
        MaterialCardView bottomSheet = view.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void setupListeners() {
        // Add Stop button
        btnAddStop.setOnClickListener(v -> addStopField());

        // Add Passenger button
        btnAddPassenger.setOnClickListener(v -> addPassengerField());

        // Book Ride button (no functionality yet, just placeholder)
        btnBookRide.setOnClickListener(v -> {
            // TODO: Implement booking logic
        });

        // Schedule type spinner listener
        scheduleTypeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                // Show/hide time picker based on selection
                if (position == 1) { // "schedule for later"
                    timePickerContainer.setVisibility(View.VISIBLE);
                } else { // "now"
                    timePickerContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                timePickerContainer.setVisibility(View.GONE);
            }
        });
    }

    private void addStopField() {
        View stopView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_stop, stopsContainer, false);

        Button removeButton = stopView.findViewById(R.id.btn_remove_stop);
        removeButton.setOnClickListener(v -> stopsContainer.removeView(stopView));

        stopsContainer.addView(stopView);
        stopsContainer.setVisibility(View.VISIBLE);
    }

    private void addPassengerField() {
        View passengerView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_passenger, passengersContainer, false);

        Button removeButton = passengerView.findViewById(R.id.btn_remove_passenger);
        removeButton.setOnClickListener(v -> passengersContainer.removeView(passengerView));

        passengersContainer.addView(passengerView);
        passengersContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null) {
            mapView.onDetach();
        }
    }
}
