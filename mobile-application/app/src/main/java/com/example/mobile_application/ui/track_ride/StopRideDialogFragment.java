
package com.example.mobile_application.ui.track_ride;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.StopRideRequestDTO;
import com.example.mobile_application.dto.StopRideResponseDTO;
import com.example.mobile_application.repository.RideStopRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StopRideDialogFragment extends DialogFragment {

    private static final String ARG_RIDE_ID = "ride_id";
    private static final String ARG_PASSENGER = "passenger";
    private static final String ARG_DESTINATION = "destination";
    private static final String ARG_PRICE = "price";
    private static final String ARG_DISTANCE = "distance";

    private Long rideId;

    private TextView tvPassenger, tvDestination, tvOriginalPrice,
            tvNewPrice, tvError;
    private EditText etLocation;
    private Button btnStop, btnContinue;
    private LinearLayout layoutForm, layoutSuccess;
    private TextView tvSuccessLocation, tvSuccessPrice;
    private RideStopRepository repository;

    public interface OnRideStoppedListener {
        void onRideStopped(String newDestination, double newPrice);
    }

    public static StopRideDialogFragment newInstance(Long rideId, String passenger,
                                                     String destination, double price,
                                                     double distance) {
        StopRideDialogFragment fragment = new StopRideDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        args.putString(ARG_PASSENGER, passenger);
        args.putString(ARG_DESTINATION, destination);
        args.putDouble(ARG_PRICE, price);
        args.putDouble(ARG_DISTANCE, distance);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID);
        }
        repository = new RideStopRepository();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_stop_ride, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvPassenger = view.findViewById(R.id.tvStopPassenger);
        tvDestination = view.findViewById(R.id.tvStopDestination);
        tvOriginalPrice = view.findViewById(R.id.tvStopOriginalPrice);
        tvNewPrice = view.findViewById(R.id.tvStopNewPrice);
        tvError = view.findViewById(R.id.tvStopError);
        etLocation = view.findViewById(R.id.etStopLocation);
        btnStop = view.findViewById(R.id.btnConfirmStop);
        btnContinue = view.findViewById(R.id.btnContinueRide);
        layoutForm = view.findViewById(R.id.layoutStopForm);
        layoutSuccess = view.findViewById(R.id.layoutStopSuccess);
        tvSuccessLocation = view.findViewById(R.id.tvStopSuccessLocation);
        tvSuccessPrice = view.findViewById(R.id.tvStopSuccessPrice);

        Bundle args = getArguments();
        if (args != null) {
            tvPassenger.setText(args.getString(ARG_PASSENGER, ""));
            tvDestination.setText(args.getString(ARG_DESTINATION, ""));
            tvOriginalPrice.setText(String.format(Locale.getDefault(),
                    "%.0f RSD", args.getDouble(ARG_PRICE)));

            double distance = args.getDouble(ARG_DISTANCE, 0);
            double estimated = 300 + (distance * 120);
            tvNewPrice.setText(String.format(Locale.getDefault(),
                    "~%.0f RSD", estimated));
        }

        etLocation.setText(getCurrentCoordinates());

        btnContinue.setOnClickListener(v -> dismiss());
        btnStop.setOnClickListener(v -> performStop());
    }

    private String getCurrentCoordinates() {
        return "45.2550, 19.8450";
    }

    private String getCurrentIsoTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date());
    }

    private void performStop() {
        String location = etLocation.getText().toString().trim();
        if (location.isEmpty()) {
            tvError.setText("Cannot determine current location.");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        tvError.setVisibility(View.GONE);
        btnStop.setEnabled(false);
        btnStop.setText("Stopping...");
        btnContinue.setEnabled(false);

        StopRideRequestDTO request = new StopRideRequestDTO(
                location, getCurrentIsoTimestamp());

        repository.stopRide(rideId, request, new Callback<StopRideResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<StopRideResponseDTO> call,
                                   @NonNull Response<StopRideResponseDTO> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    StopRideResponseDTO body = response.body();

                    tvSuccessLocation.setText(body.getStoppedLocation());
                    tvSuccessPrice.setText(String.format(Locale.getDefault(),
                            "%.0f RSD", body.getRecalculatedPrice()));

                    layoutForm.setVisibility(View.GONE);
                    layoutSuccess.setVisibility(View.VISIBLE);

                    View root = getView();
                    if (root != null) {
                        root.postDelayed(() -> {
                            if (isAdded()) {
                                if (getParentFragment() instanceof OnRideStoppedListener) {
                                    ((OnRideStoppedListener) getParentFragment())
                                            .onRideStopped(body.getStoppedLocation(),
                                                    body.getRecalculatedPrice());
                                }
                                dismiss();
                            }
                        }, 3000);
                    }
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<StopRideResponseDTO> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                showError("Network error. Please try again.");
            }
        });
    }

    private void handleError(Response<StopRideResponseDTO> response) {
        try {
            String errorBody = response.errorBody() != null
                    ? response.errorBody().string() : "Failed to stop ride.";
            showError(errorBody);
        } catch (Exception e) {
            showError("Failed to stop ride.");
        }
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        btnStop.setEnabled(true);
        btnStop.setText("Stop Ride Here");
        btnContinue.setEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}