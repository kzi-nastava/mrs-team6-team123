package com.example.mobile_application.ui.base;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.GeoPointDTO;
import com.example.mobile_application.dto.GeocodingResult;
import com.example.mobile_application.dto.RideOrderRequestDTO;
import com.example.mobile_application.dto.RideResponseDTO;
import com.example.mobile_application.helper.DrawMarkerHelper;
import com.example.mobile_application.helper.MapRouteHelper;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.RideOrderService;
import com.example.mobile_application.service.TokenManager;
import com.example.mobile_application.ui.AddressAutocompleteAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseRideBookingFragment extends Fragment {

    // Map components
    protected MapView mapView;
    protected MapRouteHelper mapRouteHelper;
    protected DrawMarkerHelper drawMarkerHelper;
    protected BitmapDrawable stopIcon;
    protected BottomSheetBehavior<MaterialCardView> bottomSheetBehavior;

    // Input fields
    protected AutoCompleteTextView startingPointInput;
    protected AutoCompleteTextView destinationInput;
    protected TextInputEditText additionalInstructionsInput;

    // Coordinates storage
    protected String startCoordinates = "";
    protected String endCoordinates = "";

    // Autocomplete adapters
    protected AddressAutocompleteAdapter startAdapter;
    protected AddressAutocompleteAdapter endAdapter;

    // Containers
    protected LinearLayout stopsContainer;
    protected LinearLayout passengersContainer;
    protected LinearLayout timePickerContainer;
    protected LinearLayout priceContainer;

    // Buttons
    protected Button btnAddStop;
    protected Button btnAddPassenger;
    protected Button btnBookRide;

    // Spinners
    protected Spinner vehicleTypeSpinner;
    protected Spinner scheduleTypeSpinner;

    // Switches
    protected SwitchMaterial switchPets;
    protected SwitchMaterial switchBabies;

    // Number Pickers
    protected NumberPicker hourPicker;
    protected NumberPicker minutePicker;

    // TextViews
    protected TextView estimatedPriceText;

    // Time restriction variables
    protected int currentHour;
    protected int currentMinute;
    protected String[] availableHours;
    protected String[] availableMinutes;

    protected abstract String getLogTag();

    protected void initializeMarkerIcon() {
        int iconSize = 36;
        Bitmap originalBitmap = ((BitmapDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.location_icon))
                .getBitmap();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, iconSize, iconSize, true);
        stopIcon = new BitmapDrawable(getResources(), scaledBitmap);
    }

    protected void setupMap() {
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(true);

        // Set default location to Novi Sad
        GeoPoint centerPoint = new GeoPoint(45.2576, 19.8442);
        mapView.getController().setZoom(16.0);
        mapView.getController().setCenter(centerPoint);

        // Initialize helpers
        mapRouteHelper = new MapRouteHelper(mapView);
        drawMarkerHelper = new DrawMarkerHelper(mapView);

        // Force map to render
        mapView.invalidate();
    }

    protected void setupAutocomplete() {
        startAdapter = new AddressAutocompleteAdapter(requireContext());
        endAdapter = new AddressAutocompleteAdapter(requireContext());

        startingPointInput.setAdapter(startAdapter);
        destinationInput.setAdapter(endAdapter);

        startingPointInput.setThreshold(3);
        destinationInput.setThreshold(3);

        // Handle selection of autocomplete items
        startingPointInput.setOnItemClickListener((parent, view, position, id) -> {
            GeocodingResult result = startAdapter.getItem(position);
            if (result != null) {
                startCoordinates = result.getLatitude() + ", " + result.getLongitude();
                startingPointInput.setText(result.getDisplayName());
                drawRouteIfBothLocationsSet();
            }
        });

        destinationInput.setOnItemClickListener((parent, view, position, id) -> {
            GeocodingResult result = endAdapter.getItem(position);
            if (result != null) {
                endCoordinates = result.getLatitude() + ", " + result.getLongitude();
                destinationInput.setText(result.getDisplayName());
                drawRouteIfBothLocationsSet();
            }
        });
    }

    protected void setupSpinners() {
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

    protected void setupNumberPickers() {
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

    protected void updateAvailableMinutes(int selectedHourIndex) {
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

    protected void setupListeners() {
        // Add Stop button
        btnAddStop.setOnClickListener(v -> addStopField());

        // Add Passenger button
        btnAddPassenger.setOnClickListener(v -> addPassengerField());

        // Book Ride button
        btnBookRide.setOnClickListener(v -> bookRide());

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

    protected void addStopField() {
        View stopView = getLayoutInflater().inflate(R.layout.item_stop, stopsContainer, false);
        Button removeButton = stopView.findViewById(R.id.btn_remove_stop);
        removeButton.setOnClickListener(v -> stopsContainer.removeView(stopView));
        stopsContainer.addView(stopView);
        stopsContainer.setVisibility(View.VISIBLE);
    }

    protected void addPassengerField() {
        View passengerView = getLayoutInflater().inflate(R.layout.item_passenger, passengersContainer, false);
        Button removeButton = passengerView.findViewById(R.id.btn_remove_passenger);
        removeButton.setOnClickListener(v -> passengersContainer.removeView(passengerView));
        passengersContainer.addView(passengerView);
        passengersContainer.setVisibility(View.VISIBLE);
    }

    protected void drawRouteIfBothLocationsSet() {
        if (startCoordinates.isEmpty() || endCoordinates.isEmpty()) {
            return;
        }

        try {
            // Parse coordinates
            String[] startParts = startCoordinates.split(",\\s*");
            String[] endParts = endCoordinates.split(",\\s*");

            if (startParts.length == 2 && endParts.length == 2) {
                double startLat = Double.parseDouble(startParts[0]);
                double startLng = Double.parseDouble(startParts[1]);
                double endLat = Double.parseDouble(endParts[0]);
                double endLng = Double.parseDouble(endParts[1]);

                GeoPointDTO startPoint = new GeoPointDTO(startLat, startLng, startingPointInput.getText().toString());
                GeoPointDTO endPoint = new GeoPointDTO(endLat, endLng, destinationInput.getText().toString());

                // Clear previous overlays to avoid multiple routes
                mapView.getOverlays().clear();

                // Draw markers for start and end locations
                drawMarkerHelper.drawMarkers(startPoint, stopIcon);
                drawMarkerHelper.drawMarkers(endPoint, stopIcon);

                // Draw route
                mapRouteHelper.fetchRoute(startPoint, endPoint);

                Log.d(getLogTag(), "Drawing route from " + startCoordinates + " to " + endCoordinates);
            }
        } catch (Exception e) {
            Log.e(getLogTag(), "Error drawing route: " + e.getMessage());
        }
    }

    protected void bookRide() {
        Log.d(getLogTag(), "bookRide() called");

        // Validate inputs
        String startLocation = startingPointInput.getText().toString().trim();
        String endLocation = destinationInput.getText().toString().trim();

        Log.d(getLogTag(), "StartLocation: " + startLocation);
        Log.d(getLogTag(), "EndLocation: " + endLocation);
        Log.d(getLogTag(), "StartCoordinates: " + startCoordinates);
        Log.d(getLogTag(), "EndCoordinates: " + endCoordinates);

        if (startLocation.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter starting point", Toast.LENGTH_SHORT).show();
            return;
        }

        if (endLocation.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter destination", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startCoordinates.isEmpty() || endCoordinates.isEmpty()) {
            Toast.makeText(requireContext(), "Please select locations from suggestions", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get user ID
        TokenManager tokenManager = ApiClient.getTokenManager();
        Long userId = tokenManager.getUserId();

        Log.d(getLogTag(), "UserId: " + userId);

        if (userId == null || userId <= 0) {
            Toast.makeText(requireContext(), "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse coordinates
        String[] startCoord = startCoordinates.split(",\\s*");
        String[] endCoord = endCoordinates.split(",\\s*");

        if (startCoord.length < 2 || endCoord.length < 2) {
            Toast.makeText(requireContext(), "Invalid location coordinates", Toast.LENGTH_SHORT).show();
            return;
        }

        Double startLatitude = Double.parseDouble(startCoord[0].trim());
        Double startLongitude = Double.parseDouble(startCoord[1].trim());
        Double endLatitude = Double.parseDouble(endCoord[0].trim());
        Double endLongitude = Double.parseDouble(endCoord[1].trim());

        Log.d(getLogTag(), "Parsed coordinates - Start: " + startLatitude + "," + startLongitude +
                " End: " + endLatitude + "," + endLongitude);

        // Get vehicle type
        String vehicleTypeDisplay = (String) vehicleTypeSpinner.getSelectedItem();
        String vehicleType = vehicleTypeDisplay.toUpperCase();

        Log.d(getLogTag(), "VehicleType: " + vehicleType);

        // Get schedule info
        String scheduleType = (String) scheduleTypeSpinner.getSelectedItem();
        String scheduledAt = buildScheduledAtString(scheduleType);

        Log.d(getLogTag(), "ScheduledAt: " + scheduledAt);

        // Get preferences
        Boolean petFriendly = switchPets.isChecked();
        Boolean babySeat = switchBabies.isChecked();

        // Get additional instructions
        String additionalInstructions = additionalInstructionsInput.getText().toString().trim();

        // Build passenger IDs list (empty - only creator)
        List<Long> passengerIds = new ArrayList<>();

        // Build waypoints from stops
        List<String> waypoints = new ArrayList<>();

        // Build the request
        RideOrderRequestDTO request = new RideOrderRequestDTO();
        request.setCreatorId(userId);
        request.setPassengerIds(passengerIds);
        request.setStartLocation(startLocation);
        request.setEndLocation(endLocation);
        request.setStartLatitude(startLatitude);
        request.setStartLongitude(startLongitude);
        request.setEndLatitude(endLatitude);
        request.setEndLongitude(endLongitude);
        request.setWaypoints(waypoints);
        request.setScheduledAt(scheduledAt);
        request.setBabySeat(babySeat);
        request.setPetFriendly(petFriendly);
        request.setVehicleType(vehicleType);
        request.setAdditionalInstructions(additionalInstructions);
        request.setEstimatedPrice(0.0);

        Log.d(getLogTag(), "Request built, calling submitRideOrder...");

        // Call the API
        submitRideOrder(request);
    }

    protected String buildScheduledAtString(String scheduleType) {
        if ("now".equalsIgnoreCase(scheduleType)) {
            LocalDateTime now = LocalDateTime.now();
            // Format without fractional seconds: YYYY-MM-DDTHH:mm:ss
            return now.withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } else {
            // "schedule for later"
            int hourIndex = hourPicker.getValue();
            int minuteIndex = minutePicker.getValue();
            String selectedHourStr = availableHours[hourIndex];
            String selectedMinuteStr = availableMinutes[minuteIndex];

            int hour = Integer.parseInt(selectedHourStr);
            int minute = Integer.parseInt(selectedMinuteStr);

            LocalDateTime scheduledTime = LocalDateTime.now();
            scheduledTime = scheduledTime.withHour(hour).withMinute(minute).withSecond(0).withNano(0);

            return scheduledTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }

    protected void submitRideOrder(RideOrderRequestDTO request) {
        Log.d(getLogTag(), "submitRideOrder() called");

        RideOrderService service = ApiClient.getInstance().create(RideOrderService.class);
        Call<RideResponseDTO> call = service.orderRide(request);

        call.enqueue(new Callback<RideResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<RideResponseDTO> call, @NonNull Response<RideResponseDTO> response) {
                Log.d(getLogTag(), "onResponse called, code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    RideResponseDTO rideResponse = response.body();
                    String message = "Ride booked successfully!\n" +
                            "Driver: " + rideResponse.getDriverName() + "\n" +
                            "Vehicle: " + rideResponse.getVehicleLicense();
                    Log.d(getLogTag(), "Ride booked successfully");
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();

                    // Clear form
                    clearForm();

                    // Collapse bottom sheet if it exists
                    if (bottomSheetBehavior != null) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                } else {
                    Log.d(getLogTag(), "Response not successful. Code: " + response.code());

                    String errorMessage = "Failed to book ride (Error " + response.code() + ")";

                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.d(getLogTag(), "Error body: " + errorBody);
                            if (errorBody != null && !errorBody.trim().isEmpty()) {
                                errorMessage = errorBody;
                            } else {
                                errorMessage = "Server error (Code " + response.code() + ") - " + response.message();
                            }
                        } else {
                            errorMessage = "Server error (Code " + response.code() + ") - " + response.message();
                        }
                    } catch (Exception e) {
                        Log.e(getLogTag(), "Error parsing error body", e);
                        errorMessage = "Error: " + response.code() + " - " + response.message();
                    }

                    Log.d(getLogTag(), "Showing error toast: " + errorMessage);
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RideResponseDTO> call, @NonNull Throwable t) {
                String errorMsg = "Connection error: " + t.getMessage();
                Log.e(getLogTag(), "onFailure called", t);
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void clearForm() {
        startingPointInput.setText("");
        destinationInput.setText("");
        additionalInstructionsInput.setText("");
        switchPets.setChecked(false);
        switchBabies.setChecked(false);
        vehicleTypeSpinner.setSelection(0);
        scheduleTypeSpinner.setSelection(0);
        stopsContainer.removeAllViews();
        passengersContainer.removeAllViews();
        stopsContainer.setVisibility(View.GONE);
        passengersContainer.setVisibility(View.GONE);
        startCoordinates = "";
        endCoordinates = "";

        // Clear map overlays
        if (mapView != null) {
            mapView.getOverlays().clear();
            mapView.invalidate();
        }
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
