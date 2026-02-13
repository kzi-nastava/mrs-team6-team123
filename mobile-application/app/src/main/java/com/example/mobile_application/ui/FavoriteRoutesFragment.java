package com.example.mobile_application.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.FavoriteRouteDTO;
import com.example.mobile_application.dto.GeocodingResult;
import com.example.mobile_application.repository.FavoritesRepository;
import com.example.mobile_application.service.TokenManager;
import com.example.mobile_application.service.ApiClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteRoutesFragment extends Fragment {
    // Favorites list components
    private RecyclerView recyclerView;
    private FavoriteRoutesAdapter adapter;
    private ProgressBar progressBar;
    private TextView errorMessage;
    private LinearLayout emptyState;
    private FavoritesRepository repository;
    private Long passengerId;
    private TokenManager tokenManager;

    // Schedule ride components
    private MaterialCardView scheduleRideCard;
    private BottomSheetBehavior<MaterialCardView> bottomSheetBehavior;
    private AutoCompleteTextView startingPointInput;
    private AutoCompleteTextView destinationInput;
    private TextInputEditText additionalInstructionsInput;
    private String startCoordinates = "";
    private String endCoordinates = "";
    private AddressAutocompleteAdapter startAdapter;
    private AddressAutocompleteAdapter endAdapter;
    private LinearLayout stopsContainer;
    private LinearLayout passengersContainer;
    private LinearLayout timePickerContainer;
    private LinearLayout priceContainer;
    private Button btnAddStop;
    private Button btnAddPassenger;
    private Button btnBookRide;
    private Spinner vehicleTypeSpinner;
    private Spinner scheduleTypeSpinner;
    private SwitchMaterial switchPets;
    private SwitchMaterial switchBabies;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private TextView estimatedPriceText;
    private int currentHour;
    private int currentMinute;
    private String[] availableHours;
    private String[] availableMinutes;

    public FavoriteRoutesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_routes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeFavoritesViews(view);
        initializeScheduleRideViews(view);
        setupBottomSheet();
        setupSpinners();
        setupNumberPickers();
        setupListeners();

        repository = new FavoritesRepository();
        tokenManager = ApiClient.getTokenManager();
        passengerId = tokenManager.getUserId();

        if (passengerId == null || passengerId == -1L) {
            errorMessage.setText("Please log in to view your favorite routes");
            errorMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.GONE);
            scheduleRideCard.setVisibility(View.GONE);
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FavoriteRoutesAdapter(new ArrayList<>());
        adapter.setBookClickListener(this::populateAndScrollToForm);
        adapter.setDeleteClickListener(this::deleteFavoriteRoute);
        recyclerView.setAdapter(adapter);

        loadFavoriteRoutes();
    }

    private void initializeFavoritesViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_routes);
        progressBar = view.findViewById(R.id.progress_bar);
        errorMessage = view.findViewById(R.id.error_message);
        emptyState = view.findViewById(R.id.empty_state);
    }

    private void initializeScheduleRideViews(View view) {
        scheduleRideCard = view.findViewById(R.id.schedule_ride_card);

        // Input fields
        startingPointInput = view.findViewById(R.id.starting_point_input);
        destinationInput = view.findViewById(R.id.destination_input);
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

    private void setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(scheduleRideCard);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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

    private void setupListeners() {
        // Add Stop button
        btnAddStop.setOnClickListener(v -> addStopField());

        // Add Passenger button
        btnAddPassenger.setOnClickListener(v -> addPassengerField());

        // Book Ride button
        btnBookRide.setOnClickListener(v -> {
            // TODO: Implement booking logic
            String start = startingPointInput.getText().toString();
            String end = destinationInput.getText().toString();
            boolean immediate = scheduleTypeSpinner.getSelectedItemPosition() == 0;

            Toast.makeText(getContext(),
                    "Booking: " + start + " → " + end + ", Immediate: " + immediate,
                    Toast.LENGTH_SHORT).show();
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
        if (passengerId != null) {
            loadFavoriteRoutes();
        }
    }

    private void loadFavoriteRoutes() {
        progressBar.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);

        repository.getFavoriteRoutes(passengerId, new Callback<List<FavoriteRouteDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<FavoriteRouteDTO>> call,
                    @NonNull Response<List<FavoriteRouteDTO>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<FavoriteRouteDTO> routes = response.body();

                    if (routes.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter = new FavoriteRoutesAdapter(routes);
                        adapter.setBookClickListener(FavoriteRoutesFragment.this::populateAndScrollToForm);
                        adapter.setDeleteClickListener(FavoriteRoutesFragment.this::deleteFavoriteRoute);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    errorMessage.setText("Failed to load favorite routes");
                    errorMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FavoriteRouteDTO>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                errorMessage.setText("Error loading favorite routes");
                errorMessage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void populateAndScrollToForm(FavoriteRouteDTO route) {
        // Populate the form with favorite route data
        startingPointInput.setText(route.getStartLocation());
        destinationInput.setText(route.getEndLocation());

        // TODO: Parse coordinates from route if available
        // For now, leave coordinates empty - user can use autocomplete if needed

        // Expand the bottom sheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        Toast.makeText(getContext(), "Route populated - review and book", Toast.LENGTH_SHORT).show();
    }

    private void deleteFavoriteRoute(int position, FavoriteRouteDTO route) {
        repository.removeFavoriteRoute(passengerId, route.getId(), new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    adapter.removeItem(position);
                    Toast.makeText(getContext(), "Favorite route removed", Toast.LENGTH_SHORT).show();

                    if (adapter.getItemCount() == 0) {
                        emptyState.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to remove favorite route", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error removing favorite route", Toast.LENGTH_SHORT).show();
            }
        });
    }
}