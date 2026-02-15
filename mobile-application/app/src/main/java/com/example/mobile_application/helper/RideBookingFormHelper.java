package com.example.mobile_application.helper;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import org.osmdroid.views.MapView;

/**
 * Helper class for managing ride booking form state.
 * Centralizes form clearing and reset logic.
 */
public class RideBookingFormHelper {

    private final AutoCompleteTextView startingPointInput;
    private final AutoCompleteTextView destinationInput;
    private final TextInputEditText additionalInstructionsInput;
    private final SwitchMaterial switchPets;
    private final SwitchMaterial switchBabies;
    private final Spinner vehicleTypeSpinner;
    private final Spinner scheduleTypeSpinner;
    private final LinearLayout stopsContainer;
    private final LinearLayout passengersContainer;
    private final MapView mapView;

    public RideBookingFormHelper(
            AutoCompleteTextView startingPointInput,
            AutoCompleteTextView destinationInput,
            TextInputEditText additionalInstructionsInput,
            SwitchMaterial switchPets,
            SwitchMaterial switchBabies,
            Spinner vehicleTypeSpinner,
            Spinner scheduleTypeSpinner,
            LinearLayout stopsContainer,
            LinearLayout passengersContainer,
            MapView mapView) {
        this.startingPointInput = startingPointInput;
        this.destinationInput = destinationInput;
        this.additionalInstructionsInput = additionalInstructionsInput;
        this.switchPets = switchPets;
        this.switchBabies = switchBabies;
        this.vehicleTypeSpinner = vehicleTypeSpinner;
        this.scheduleTypeSpinner = scheduleTypeSpinner;
        this.stopsContainer = stopsContainer;
        this.passengersContainer = passengersContainer;
        this.mapView = mapView;
    }

    /**
     * Clears all form inputs and resets UI state.
     */
    public void clearForm() {
        clearTextInputs();
        clearSwitches();
        clearSpinners();
        clearContainers();
        clearMapView();
    }

    private void clearTextInputs() {
        startingPointInput.setText("");
        destinationInput.setText("");
        additionalInstructionsInput.setText("");
    }

    private void clearSwitches() {
        switchPets.setChecked(false);
        switchBabies.setChecked(false);
    }

    private void clearSpinners() {
        vehicleTypeSpinner.setSelection(0);
        scheduleTypeSpinner.setSelection(0);
    }

    private void clearContainers() {
        stopsContainer.removeAllViews();
        passengersContainer.removeAllViews();
        stopsContainer.setVisibility(View.GONE);
        passengersContainer.setVisibility(View.GONE);
    }

    private void clearMapView() {
        if (mapView != null) {
            mapView.getOverlays().clear();
            mapView.invalidate();
        }
    }
}
