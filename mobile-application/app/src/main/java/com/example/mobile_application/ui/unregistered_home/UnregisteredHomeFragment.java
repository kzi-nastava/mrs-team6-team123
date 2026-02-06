package com.example.mobile_application.ui.unregistered_home;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.ActiveVehicleDTO;
import com.example.mobile_application.repository.ActiveVehicleRepository;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnregisteredHomeFragment extends Fragment {
    private MapView mapView;
    private Map<Long, Marker> vehicleMarkers = new HashMap<>();
    private ActiveVehicleRepository repository;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable refreshRunnable;
    private BitmapDrawable taxiIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unregistered_home, container, false);

        mapView = view.findViewById(R.id.map);
        repository = new ActiveVehicleRepository();
        Bitmap originalBitmap = ((BitmapDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.taxi)).getBitmap();
        int newSize = 36;
        Bitmap smallBitmap = Bitmap.createScaledBitmap(originalBitmap, newSize, newSize, true);
        taxiIcon = new BitmapDrawable(getResources(), smallBitmap);
        mapSetup();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView.post(() -> {
            loadActiveVehicles();
            startAutoRefresh();
        });
    }

    private void loadActiveVehicles() {
        repository.getActiveVehicles(new Callback<List<ActiveVehicleDTO>>() {
            @Override
            public void onResponse(
                    @NonNull Call<List<ActiveVehicleDTO>> call,
                    @NonNull Response<List<ActiveVehicleDTO>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    updateMarkers(response.body());
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<List<ActiveVehicleDTO>> call,
                    @NonNull Throwable t) {
                if (isAdded()) {
                    showToast("Failed loading active vehicles");
                }
            }
        });
    }

    private void startAutoRefresh() {
        if (refreshRunnable != null) return;
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadActiveVehicles();
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

    private void updateMarkers(List<ActiveVehicleDTO> vehicles) {
        for (ActiveVehicleDTO vehicle : vehicles) {

            Marker marker = vehicleMarkers.get(vehicle.getVehicleId());
            GeoPoint point = new GeoPoint(vehicle.getLatitude(), vehicle.getLongitude());

            if (marker == null) {
                marker = new Marker(mapView);
                marker.setPosition(point);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                marker.setTitle(vehicle.isAvailable() ? "Available" : "Busy");
                

                marker.setIcon(taxiIcon);

                mapView.getOverlays().add(marker);
                vehicleMarkers.put(vehicle.getVehicleId(), marker);
            } else {
                marker.setPosition(point);
                marker.setTitle(vehicle.isAvailable() ? "Available" : "Busy");
            }
        }

        mapView.invalidate();
    }

    private void showToast(String message) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
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