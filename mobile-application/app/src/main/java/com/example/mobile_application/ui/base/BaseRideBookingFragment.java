package com.example.mobile_application.ui.base;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.mobile_application.dto.RideEstimationRequestDTO;
import com.example.mobile_application.dto.RideEstimationResponseDTO;
import com.example.mobile_application.dto.RideOrderRequestDTO;
import com.example.mobile_application.dto.RideResponseDTO;
import com.example.mobile_application.dto.UserProfileDTO;
import com.example.mobile_application.helper.ApiResponseHandler;
import com.example.mobile_application.helper.DrawMarkerHelper;
import com.example.mobile_application.helper.LocationCoordinateParser;
import com.example.mobile_application.helper.MapRouteHelper;
import com.example.mobile_application.helper.RideBookingFormHelper;
import com.example.mobile_application.helper.RideBookingService;
import com.example.mobile_application.enums.VehicleType;
import com.example.mobile_application.service.ApiClient;
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

    // Passenger and user management
    protected List<String> passengerEmails = new ArrayList<>();
    protected RideBookingFormHelper formHelper;
    protected RideBookingService bookingService;
    protected com.example.mobile_application.service.RideEstimationService estimationService;

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

        setupStartLocationListener();
        setupEndLocationListener();
    }

    private void setupStartLocationListener() {
        startingPointInput.setOnItemClickListener((parent, view, position, id) -> {
            GeocodingResult result = startAdapter.getItem(position);
            if (result != null) {
                startCoordinates = result.getLatitude() + ", " + result.getLongitude();
                startingPointInput.setText(result.getDisplayName());
                drawRouteIfBothLocationsSet();
            }
        });
    }

    private void setupEndLocationListener() {
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
        setupSpinnerAdapter(vehicleTypeSpinner, R.array.vehicle_types);
        setupSpinnerAdapter(scheduleTypeSpinner, R.array.schedule_types);
    }

    private void setupSpinnerAdapter(Spinner spinner, int arrayResource) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                arrayResource,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    protected void setupNumberPickers() {
        Calendar now = Calendar.getInstance();
        currentHour = now.get(Calendar.HOUR_OF_DAY);
        currentMinute = now.get(Calendar.MINUTE);

        availableHours = generateHours();
        setupHourPicker();
        updateAvailableMinutes(0);
        hourPicker.setOnValueChangedListener((picker, oldVal, newVal) -> updateAvailableMinutes(newVal));
    }

    private String[] generateHours() {
        String[] hours = new String[6];
        for (int i = 0; i < 6; i++) {
            int hour = (currentHour + i) % 24;
            hours[i] = String.format("%02d", hour);
        }
        return hours;
    }

    private void setupHourPicker() {
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(availableHours.length - 1);
        hourPicker.setDisplayedValues(availableHours);
        hourPicker.setValue(0);
        hourPicker.setWrapSelectorWheel(false);
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
        btnAddStop.setOnClickListener(v -> addStopField());
        btnAddPassenger.setOnClickListener(v -> addPassengerField());
        btnBookRide.setOnClickListener(v -> bookRide());
        setupScheduleTypeListener();
    }

    private void setupScheduleTypeListener() {
        scheduleTypeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                timePickerContainer.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
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
        EditText passengerInput = passengerView.findViewById(R.id.passenger_input);
        Button removeButton = passengerView.findViewById(R.id.btn_remove_passenger);

        removeButton.setOnClickListener(v -> {
            int index = passengersContainer.indexOfChild(passengerView);
            if (index >= 0 && index < passengerEmails.size()) {
                passengerEmails.remove(index);
            }
            passengersContainer.removeView(passengerView);
            if (passengersContainer.getChildCount() == 0) {
                passengersContainer.setVisibility(View.GONE);
            }
        });

        passengersContainer.addView(passengerView);
        passengersContainer.setVisibility(View.VISIBLE);
        passengerEmails.add("");

        final int myIndex = passengerEmails.size() - 1;
        passengerInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (myIndex < passengerEmails.size()) {
                    passengerEmails.set(myIndex, s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });
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
        if (!validateBookingInputs()) {
            return;
        }

        Long userId = ApiClient.getTokenManager().getUserId();
        if (userId == null || userId <= 0) {
            Toast.makeText(requireContext(), "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Long> passengerIds = new ArrayList<>();
        passengerIds.add(userId);
        List<String> validPassengerEmails = collectPassengerEmails();

        if (bookingService == null) {
            bookingService = new RideBookingService();
        }

        if (!validPassengerEmails.isEmpty()) {
            bookingService.resolvePassengerEmails(
                    validPassengerEmails,
                    passengerIds,
                    () -> submitRideOrderWithPassengers(userId, passengerIds),
                    () -> Toast.makeText(requireContext(),
                            "One or more passenger emails not found. Please check and try again.", Toast.LENGTH_SHORT)
                            .show());
        } else {
            submitRideOrderWithPassengers(userId, passengerIds);
        }
    }

    private boolean validateBookingInputs() {
        String startLocation = startingPointInput.getText().toString().trim();
        String endLocation = destinationInput.getText().toString().trim();

        if (startLocation.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter starting point", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (endLocation.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter destination", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (startCoordinates.isEmpty() || endCoordinates.isEmpty()) {
            Toast.makeText(requireContext(), "Please select locations from suggestions", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private List<String> collectPassengerEmails() {
        List<String> validEmails = new ArrayList<>();
        for (String email : passengerEmails) {
            if (email != null && !email.isEmpty()) {
                validEmails.add(email);
            }
        }
        return validEmails;
    }

    private void submitRideOrderWithPassengers(Long userId, List<Long> passengerIds) {
        // First estimate the price, then submit ride
        estimateRidePrice(userId, passengerIds);
    }

    private void estimateRidePrice(Long userId, List<Long> passengerIds) {
        if (estimationService == null) {
            estimationService = new com.example.mobile_application.service.RideEstimationService();
        }

        String vehicleType = ((String) vehicleTypeSpinner.getSelectedItem()).toUpperCase();
        RideEstimationRequestDTO estimationRequest = new RideEstimationRequestDTO();
        estimationRequest.setStartLocation(startCoordinates);
        estimationRequest.setEndLocation(endCoordinates);
        estimationRequest.setVehicleType(VehicleType.valueOf(vehicleType));

        estimationService.estimateRide(
                estimationRequest,
                new Callback<RideEstimationResponseDTO>() {
                    @Override
                    public void onResponse(Call<RideEstimationResponseDTO> call,
                            Response<RideEstimationResponseDTO> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            RideOrderRequestDTO request = buildRideOrderRequest(userId, passengerIds);
                            request.setEstimatedPrice(response.body().getEstimatedPrice());
                            submitRideOrder(request);
                        }
                    }

                    @Override
                    public void onFailure(Call<RideEstimationResponseDTO> call, Throwable t) {
                        // Handled by error callback
                    }
                },
                new Callback<RideEstimationResponseDTO>() {
                    @Override
                    public void onResponse(Call<RideEstimationResponseDTO> call,
                            Response<RideEstimationResponseDTO> response) {
                        Toast.makeText(requireContext(), "Failed to estimate price", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<RideEstimationResponseDTO> call, Throwable t) {
                        Toast.makeText(requireContext(), "Price estimation error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void submitRideOrder(RideOrderRequestDTO request) {
        if (bookingService == null) {
            bookingService = new RideBookingService();
        }

        bookingService.submitRideOrder(
                request,
                new Callback<RideResponseDTO>() {
                    @Override
                    public void onResponse(Call<RideResponseDTO> call, Response<RideResponseDTO> response) {
                        handleRideBookingSuccess(call, response);
                    }

                    @Override
                    public void onFailure(Call<RideResponseDTO> call, Throwable t) {
                        handleRideBookingFailure(call, t);
                    }
                },
                new Callback<RideResponseDTO>() {
                    @Override
                    public void onResponse(Call<RideResponseDTO> call, Response<RideResponseDTO> response) {
                        handleRideBookingError(call, response);
                    }

                    @Override
                    public void onFailure(Call<RideResponseDTO> call, Throwable t) {
                        // Error callback doesn't need failure handler
                    }
                },
                new Callback<RideResponseDTO>() {
                    @Override
                    public void onResponse(Call<RideResponseDTO> call, Response<RideResponseDTO> response) {
                        // Failure callback doesn't need response handler
                    }

                    @Override
                    public void onFailure(Call<RideResponseDTO> call, Throwable t) {
                        handleRideBookingFailure(call, t);
                    }
                });
    }

    private RideOrderRequestDTO buildRideOrderRequest(Long userId, List<Long> passengerIds) {
        RideOrderRequestDTO request = new RideOrderRequestDTO();

        request.setCreatorId(userId);
        request.setPassengerIds(passengerIds);
        request.setStartLocation(startingPointInput.getText().toString());
        request.setEndLocation(destinationInput.getText().toString());

        // Parse and set coordinates
        double[] startCoord = parseCoordinates(startCoordinates);
        double[] endCoord = parseCoordinates(endCoordinates);
        request.setStartLatitude(startCoord[0]);
        request.setStartLongitude(startCoord[1]);
        request.setEndLatitude(endCoord[0]);
        request.setEndLongitude(endCoord[1]);

        // Set waypoints
        request.setWaypoints(new ArrayList<>());

        // Set schedule
        String scheduleType = (String) scheduleTypeSpinner.getSelectedItem();
        request.setScheduledAt(buildScheduledAtString(scheduleType));

        // Set preferences
        request.setBabySeat(switchBabies.isChecked());
        request.setPetFriendly(switchPets.isChecked());

        // Set vehicle and instructions
        String vehicleTypeDisplay = (String) vehicleTypeSpinner.getSelectedItem();
        request.setVehicleType(vehicleTypeDisplay.toUpperCase());
        request.setAdditionalInstructions(additionalInstructionsInput.getText().toString().trim());
        request.setEstimatedPrice(0.0); // Will be overridden by estimation call

        return request;
    }

    private double[] parseCoordinates(String coordinateString) {
        return LocationCoordinateParser.parseCoordinates(coordinateString);
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

    private void handleRideBookingSuccess(Call<RideResponseDTO> call, Response<RideResponseDTO> response) {
        RideResponseDTO rideResponse = response.body();
        String message = "Ride booked successfully!\n" +
                "Driver: " + rideResponse.getDriverName() + "\n" +
                "Vehicle: " + rideResponse.getVehicleLicense();
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        clearForm();
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void handleRideBookingError(Call<RideResponseDTO> call, Response<RideResponseDTO> response) {
        String errorMessage = ApiResponseHandler.extractErrorMessage(response, "Failed to book ride", getLogTag());
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    private void handleRideBookingFailure(Call<RideResponseDTO> call, Throwable t) {
        Toast.makeText(requireContext(), "Connection error: " + t.getMessage(), Toast.LENGTH_LONG).show();
    }

    protected void clearForm() {
        if (formHelper != null) {
            formHelper.clearForm();
        }
        startCoordinates = "";
        endCoordinates = "";
    }

    protected void initializeFormHelper() {
        formHelper = new RideBookingFormHelper(
                startingPointInput,
                destinationInput,
                additionalInstructionsInput,
                switchPets,
                switchBabies,
                vehicleTypeSpinner,
                scheduleTypeSpinner,
                stopsContainer,
                passengersContainer,
                mapView);
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
