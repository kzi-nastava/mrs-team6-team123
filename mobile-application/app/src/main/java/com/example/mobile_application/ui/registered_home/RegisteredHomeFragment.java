package com.example.mobile_application.ui.registered_home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobile_application.R;
import com.example.mobile_application.ui.base.BaseRideBookingFragment;
import com.google.android.material.card.MaterialCardView;

public class RegisteredHomeFragment extends BaseRideBookingFragment {

    @Override
    protected String getLogTag() {
        return "RegisteredHome";
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registered_home, container, false);

        // Initialize marker icon and views
        initializeMarkerIcon();
        initializeViews(view);
        setupMap();
        setupAutocomplete();
        setupSpinners();
        setupNumberPickers();
        setupBottomSheet(view);
        setupListeners();

        return view;
    }

    private void initializeViews(View view) {
        // Get references from view for base class fields
        mapView = view.findViewById(R.id.map);
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
            bottomSheetBehavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(scheduleRideCard);
            bottomSheetBehavior.setState(com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED);
        }
    }
}
