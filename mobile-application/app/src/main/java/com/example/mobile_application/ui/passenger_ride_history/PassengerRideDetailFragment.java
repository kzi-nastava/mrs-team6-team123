
package com.example.mobile_application.ui.passenger_ride_history;

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
import com.example.mobile_application.dto.GeoPointDTO;
import com.example.mobile_application.dto.PassengerRideHistoryDTO;
import com.example.mobile_application.helper.DrawMarkerHelper;
import com.example.mobile_application.helper.MapRouteHelper;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class PassengerRideDetailFragment extends Fragment {

    private static final String ARG_RIDE = "ride";
    private PassengerRideHistoryDTO ride;

    private MapView mapView;
    private TextView tvRoute, tvDate, tvTime, tvPrice,
            tvDriver, tvDriverRating, tvRideRating, tvReports;
    private Button btnBack;

    public static PassengerRideDetailFragment newInstance(PassengerRideHistoryDTO ride) {
        PassengerRideDetailFragment f = new PassengerRideDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RIDE, ride);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ride = (PassengerRideHistoryDTO) getArguments().getSerializable(ARG_RIDE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_ride_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.mapDetail);
        tvRoute = view.findViewById(R.id.tvDetailRoute);
        tvDate = view.findViewById(R.id.tvDetailDate);
        tvTime = view.findViewById(R.id.tvDetailTime);
        tvPrice = view.findViewById(R.id.tvDetailPrice);
        tvDriver = view.findViewById(R.id.tvDetailDriver);
        tvDriverRating = view.findViewById(R.id.tvDetailDriverRating);
        tvRideRating = view.findViewById(R.id.tvDetailRideRating);
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

        String route = ride.getStartLocation() + " → " + ride.getEndLocation();
        tvRoute.setText(route);
        tvDate.setText(ride.getDate());
        tvTime.setText(String.format("%s - %s",
                ride.getStartedAt() != null ? ride.getStartedAt() : "?",
                ride.getEndedAt() != null ? ride.getEndedAt() : "?"));
        tvPrice.setText(String.format("%.0f RSD", ride.getPrice()));
        tvDriver.setText(ride.getDriverName());
        tvDriverRating.setText(String.format("Driver avg: ★ %.1f",
                ride.getDriverRating()));

        if (ride.isRated()) {
            tvRideRating.setText(String.format(
                    "Your rating - Driver: ★ %.1f  Vehicle: ★ %.1f",
                    ride.getRideDriverRating(), ride.getRideVehicleRating()));
        } else {
            tvRideRating.setText("Not rated yet");
        }

        if (ride.getInconsistencyReports() != null
                && !ride.getInconsistencyReports().isEmpty()) {
            tvReports.setText("Reports:\n" + TextUtils.join("\n• ",
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

        // Zoom to fit
        double padding = 0.01;
        BoundingBox box = new BoundingBox(
                Math.max(ride.getStartLat(), ride.getEndLat()) + padding,
                Math.max(ride.getStartLng(), ride.getEndLng()) + padding,
                Math.min(ride.getStartLat(), ride.getEndLat()) - padding,
                Math.min(ride.getStartLng(), ride.getEndLng()) - padding);
        mapView.post(() -> mapView.zoomToBoundingBox(box, true));
    }
}