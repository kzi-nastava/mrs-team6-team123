package com.example.mobile_application.ui.track_ride;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
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
import com.example.mobile_application.repository.ActiveVehicleRepository;
import com.example.mobile_application.repository.TrackRideRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
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
    private static final String ARG_RIDE_ID = "ride_id";
    private Long rideId;
    private Long driverId;
    private TrackRideRepository trackRideRepository;
    private ActiveVehicleRepository activeVehicleRepository;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable refreshRunnable;
    private BitmapDrawable taxiIcon;
    private BitmapDrawable stopIcon;
    private boolean rideInfoInitialized = false;

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
        trackRideRepository = new TrackRideRepository();
        activeVehicleRepository = new ActiveVehicleRepository();

        int newSize = 36;
        Bitmap originalBitmap = ((BitmapDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.taxi)).getBitmap();
        Bitmap smallBitmap = Bitmap.createScaledBitmap(originalBitmap, newSize, newSize, true);
        taxiIcon = new BitmapDrawable(getResources(), smallBitmap);

        originalBitmap = ((BitmapDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.location_icon)).getBitmap();
        smallBitmap = Bitmap.createScaledBitmap(originalBitmap, newSize, newSize, true);
        stopIcon = new BitmapDrawable(getResources(), smallBitmap);

        mapSetup();

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

    private void drawMarkers(GeoPointDTO dto) {
        Marker marker = new Marker(mapView);
        GeoPoint point = new GeoPoint(dto.getLatitude(), dto.getLongitude());
        marker.setPosition(point);
        marker.setTitle(dto.getLocation());
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(stopIcon);
        mapView.getOverlays().add(marker);
    }

    private void showRoute(List<GeoPointDTO> stops) {
        for (int i = 0; i < stops.size() - 1; ++i) {
            fetchRoute(stops.get(i), stops.get(i+1));
        }
        for (GeoPointDTO stop : stops)
            drawMarkers(stop);
    }

    private void fetchRoute(GeoPointDTO stop1, GeoPointDTO stop2) {
        new Thread(() -> {
            try {
                if (stop1 == null || stop2 == null) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() ->
                                showToast("No points were sent")
                        );
                    }
                }
                GeoPoint startPoint = new GeoPoint(stop1.getLatitude(), stop1.getLongitude());
                GeoPoint endPoint = new GeoPoint(stop2.getLatitude(), stop2.getLongitude());
                if (startPoint != null && endPoint != null) {
                    List<GeoPoint> routePoints = getRoute(startPoint, endPoint);
                    if (routePoints != null && isAdded()) {
                        requireActivity().runOnUiThread(() ->
                                drawRoute(routePoints)
                        );
                    } else if (isAdded()) {
                        requireActivity().runOnUiThread(() ->
                                showToast("Unable to fetch route")
                        );
                    }
                } else if (isAdded()) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() ->
                                showToast("Error fetching route")
                        );
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    private List<GeoPoint> getRoute(GeoPoint startPoint, GeoPoint endPoint)
            throws IOException, JSONException {
        String url = "https://router.project-osrm.org/route/v1/driving/" +
                startPoint.getLongitude() + "," + startPoint.getLatitude() + ";" +
                endPoint.getLongitude() + "," + endPoint.getLatitude() +
                "?overview=simplified&geometries=geojson";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "OSMDroidExample/1.0, osmdroid@gmail.com")
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                return null;
            }
            JSONObject json = new JSONObject(response.body().string());
            JSONArray routes = json.optJSONArray("routes");
            if (routes == null || routes.length() == 0) {
                return null;
            }

            JSONArray coordinates = routes.getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONArray("coordinates");

            List<GeoPoint> routePoints = new ArrayList<>(coordinates.length());

            for (int i = 0; i < coordinates.length(); i++) {
                JSONArray point = coordinates.getJSONArray(i);
                double lon = point.getDouble(0);
                double lat = point.getDouble(1);
                routePoints.add(new GeoPoint(lat, lon));
            }

            return routePoints;
        }
    }

    private void drawRoute(List<GeoPoint> routePoints) {
        Polyline routeLine = new Polyline();
        routeLine.setPoints(routePoints);
        routeLine.setColor(0xFF0000FF);
        routeLine.setWidth(10.0f);

        mapView.getOverlays().add(routeLine);
        mapView.invalidate();

        if (!routePoints.isEmpty()) {
            IMapController mapController = mapView.getController();
            mapController.setZoom(15.0);
            mapController.setCenter(routePoints.get(0));
        }
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
                    TrackRideDTO dto = response.body();

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
        String userRole = "passenger";
        String routeStr = dto.getInfo().getFrom() + " -> " + dto.getInfo().getTo();
        tvRouteName.setText(routeStr);
        if (userRole.equals("passenger")) {
            tvPrice.setVisibility(View.GONE);
            tvPassengers.setVisibility(View.GONE);
            tvReports.setVisibility(View.GONE);
            tvDriver.setVisibility(View.GONE);
            tvStartedAt.setVisibility(View.GONE);
            tvPassengersHeading.setVisibility(View.GONE);
            tvReportsHeading.setVisibility(View.GONE);
            btnStop.setVisibility(View.GONE);
            btnFinish.setVisibility(View.GONE);
        }
        if (userRole.equals("driver")) {
            tvPassengers.setVisibility(View.GONE);
            tvReports.setVisibility(View.GONE);
            tvDriver.setVisibility(View.GONE);
            tvPassengersHeading.setVisibility(View.GONE);
            tvReportsHeading.setVisibility(View.GONE);
            tvStartedAt.setVisibility(View.GONE);
            btnReport.setVisibility(View.GONE);
        }
        if (userRole.equals("admin")) {
            btnStop.setVisibility(View.GONE);
            btnFinish.setVisibility(View.GONE);
            btnReport.setVisibility(View.GONE);
            btnPanic.setVisibility(View.GONE);
        }

        rideInfoInitialized = true;
    }

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
}