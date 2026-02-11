package com.example.mobile_application.ui.rate_ride;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.RateRideRequestDTO;
import com.example.mobile_application.dto.RateRideResponseDTO;
import com.example.mobile_application.repository.RateRideRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RateRideFragment extends Fragment {

    private static final String ARG_RIDE = "ride";
    private RateRideRequestDTO ride;
    private TextView tvDriver, tvVehicle;
    private EditText etComment;
    private Button btnRate, btnCancel;
    private ImageView[] driverStars, vehicleStars;
    private RateRideRepository repository;
    private int driverRating, vehicleRating;

    public static RateRideFragment newInstance(RateRideRequestDTO ride) {
        RateRideFragment fragment = new RateRideFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RIDE, ride);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate_ride, container, false);

        tvDriver = view.findViewById(R.id.tvDriver);
        tvVehicle = view.findViewById(R.id.tvVehicle);
        etComment = view.findViewById(R.id.etComment);
        btnRate = view.findViewById(R.id.btnRate);
        btnCancel = view.findViewById(R.id.btnCancel);
        driverStars = new ImageView[5];
        driverStars[0] = view.findViewById(R.id.star1Driver);
        driverStars[1] = view.findViewById(R.id.star2Driver);
        driverStars[2] = view.findViewById(R.id.star3Driver);
        driverStars[3] = view.findViewById(R.id.star4Driver);
        driverStars[4] = view.findViewById(R.id.star5Driver);

        vehicleStars = new ImageView[5];
        vehicleStars[0] = view.findViewById(R.id.star1Vehicle);
        vehicleStars[1] = view.findViewById(R.id.star2Vehicle);
        vehicleStars[2] = view.findViewById(R.id.star3Vehicle);
        vehicleStars[3] = view.findViewById(R.id.star4Vehicle);
        vehicleStars[4] = view.findViewById(R.id.star5Vehicle);

        repository = new RateRideRepository();

        for (int i = 0; i < driverStars.length; i++) {
            final int index = i;
            driverStars[i].setOnClickListener(v -> {
                driverRating = index + 1;
                updateStars(driverStars, driverRating);
            });
        }

        for (int i = 0; i < vehicleStars.length; i++) {
            final int index  = i;
            vehicleStars[i].setOnClickListener(v -> {
                vehicleRating = index  + 1;
                updateStars(vehicleStars, vehicleRating);
            });
        }

        if (getArguments() != null) {
            ride = (RateRideRequestDTO) getArguments().getSerializable("ride");
        }

        tvDriver.setText(String.format("Driver: %s", ride.getDriver()));
        tvVehicle.setText(String.format("Vehicle: %s", ride.getLicencePlate()));

        btnCancel.setOnClickListener(v -> cancel());
        btnRate.setOnClickListener(v -> rate());

        return view;
    }

    public void cancel() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    public void rate() {
        if (driverRating == 0 || vehicleRating == 0) {
            showToast("Please select a rating");
            return;
        }
        String comment = etComment.getText().toString().trim();
        if (comment.isEmpty())
            comment = "";
        RateRideResponseDTO dto = new RateRideResponseDTO();
        dto.setRideId(ride.getRideId());
        dto.setAuthorId(2L); // TODO: current user id
        dto.setDriverId(ride.getDriverId());
        dto.setVehicleId(ride.getVehicleId());
        dto.setDriverRating(driverRating);
        dto.setVehicleRating(vehicleRating);
        dto.setComment(comment);
        btnRate.setEnabled(false);
        repository.rateRide(dto, new Callback<Void>() {
            @Override
            public void onResponse(
                    @NonNull Call<Void> call,
                    @NonNull Response<Void> response) {
                btnRate.setEnabled(true);
                if (response.isSuccessful())
                    if (isAdded())
                        showToast("Ride rated successfully");
            }

            @Override
            public void onFailure(
                    @NonNull Call<Void> call,
                    @NonNull Throwable t) {
                btnRate.setEnabled(true);
                if (isAdded())
                    showToast("Failed rating the ride");
            }
        });
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void updateStars(ImageView[] stars, int rating) {
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setImageResource(R.drawable.star_colored);
            } else {
                stars[i].setImageResource(R.drawable.star_outline);
            }
        }
    }

    private void showToast(String message) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }
}