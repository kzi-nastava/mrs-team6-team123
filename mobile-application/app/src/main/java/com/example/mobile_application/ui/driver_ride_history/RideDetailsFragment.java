package com.example.mobile_application.ui.driver_ride_history;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.example.mobile_application.dto.DriverRideHistoryDTO;
import com.example.mobile_application.dto.GeoPointDTO;
import com.example.mobile_application.helper.MapRouteHelper;

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
    private BitmapDrawable stopIcon;
    private MapRouteHelper mapRouteHelper;

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

        int newSize = 36;
        Bitmap originalBitmap = ((BitmapDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.location_icon)).getBitmap();
        Bitmap smallBitmap = Bitmap.createScaledBitmap(originalBitmap, newSize, newSize, true);
        stopIcon = new BitmapDrawable(getResources(), smallBitmap);

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
        mapRouteHelper = new MapRouteHelper(mapView);

        for (int i = 0; i < ride.getStops().size()-1; ++i) {
            mapRouteHelper.fetchRoute(ride.getStops().get(i), ride.getStops().get(i+1));
        }

        for (GeoPointDTO stop : ride.getStops())
            drawMarkers(stop);
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
}