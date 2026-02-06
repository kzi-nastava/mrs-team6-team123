package com.example.mobile_application.ui.driver_ride_history;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile_application.R;
import com.example.mobile_application.model.DriverRideHistoryDTO;
import com.example.mobile_application.model.GeoPointDTO;

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
import okhttp3.Response;

public class RideDetailsFragment extends Fragment {

    private MapView mapView;
    private TextView tvRoute;
    private TextView tvStartedAt;
    private TextView tvEndedAt;
    private TextView tvPrice;
    private TextView tvPanic;
    private TextView tvCanceledBy;
    private TextView tvPassengers;
    private TextView tvReports;
    private DriverRideHistoryDTO ride;

    public static RideDetailsFragment newInstance(DriverRideHistoryDTO ride) {
        RideDetailsFragment fragment = new RideDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("ride", ride);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_details, container, false);

        mapView = view.findViewById(R.id.map);

        tvRoute = view.findViewById(R.id.tvRoute);
        tvStartedAt = view.findViewById(R.id.tvStartedAt);
        tvEndedAt = view.findViewById(R.id.tvEndedAt);
        tvPrice = view.findViewById(R.id.tvPrice);
        tvPanic = view.findViewById(R.id.tvPanic);
        tvCanceledBy = view.findViewById(R.id.tvCanceledBy);
        tvPassengers = view.findViewById(R.id.tvPassengers);
        tvReports = view.findViewById(R.id.tvReports);

        if (getArguments() != null) {
            ride = (DriverRideHistoryDTO) getArguments().getSerializable("ride");
        }

        if (ride != null) {
            mapSetup();
            showRideInfo();
        }

        return view;
    }

    private void showRideInfo() {
        String routeStr = ride.getStartLocation() + " -> " + ride.getEndLocation();
        tvRoute.setText(routeStr);
        tvStartedAt.setText(String.format("Started at: %s", ride.getStartedAt()));
        tvEndedAt.setText(String.format("Ended at: %s", ride.getEndedAt()));
        tvPrice.setText(String.format("Price: %s", ride.getPrice()));
        tvPanic.setText(String.format("PANIC: %s", ride.getPanicTriggered()));
        tvCanceledBy.setText(String.format("Canceled by: %s", ride.getCanceledBy()));
        List<String> passengers = ride.getPassengers();
        String passengersText = TextUtils.join("\n", passengers);
        tvPassengers.setText(passengersText);
        List<String> reports = ride.getReports();
        String reportsText = TextUtils.join("\n", reports);
        tvReports.setText(reportsText);
    }

    private void mapSetup() {
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        int lastStop = ride.getStops().size() - 1;

        GeoPoint centerPoint = new GeoPoint(
                (ride.getStops().get(0).getLatitude()
                        + ride.getStops().get(lastStop).getLatitude()) / 2,
                (ride.getStops().get(0).getLongitude()
                        + ride.getStops().get(lastStop).getLongitude()) / 2
        );
        MapController mapController = (MapController) mapView.getController();
        mapController.setZoom(16.0);
        mapController.setCenter(centerPoint);

        for (int i = 0; i < ride.getStops().size()-1; ++i) {
            fetchRoute(ride.getStops().get(i), ride.getStops().get(i+1));
        }

        for (GeoPointDTO stop : ride.getStops())
            drawMarkers(stop, R.drawable.location_icon);
    }

    private void drawMarkers(GeoPointDTO dto, int drawableRes) {
        Marker marker = new Marker(mapView);
        GeoPoint point = new GeoPoint(dto.getLatitude(), dto.getLongitude());
        marker.setPosition(point);
        marker.setTitle(dto.getLocation());
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        Drawable icon = ContextCompat.getDrawable(requireContext(), drawableRes);
        marker.setIcon(icon);
        mapView.getOverlays().add(marker);
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

        try (Response response = client.newCall(request).execute()) {
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

}