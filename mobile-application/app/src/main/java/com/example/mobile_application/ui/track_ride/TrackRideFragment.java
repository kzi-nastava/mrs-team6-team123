package com.example.mobile_application.ui.track_ride;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.ActiveVehicleDTO;
import com.example.mobile_application.dto.GeoPointDTO;
import com.example.mobile_application.dto.TrackRideDTO;
import com.example.mobile_application.helper.DrawMarkerHelper;
import com.example.mobile_application.helper.MapRouteHelper;
import com.example.mobile_application.repository.ActiveVehicleRepository;
import com.example.mobile_application.repository.RideRepository;
import com.example.mobile_application.repository.TrackRideRepository;
import com.example.mobile_application.ui.irregularity_report.IrregularityReportFragment;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackRideFragment extends Fragment {
    private MapView mapView;
    private Marker vehicleMarker;
    private Button btnPanic, btnReport, btnFinish, btnStop;
    private TextView
            tvRouteName, tvStartedAt, tvTimeLeft,
            tvDriver, tvPrice, tvPassengers, tvReports,
            tvPassengersHeading, tvReportsHeading;
    private View viewButtons, viewPassengers;
    private static final String ARG_RIDE_ID = "ride_id";
    private Long rideId;
    private Long driverId;
    private TrackRideRepository trackRideRepository;
    private ActiveVehicleRepository activeVehicleRepository;
    private RideRepository rideRepository;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable refreshRunnable;
    private BitmapDrawable taxiIcon;
    private BitmapDrawable stopIcon;
    private boolean rideInfoInitialized = false;
    private MapRouteHelper mapRouteHelper;
    private DrawMarkerHelper drawMarkerHelper;
    private TrackRideDTO dto;

    public static TrackRideFragment newInstance(Long rideId) {
        TrackRideFragment fragment = new TrackRideFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_ride, container, false);

        mapView = view.findViewById(R.id.map);
        btnPanic = view.findViewById(R.id.btnPanic);
        btnReport = view.findViewById(R.id.btnReport);
        btnFinish = view.findViewById(R.id.btnFinish);
        btnStop = view.findViewById(R.id.btnStop);
        tvRouteName = view.findViewById(R.id.tvRouteName);
        tvStartedAt = view.findViewById(R.id.tvStartedAt);
        tvTimeLeft = view.findViewById(R.id.tvTimeLeft);
        tvDriver = view.findViewById(R.id.tvDriver);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvPassengers = view.findViewById(R.id.tvPassengers);
        tvReports = view.findViewById(R.id.tvReports);
        tvPassengersHeading = view.findViewById(R.id.tvPassengersHeading);
        tvReportsHeading = view.findViewById(R.id.tvReportsHeading);
        viewButtons = view.findViewById(R.id.viewButtons);
        viewPassengers = view.findViewById(R.id.viewPassengers);
        trackRideRepository = new TrackRideRepository();
        activeVehicleRepository = new ActiveVehicleRepository();
        rideRepository = new RideRepository();

        int newSize = 36;
        Bitmap originalBitmap = ((BitmapDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.taxi)).getBitmap();
        Bitmap smallBitmap = Bitmap.createScaledBitmap(originalBitmap, newSize, newSize, true);
        taxiIcon = new BitmapDrawable(getResources(), smallBitmap);

        originalBitmap = ((BitmapDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.location_icon)).getBitmap();
        smallBitmap = Bitmap.createScaledBitmap(originalBitmap, newSize, newSize, true);
        stopIcon = new BitmapDrawable(getResources(), smallBitmap);

        mapSetup();
        mapRouteHelper = new MapRouteHelper(mapView);
        drawMarkerHelper = new DrawMarkerHelper(mapView);

        btnFinish.setOnClickListener(v -> finishRide());
        btnReport.setOnClickListener(v -> reportDriver());

        return view;
    }

    private void mapSetup() {
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        GeoPoint centerPoint = new GeoPoint(45.2576, 19.8442);
        MapController mapController = (MapController) mapView.getController();
        mapController.setZoom(16.0);
        mapController.setCenter(centerPoint);
    }

    private void showRoute(List<GeoPointDTO> stops) {
        for (int i = 0; i < stops.size() - 1; ++i) {
            mapRouteHelper.fetchRoute(stops.get(i), stops.get(i+1));
        }
        for (GeoPointDTO stop : stops)
            drawMarkerHelper.drawMarkers(stop, stopIcon);
    }

    private void showToast(String message) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }

    private void loadRide() {
        trackRideRepository.trackRide(rideId, new Callback<TrackRideDTO>() {
            @Override
            public void onResponse(
                    @NonNull Call<TrackRideDTO> call,
                    @NonNull Response<TrackRideDTO> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    dto = response.body();

                    if (!rideInfoInitialized) {
                        updateRideStaticUI(dto);
                        showRoute(dto.getStops());
                    }

                    updateTimeLeft(dto);

                    driverId = dto.getDriverId();
                    loadVehicle(driverId);
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<TrackRideDTO> call,
                    @NonNull Throwable t) {
                if (isAdded())
                    showToast("Failed loading ride to track");
            }
        });
    }

    private void loadVehicle(Long driverId) {
        if (driverId == null) return;

        activeVehicleRepository.getDriversVehicle(driverId,
                new Callback<ActiveVehicleDTO>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<ActiveVehicleDTO> call,
                            @NonNull Response<ActiveVehicleDTO> response) {

                        if (!isAdded()) return;

                        if (response.isSuccessful() && response.body() != null) {
                            updateVehicleMarker(response.body());
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ActiveVehicleDTO> call,
                            @NonNull Throwable t) {
                        if (isAdded())
                            showToast("Failed showing driver's vehicle");
                    }
                });
    }

    private void updateVehicleMarker(ActiveVehicleDTO vehicle) {
        GeoPoint point = new GeoPoint(
                vehicle.getLatitude(),
                vehicle.getLongitude()
        );

        if (vehicleMarker == null) {
            vehicleMarker = new Marker(mapView);
            vehicleMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            vehicleMarker.setIcon(taxiIcon);
            mapView.getOverlays().add(vehicleMarker);
        }

        vehicleMarker.setPosition(point);
        mapView.invalidate();
    }


    private void updateRideStaticUI(TrackRideDTO dto) {
        String userRole = "PASSENGER"; // current logged in user role
        String routeStr = dto.getInfo().getFrom() + " -> " + dto.getInfo().getTo();
        tvRouteName.setText(routeStr);
        tvPrice.setText(String.format("%sRSD", dto.getInfo().getPrice()));
        tvDriver.setText(dto.getInfo().getDriver());
        tvStartedAt.setText(dto.getInfo().getStartedAt());
        String passengersText = TextUtils.join("\n", dto.getInfo().getPassengers());
        tvPassengers.setText(passengersText);
        String reportsText = TextUtils.join("\n", dto.getInfo().getReports());
        tvReports.setText(reportsText);
        if (userRole.equals(getString(R.string.role_passenger)))
            setVisibilityPassenger();
        if (userRole.equals(getString(R.string.role_driver)))
            setVisibilityDriver();
        if (userRole.equals(getString(R.string.role_admin)))
            hideButtons();

        rideInfoInitialized = true;
    }

    private void setVisibilityPassenger() {
        tvPrice.setVisibility(View.GONE);
        tvPassengers.setVisibility(View.GONE);
        tvReports.setVisibility(View.GONE);
        tvDriver.setVisibility(View.GONE);
        tvStartedAt.setVisibility(View.GONE);
        tvPassengersHeading.setVisibility(View.GONE);
        tvReportsHeading.setVisibility(View.GONE);
        btnStop.setVisibility(View.GONE);
        btnFinish.setVisibility(View.GONE);
        viewPassengers.setVisibility(View.GONE);
    }

    private void setVisibilityDriver() {
        tvPassengers.setVisibility(View.GONE);
        tvReports.setVisibility(View.GONE);
        tvDriver.setVisibility(View.GONE);
        tvPassengersHeading.setVisibility(View.GONE);
        tvReportsHeading.setVisibility(View.GONE);
        tvStartedAt.setVisibility(View.GONE);
        btnReport.setVisibility(View.GONE);
        viewPassengers.setVisibility(View.GONE);
    }

    private void hideButtons() {
        btnStop.setVisibility(View.GONE);
        btnFinish.setVisibility(View.GONE);
        btnReport.setVisibility(View.GONE);
        btnPanic.setVisibility(View.GONE);
        viewButtons.setVisibility(View.GONE);
    }

    // TODO: live time left update
    private void updateTimeLeft(TrackRideDTO dto) {
        tvTimeLeft.setText(String.format("%d min", dto.getInfo().getDuration()));
    }

    private void startAutoRefresh() {
        if (refreshRunnable != null) return;
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadRide();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(refreshRunnable);
    }

    private void stopAutoRefresh() {
        if (refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
            refreshRunnable = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startAutoRefresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoRefresh();
    }

    public void finishRide() {
        btnFinish.setEnabled(false);
        rideRepository.finishRide(rideId, new Callback<Void>() {
            @Override
            public void onResponse(
                    @NonNull Call<Void> call,
                    @NonNull Response<Void> response) {
                btnFinish.setEnabled(true);
                if (response.isSuccessful()) {
                    if (isAdded())
                        showToast("Ride successfully finished!");
                } else {
                    if (isAdded())
                        showToast("Error while finishing the ride");
                }
            }
            @Override
            public void onFailure(
                    @NonNull Call<Void> call,
                    @NonNull Throwable t) {
                btnFinish.setEnabled(true);
                if (isAdded())
                    showToast("Failed finishing ride");
            }
        });
    }

    public void reportDriver() {
        Fragment fragment = IrregularityReportFragment.newInstance(dto);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}