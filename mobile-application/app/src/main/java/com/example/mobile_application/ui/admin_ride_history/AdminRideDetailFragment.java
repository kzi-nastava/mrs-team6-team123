
package com.example.mobile_application.ui.admin_ride_history;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.AdminRideHistoryDTO;
import com.example.mobile_application.dto.GeoPointDTO;
import com.example.mobile_application.dto.PassengerInfoDTO;
import com.example.mobile_application.helper.DrawMarkerHelper;
import com.example.mobile_application.helper.MapRouteHelper;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

public class AdminRideDetailFragment extends Fragment {

    private static final String ARG_RIDE = "ride";
    private AdminRideHistoryDTO ride;

    private MapView mapView;
    private TextView tvRoute, tvDate, tvTime, tvPrice, tvDistance,
            tvDriver, tvCreator, tvPassengers,
            tvCancelled, tvPanic, tvRatings, tvReports;
    private Button btnBack;

    public static AdminRideDetailFragment newInstance(AdminRideHistoryDTO ride) {
        AdminRideDetailFragment f = new AdminRideDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RIDE, ride);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ride = (AdminRideHistoryDTO) getArguments().getSerializable(ARG_RIDE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_ride_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.mapDetail);
        tvRoute = view.findViewById(R.id.tvDetailRoute);
        tvDate = view.findViewById(R.id.tvDetailDate);
        tvTime = view.findViewById(R.id.tvDetailTime);
        tvPrice = view.findViewById(R.id.tvDetailPrice);
        tvDistance = view.findViewById(R.id.tvDetailDistance);
        tvDriver = view.findViewById(R.id.tvDetailDriver);
        tvCreator = view.findViewById(R.id.tvDetailCreator);
        tvPassengers = view.findViewById(R.id.tvDetailPassengers);
        tvCancelled = view.findViewById(R.id.tvDetailCancelled);
        tvPanic = view.findViewById(R.id.tvDetailPanic);
        tvRatings = view.findViewById(R.id.tvDetailRatings);
        tvReports = view.findViewById(R.id.tvDetailReports);
        btnBack = view.findViewById(R.id.btnDetailBack);

        setupMap();
        populateData();
        drawRoute();

        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void setupMap() {
        Configuration.getInstance().setUserAgentValue(
                requireContext().getPackageName());
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(14.0);
    }

    private void populateData() {
        if (ride == null) return;

        tvRoute.setText(ride.getStartLocation() + " → " + ride.getEndLocation());
        tvDate.setText(String.format("Date: %s", ride.getDate()));
        tvTime.setText(String.format("Time: %s - %s",
                ride.getStartedAt() != null ? ride.getStartedAt() : "?",
                ride.getEndedAt() != null ? ride.getEndedAt() : "?"));
        tvPrice.setText(String.format("Price: %.0f RSD", ride.getPrice()));
        tvDistance.setText(String.format("Distance: %.1f km", ride.getTotalDistance()));
        tvDriver.setText(String.format("Driver: %s", ride.getDriverName()));
        tvCreator.setText(String.format("Ordered by: %s", ride.getCreatorName()));

        // Passengers
        if (ride.getPassengers() != null && !ride.getPassengers().isEmpty()) {
            List<String> names = new ArrayList<>();
            for (PassengerInfoDTO p : ride.getPassengers()) {
                names.add(p.getName());
            }
            tvPassengers.setText("Passengers: " + TextUtils.join(", ", names));
            tvPassengers.setVisibility(View.VISIBLE);
        } else {
            tvPassengers.setVisibility(View.GONE);
        }

        // Cancelled
        if (ride.isCancelled()) {
            tvCancelled.setText(String.format("Cancelled by: %s (%s)",
                    ride.getCancelledByName(), ride.getCancelledByRole()));
            tvCancelled.setVisibility(View.VISIBLE);
        } else {
            tvCancelled.setVisibility(View.GONE);
        }

        // Panic
        if (ride.isPanicTriggered()) {
            tvPanic.setText("🚨 PANIC was triggered");
            tvPanic.setVisibility(View.VISIBLE);
        } else {
            tvPanic.setVisibility(View.GONE);
        }

        // Ratings
        if (ride.isRated()) {
            tvRatings.setText(String.format("Ratings - Driver: ★ %.1f  Vehicle: ★ %.1f",
                    ride.getDriverRating(), ride.getVehicleRating()));
        } else {
            tvRatings.setText("Not rated");
        }

        // Reports
        if (ride.getInconsistencyReports() != null
                && !ride.getInconsistencyReports().isEmpty()) {
            tvReports.setText("Reports:\n• " + TextUtils.join("\n• ",
                    ride.getInconsistencyReports()));
            tvReports.setVisibility(View.VISIBLE);
        } else {
            tvReports.setVisibility(View.GONE);
        }
    }

    private void drawRoute() {
        if (ride == null) return;

        MapRouteHelper routeHelper = new MapRouteHelper(mapView);
        DrawMarkerHelper markerHelper = new DrawMarkerHelper(mapView);

        Bitmap original = ((BitmapDrawable) ContextCompat.getDrawable(
                requireContext(), R.drawable.location_icon)).getBitmap();
        Bitmap small = Bitmap.createScaledBitmap(original, 36, 36, true);
        BitmapDrawable icon = new BitmapDrawable(getResources(), small);

        GeoPointDTO start = new GeoPointDTO();
        start.setLatitude(ride.getStartLat());
        start.setLongitude(ride.getStartLng());

        GeoPointDTO end = new GeoPointDTO();
        end.setLatitude(ride.getEndLat());
        end.setLongitude(ride.getEndLng());

        routeHelper.fetchRoute(start, end);
        markerHelper.drawMarkers(start, icon);
        markerHelper.drawMarkers(end, icon);

        double padding = 0.01;
        BoundingBox box = new BoundingBox(
                Math.max(ride.getStartLat(), ride.getEndLat()) + padding,
                Math.max(ride.getStartLng(), ride.getEndLng()) + padding,
                Math.min(ride.getStartLat(), ride.getEndLat()) - padding,
                Math.min(ride.getStartLng(), ride.getEndLng()) - padding);
        mapView.post(() -> mapView.zoomToBoundingBox(box, true));
    }
}