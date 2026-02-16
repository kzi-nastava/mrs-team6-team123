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
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.TokenManager;
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

public class TrackRideFragment extends Fragment
        implements StopRideDialogFragment.OnRideStoppedListener,
        CancelRideDialogFragment.OnRideCancelledListener {

    private MapView mapView;
    private Marker vehicleMarker;
    private Button btnPanic, btnReport, btnFinish, btnStop, btnCancel;
    private TextView tvRouteName, tvStartedAt, tvTimeLeft,
            tvDriver, tvPrice, tvPassengers, tvReports,
            tvPassengersHeading, tvReportsHeading;
    private View viewButtons, viewPassengers;
    private static final String ARG_RIDE_ID = "ride_id";
    private Long rideId;
    private Long driverId;
    private TrackRideRepository trackRideRepository;
    private ActiveVehicleRepository activeVehicleRepository;
    private RideRepository rideRepository;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable refreshRunnable;
    private BitmapDrawable taxiIcon;
    private BitmapDrawable stopIcon;
    private boolean rideInfoInitialized = false;
    private MapRouteHelper mapRouteHelper;
    private DrawMarkerHelper drawMarkerHelper;
    private TrackRideDTO dto;

    // ── Fallback helpers for when tracking endpoint fails ────────
    private String getDriverName() {
        if (dto != null && dto.getInfo() != null && dto.getInfo().getDriver() != null)
            return dto.getInfo().getDriver();
        return "Driver";
    }

    private String getFromLocation() {
        if (dto != null && dto.getInfo() != null && dto.getInfo().getFrom() != null)
            return dto.getInfo().getFrom();
        return "Unknown";
    }

    private String getToLocation() {
        if (dto != null && dto.getInfo() != null && dto.getInfo().getTo() != null)
            return dto.getInfo().getTo();
        return "Unknown";
    }

    private double getRidePrice() {
        if (dto != null && dto.getInfo() != null)
            return dto.getInfo().getPrice();
        return 0.0;
    }

    private String getFirstPassenger() {
        if (dto != null && dto.getInfo() != null
                && dto.getInfo().getPassengers() != null
                && !dto.getInfo().getPassengers().isEmpty())
            return dto.getInfo().getPassengers().get(0);
        return "Passenger";
    }

    private String getCurrentVehicleLocation() {
        if (vehicleMarker != null) {
            return vehicleMarker.getPosition().getLatitude()
                    + ", " + vehicleMarker.getPosition().getLongitude();
        }
        return "45.2550, 19.8450";
    }

    // ── Fragment lifecycle ───────────────────────────────────────

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
        btnCancel = view.findViewById(R.id.btnCancel);
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
        Bitmap originalBitmap = ((BitmapDrawable) ContextCompat.getDrawable(
                requireContext(), R.drawable.taxi)).getBitmap();
        Bitmap smallBitmap = Bitmap.createScaledBitmap(originalBitmap, newSize, newSize, true);
        taxiIcon = new BitmapDrawable(getResources(), smallBitmap);

        originalBitmap = ((BitmapDrawable) ContextCompat.getDrawable(
                requireContext(), R.drawable.location_icon)).getBitmap();
        smallBitmap = Bitmap.createScaledBitmap(originalBitmap, newSize, newSize, true);
        stopIcon = new BitmapDrawable(getResources(), smallBitmap);

        mapSetup();
        mapRouteHelper = new MapRouteHelper(mapView);
        drawMarkerHelper = new DrawMarkerHelper(mapView);

        // All buttons work regardless of whether dto is loaded
        btnFinish.setOnClickListener(v -> finishRide());
        btnReport.setOnClickListener(v -> reportDriver());
        btnPanic.setOnClickListener(v -> openPanicDialog());
        btnStop.setOnClickListener(v -> openStopDialog());
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> openCancelDialog());
        }

        return view;
    }

    // ── Map setup ────────────────────────────────────────────────

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
            mapRouteHelper.fetchRoute(stops.get(i), stops.get(i + 1));
        }
        for (GeoPointDTO stop : stops)
            drawMarkerHelper.drawMarkers(stop, stopIcon);
    }

    private void showToast(String message) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }

    // ── Load ride data ───────────────────────────────────────────

    private void loadRide() {
        trackRideRepository.trackRide(rideId, new Callback<TrackRideDTO>() {
            @Override
            public void onResponse(@NonNull Call<TrackRideDTO> call,
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
                } else {
                    // Tracking failed (e.g. 500) — log but don't block UI
                    android.util.Log.w("TrackRide",
                            "Tracking endpoint returned " + response.code()
                                    + " — buttons still work");
                }
            }

            @Override
            public void onFailure(@NonNull Call<TrackRideDTO> call,
                                  @NonNull Throwable t) {
                if (isAdded()) {
                    android.util.Log.w("TrackRide",
                            "Tracking network error: " + t.getMessage());
                }
            }
        });
    }

    private void loadVehicle(Long driverId) {
        if (driverId == null) return;

        activeVehicleRepository.getDriversVehicle(driverId,
                new Callback<ActiveVehicleDTO>() {
                    @Override
                    public void onResponse(@NonNull Call<ActiveVehicleDTO> call,
                                           @NonNull Response<ActiveVehicleDTO> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful() && response.body() != null) {
                            updateVehicleMarker(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ActiveVehicleDTO> call,
                                          @NonNull Throwable t) {
                    }
                });
    }

    private void updateVehicleMarker(ActiveVehicleDTO vehicle) {
        GeoPoint point = new GeoPoint(vehicle.getLatitude(), vehicle.getLongitude());
        if (vehicleMarker == null) {
            vehicleMarker = new Marker(mapView);
            vehicleMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            vehicleMarker.setIcon(taxiIcon);
            mapView.getOverlays().add(vehicleMarker);
        }
        vehicleMarker.setPosition(point);
        mapView.invalidate();
    }

    // ── UI updates ───────────────────────────────────────────────

    private void updateRideStaticUI(TrackRideDTO dto) {
        TokenManager tokenManager = ApiClient.getTokenManager();
        String userRole = tokenManager.getRole();
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
        if (btnCancel != null) btnCancel.setVisibility(View.GONE);
        viewButtons.setVisibility(View.GONE);
    }

    private void updateTimeLeft(TrackRideDTO dto) {
        tvTimeLeft.setText(String.format("%d min", dto.getInfo().getDuration()));
    }

    // ── Auto refresh ─────────────────────────────────────────────

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

    // ── Dialog openers (NO dto null check — use fallbacks) ──────

    private void openPanicDialog() {
        TokenManager tokenManager = ApiClient.getTokenManager();

        PanicDialogFragment dialog = PanicDialogFragment.newInstance(
                rideId,
                tokenManager.getUserId(),
                getDriverName(),
                getCurrentVehicleLocation());

        dialog.show(getChildFragmentManager(), "panic_dialog");
    }

    private void openStopDialog() {
        StopRideDialogFragment dialog = StopRideDialogFragment.newInstance(
                rideId,
                getFirstPassenger(),
                getToLocation(),
                getRidePrice(),
                5.5);

        dialog.show(getChildFragmentManager(), "stop_dialog");
    }

    private void openCancelDialog() {
        TokenManager tokenManager = ApiClient.getTokenManager();

        CancelRideDialogFragment dialog = CancelRideDialogFragment.newInstance(
                rideId,
                tokenManager.getUserId(),
                getFromLocation(),
                getToLocation(),
                getDriverName());

        dialog.show(getChildFragmentManager(), "cancel_dialog");
    }

    // ── Callbacks from dialogs ───────────────────────────────────

    @Override
    public void onRideStopped(String newDestination, double newPrice) {
        showToast("Ride stopped. New price: " + (int) newPrice + " RSD");
        if (isAdded()) {
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onRideCancelled() {
        showToast("Ride cancelled successfully");
        if (isAdded()) {
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    // ── Actions ──────────────────────────────────────────────────

    public void finishRide() {
        btnFinish.setEnabled(false);
        rideRepository.finishRide(rideId, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,
                                   @NonNull Response<Void> response) {
                btnFinish.setEnabled(true);
                if (response.isSuccessful()) {
                    if (isAdded()) showToast("Ride successfully finished!");
                } else {
                    if (isAdded()) showToast("Error while finishing the ride");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call,
                                  @NonNull Throwable t) {
                btnFinish.setEnabled(true);
                if (isAdded()) showToast("Failed finishing ride");
            }
        });
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    public void reportDriver() {
        if (dto == null) {
            showToast("Ride data not loaded yet. Please try again.");
            return;
        }
        Fragment fragment = IrregularityReportFragment.newInstance(dto);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    // ── Lifecycle ────────────────────────────────────────────────

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
}