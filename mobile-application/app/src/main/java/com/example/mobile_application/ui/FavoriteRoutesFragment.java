package com.example.mobile_application.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.FavoriteRouteDTO;
import com.example.mobile_application.repository.FavoritesRepository;
import com.example.mobile_application.service.TokenManager;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.ui.base.BaseRideBookingFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteRoutesFragment extends BaseRideBookingFragment {
    // Favorites list components (unique to this fragment)
    private RecyclerView recyclerView;
    private FavoriteRoutesAdapter adapter;
    private ProgressBar progressBar;
    private TextView errorMessage;
    private LinearLayout emptyState;
    private FavoritesRepository repository;
    private Long passengerId;
    private TokenManager tokenManager;

    @Override
    protected String getLogTag() {
        return "FavoriteRoutes";
    }

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

        // Only setup map if the layout actually has a map view
        if (mapView != null) {
            initializeMarkerIcon();
            setupMap();
            setupAutocomplete();
        }

        setupBottomSheet(view);
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
            View scheduleRideCard = view.findViewById(R.id.schedule_ride_card);
            if (scheduleRideCard != null) {
                scheduleRideCard.setVisibility(View.GONE);
            }
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
        // Get references from view for base class fields
        mapView = view.findViewById(R.id.map); // This fragment doesn't have a map, but keeping for consistency
        startingPointInput = view.findViewById(R.id.starting_point_input);
        destinationInput = view.findViewById(R.id.destination_input);
        additionalInstructionsInput = view.findViewById(R.id.additional_instructions_input);
        stopsContainer = view.findViewById(R.id.stops_container);
        passengersContainer = view.findViewById(R.id.passengers_container);
        timePickerContainer = view.findViewById(R.id.time_picker_container);
        priceContainer = view.findViewById(R.id.price_container);
        btnAddStop = view.findViewById(R.id.btn_add_stop);
        btnAddPassenger = view.findViewById(R.id.btn_add_passenger);
        btnBookRide = view.findViewById(R.id.btn_book_ride);
        vehicleTypeSpinner = view.findViewById(R.id.vehicle_type_spinner);
        scheduleTypeSpinner = view.findViewById(R.id.schedule_type_spinner);
        switchPets = view.findViewById(R.id.switch_pets);
        switchBabies = view.findViewById(R.id.switch_babies);
        hourPicker = view.findViewById(R.id.hour_picker);
        minutePicker = view.findViewById(R.id.minute_picker);
        estimatedPriceText = view.findViewById(R.id.estimated_price_text);
    }

    private void setupBottomSheet(View view) {
        MaterialCardView scheduleRideCard = view.findViewById(R.id.schedule_ride_card);
        if (scheduleRideCard != null) {
            bottomSheetBehavior = BottomSheetBehavior.from(scheduleRideCard);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
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

        // Expand the bottom sheet if it exists
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

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