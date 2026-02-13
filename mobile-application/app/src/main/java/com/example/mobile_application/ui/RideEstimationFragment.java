package com.example.mobile_application.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import androidx.core.content.ContextCompat;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.GeoPointDTO;
import com.example.mobile_application.dto.NominatimResultDTO;
import com.example.mobile_application.dto.RideEstimationRequestDTO;
import com.example.mobile_application.dto.RideEstimationResponseDTO;
import com.example.mobile_application.enums.VehicleType;
import com.example.mobile_application.helper.DrawMarkerHelper;
import com.example.mobile_application.helper.MapRouteHelper;
import com.example.mobile_application.repository.NominatimRepository;
import com.example.mobile_application.repository.RideEstimationRepository;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideEstimationFragment extends Fragment {

    private AutoCompleteTextView etStart, etDestination;
    private ChipGroup chipGroupVehicleType;
    private Button btnEstimate, btnAddStop, btnBack;
    private LinearLayout stopsContainer;
    private View resultContainer;
    private TextView tvDistance, tvTime, tvPrice, tvError;
    private ProgressBar progressBar;
    private MapView mapResult;
    private MapRouteHelper mapRouteHelper;
    private DrawMarkerHelper drawMarkerHelper;
    private BitmapDrawable stopIcon;

    private NominatimRepository nominatimRepo;
    private RideEstimationRepository estimationRepo;

    private String startCoords = "";
    private String destCoords = "";
    private String acceptedStartText = "";
    private String acceptedDestText = "";
    private VehicleType selectedVehicleType = VehicleType.STANDARD;

    private List<NominatimResultDTO> startSuggestions = new ArrayList<>();
    private List<NominatimResultDTO> destSuggestions = new ArrayList<>();

    // intermediate stops
    private final List<StopEntry> stopEntries = new ArrayList<>();
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable startSearchRunnable, destSearchRunnable;

    public RideEstimationFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ride_estimation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nominatimRepo = new NominatimRepository();
        estimationRepo = new RideEstimationRepository();

        bindViews(view);
        setupMap();
        setupVehicleTypeChips();
        setupAutocomplete();
        setupListeners();
    }

    private void bindViews(View v) {
        etStart = v.findViewById(R.id.etStartAddress);
        etDestination = v.findViewById(R.id.etDestinationAddress);
        chipGroupVehicleType = v.findViewById(R.id.chipGroupVehicleType);
        btnEstimate = v.findViewById(R.id.btnEstimate);
        btnAddStop = v.findViewById(R.id.btnAddStop);
        btnBack = v.findViewById(R.id.btnBack);
        stopsContainer = v.findViewById(R.id.stopsContainer);
        resultContainer = v.findViewById(R.id.resultContainer);
        tvDistance = v.findViewById(R.id.tvDistance);
        tvTime = v.findViewById(R.id.tvTime);
        tvPrice = v.findViewById(R.id.tvPrice);
        tvError = v.findViewById(R.id.tvError);
        progressBar = v.findViewById(R.id.progressBar);
        mapResult = v.findViewById(R.id.mapResult);
    }

    private void setupMap() {
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        mapResult.setTileSource(TileSourceFactory.MAPNIK);
        mapResult.setMultiTouchControls(true);
        mapResult.getController().setZoom(14.0);
        mapResult.getController().setCenter(new GeoPoint(45.2576, 19.8442));
        mapRouteHelper = new MapRouteHelper(mapResult);
        drawMarkerHelper = new DrawMarkerHelper(mapResult);

        Bitmap original = ((BitmapDrawable) ContextCompat.getDrawable(
                requireContext(), R.drawable.location_icon)).getBitmap();
        Bitmap small = Bitmap.createScaledBitmap(original, 36, 36, true);
        stopIcon = new BitmapDrawable(getResources(), small);
    }

    // ── Vehicle type chips ───────────────────────────────────────

    private void setupVehicleTypeChips() {
        chipGroupVehicleType.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipStandard) selectedVehicleType = VehicleType.STANDARD;
            else if (id == R.id.chipLuxury) selectedVehicleType = VehicleType.LUXURY;
            else if (id == R.id.chipVan) selectedVehicleType = VehicleType.VAN;
        });
    }

    // ── Autocomplete for start & destination ─────────────────────

    private void setupAutocomplete() {
        etStart.setThreshold(3);
        etDestination.setThreshold(3);

        etStart.addTextChangedListener(new DebouncedTextWatcher() {
            @Override
            void onDebouncedTextChanged(String text) {
                // If text matches what was selected from dropdown, skip
                if (text.equals(acceptedStartText) && !startCoords.isEmpty()) return;
                startCoords = "";
                acceptedStartText = "";
                if (text.length() >= 3) fetchSuggestions(text, true);
            }
        });

        etDestination.addTextChangedListener(new DebouncedTextWatcher() {
            @Override
            void onDebouncedTextChanged(String text) {
                if (text.equals(acceptedDestText) && !destCoords.isEmpty()) return;
                destCoords = "";
                acceptedDestText = "";
                if (text.length() >= 3) fetchSuggestions(text, false);
            }
        });

        etStart.setOnItemClickListener((parent, view, pos, id) -> {
            if (pos < startSuggestions.size()) {
                NominatimResultDTO r = startSuggestions.get(pos);
                startCoords = r.toCoordinateString();
                acceptedStartText = shortenAddress(r.getDisplayName());
                etStart.setText(acceptedStartText, false);
                etStart.dismissDropDown();
            }
        });

        etDestination.setOnItemClickListener((parent, view, pos, id) -> {
            if (pos < destSuggestions.size()) {
                NominatimResultDTO r = destSuggestions.get(pos);
                destCoords = r.toCoordinateString();
                acceptedDestText = shortenAddress(r.getDisplayName());
                etDestination.setText(acceptedDestText, false);
                etDestination.dismissDropDown();
            }
        });
    }

    private void fetchSuggestions(String query, boolean isStart) {
        nominatimRepo.searchSuggestions(query, new Callback<List<NominatimResultDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<NominatimResultDTO>> call,
                                   @NonNull Response<List<NominatimResultDTO>> resp) {
                if (!isAdded() || resp.body() == null) return;

                List<NominatimResultDTO> results = resp.body();
                List<String> names = new ArrayList<>();
                for (NominatimResultDTO r : results) {
                    names.add(shortenAddress(r.getDisplayName()));
                }

                if (isStart) {
                    startSuggestions = results;
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line, names);
                    etStart.setAdapter(adapter);
                    etStart.showDropDown();
                } else {
                    destSuggestions = results;
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line, names);
                    etDestination.setAdapter(adapter);
                    etDestination.showDropDown();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<NominatimResultDTO>> call,
                                  @NonNull Throwable t) {
                // silently ignore
            }
        });
    }

    // ── Intermediate stops ───────────────────────────────────────

    private void addStopView() {
        View stopView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_intermediate_stop, stopsContainer, false);

        AutoCompleteTextView etStop = stopView.findViewById(R.id.etStopAddress);
        Button btnRemove = stopView.findViewById(R.id.btnRemoveStop);

        StopEntry entry = new StopEntry();
        entry.view = stopView;
        entry.editText = etStop;

        etStop.setThreshold(3);
        etStop.addTextChangedListener(new DebouncedTextWatcher() {
            @Override
            void onDebouncedTextChanged(String text) {
                entry.coordinates = "";
                if (text.length() >= 3) fetchStopSuggestions(text, entry);
            }
        });

        etStop.setOnItemClickListener((parent, view, pos, id) -> {
            if (pos < entry.suggestions.size()) {
                NominatimResultDTO r = entry.suggestions.get(pos);
                entry.coordinates = r.toCoordinateString();
                etStop.setText(shortenAddress(r.getDisplayName()));
                etStop.dismissDropDown();
            }
        });

        btnRemove.setOnClickListener(v -> {
            stopsContainer.removeView(stopView);
            stopEntries.remove(entry);
        });

        stopEntries.add(entry);
        stopsContainer.addView(stopView);
    }

    private void fetchStopSuggestions(String query, StopEntry entry) {
        nominatimRepo.searchSuggestions(query, new Callback<List<NominatimResultDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<NominatimResultDTO>> call,
                                   @NonNull Response<List<NominatimResultDTO>> resp) {
                if (!isAdded() || resp.body() == null) return;

                entry.suggestions = resp.body();
                List<String> names = new ArrayList<>();
                for (NominatimResultDTO r : entry.suggestions) {
                    names.add(shortenAddress(r.getDisplayName()));
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line, names);
                entry.editText.setAdapter(adapter);
                entry.editText.showDropDown();
            }

            @Override
            public void onFailure(@NonNull Call<List<NominatimResultDTO>> call,
                                  @NonNull Throwable t) {}
        });
    }

    // ── Button listeners ─────────────────────────────────────────

    private void setupListeners() {
        btnAddStop.setOnClickListener(v -> addStopView());
        btnEstimate.setOnClickListener(v -> performEstimation());
        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
    }

    // ── Estimation logic ─────────────────────────────────────────

    private void performEstimation() {
        tvError.setVisibility(View.GONE);
        resultContainer.setVisibility(View.GONE);

        String startText = etStart.getText() != null
                ? etStart.getText().toString().trim() : "";
        String destText = etDestination.getText() != null
                ? etDestination.getText().toString().trim() : "";

        if (startText.isEmpty()) {
            showError("Please enter starting location");
            return;
        }
        if (destText.isEmpty()) {
            showError("Please enter destination");
            return;
        }

        setLoading(true);

        // If coordinates not resolved yet, geocode first
        if (startCoords.isEmpty()) {
            geocodeAndContinue(startText, true);
        } else if (destCoords.isEmpty()) {
            geocodeAndContinue(destText, false);
        } else {
            geocodeStopsAndSend();
        }
    }

    private void geocodeAndContinue(String address, boolean isStart) {
        nominatimRepo.geocode(address, new Callback<List<NominatimResultDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<NominatimResultDTO>> call,
                                   @NonNull Response<List<NominatimResultDTO>> resp) {
                if (!isAdded()) return;
                if (resp.body() != null && !resp.body().isEmpty()) {
                    NominatimResultDTO r = resp.body().get(0);
                    if (isStart) {
                        startCoords = r.toCoordinateString();
                        // Now check destination
                        if (destCoords.isEmpty()) {
                            String destText = etDestination.getText() != null
                                    ? etDestination.getText().toString().trim() : "";
                            geocodeAndContinue(destText, false);
                        } else {
                            geocodeStopsAndSend();
                        }
                    } else {
                        destCoords = r.toCoordinateString();
                        geocodeStopsAndSend();
                    }
                } else {
                    setLoading(false);
                    showError(isStart
                            ? "Could not find starting location"
                            : "Could not find destination");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<NominatimResultDTO>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                setLoading(false);
                showError("Geocoding failed. Please try again.");
            }
        });
    }

    private void geocodeStopsAndSend() {
        List<String> resolvedStops = new ArrayList<>();
        geocodeNextStop(0, resolvedStops);
    }

    private void geocodeNextStop(int index, List<String> resolvedStops) {
        // skip stops without text
        while (index < stopEntries.size()) {
            StopEntry entry = stopEntries.get(index);
            String text = entry.editText.getText() != null
                    ? entry.editText.getText().toString().trim() : "";
            if (text.isEmpty()) {
                index++;
                continue;
            }
            if (!entry.coordinates.isEmpty()) {
                resolvedStops.add(entry.coordinates);
                index++;
                continue;
            }
            // need to geocode this stop
            int nextIdx = index;
            nominatimRepo.geocode(text, new Callback<List<NominatimResultDTO>>() {
                @Override
                public void onResponse(@NonNull Call<List<NominatimResultDTO>> call,
                                       @NonNull Response<List<NominatimResultDTO>> resp) {
                    if (!isAdded()) return;
                    if (resp.body() != null && !resp.body().isEmpty()) {
                        String coords = resp.body().get(0).toCoordinateString();
                        stopEntries.get(nextIdx).coordinates = coords;
                        resolvedStops.add(coords);
                    }
                    geocodeNextStop(nextIdx + 1, resolvedStops);
                }

                @Override
                public void onFailure(@NonNull Call<List<NominatimResultDTO>> call,
                                      @NonNull Throwable t) {
                    if (!isAdded()) return;
                    geocodeNextStop(nextIdx + 1, resolvedStops);
                }
            });
            return; // wait for callback
        }

        // all stops resolved — send request
        sendEstimationRequest(resolvedStops);
    }

    private void sendEstimationRequest(List<String> intermediateStops) {
        RideEstimationRequestDTO request = new RideEstimationRequestDTO(
                startCoords,
                destCoords,
                intermediateStops.isEmpty() ? null : intermediateStops,
                selectedVehicleType.name()
        );

        estimationRepo.estimateRide(request, new Callback<RideEstimationResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<RideEstimationResponseDTO> call,
                                   @NonNull Response<RideEstimationResponseDTO> resp) {
                if (!isAdded()) return;
                setLoading(false);

                if (resp.isSuccessful() && resp.body() != null) {
                    showResult(resp.body());
                } else {
                    showError("Failed to calculate estimate");
                }
            }

            @Override
            public void onFailure(@NonNull Call<RideEstimationResponseDTO> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                setLoading(false);
                showError("Network error. Please try again.");
            }
        });
    }

    // ── UI helpers ───────────────────────────────────────────────

    private void showResult(RideEstimationResponseDTO resp) {
        resultContainer.setVisibility(View.VISIBLE);
        tvDistance.setText(String.format("%.2f km", resp.getEstimatedDistance()));
        tvTime.setText(String.format("%d min", resp.getEstimatedTime()));
        tvPrice.setText(String.format("%.2f RSD", resp.getEstimatedPrice()));

        drawRoute();
    }

    private void drawRoute() {
        mapResult.getOverlays().clear();

        // Build list of all points: start -> stops -> destination
        List<GeoPointDTO> allPoints = new ArrayList<>();
        allPoints.add(parseToGeoPointDTO(startCoords));

        for (StopEntry entry : stopEntries) {
            if (!entry.coordinates.isEmpty()) {
                allPoints.add(parseToGeoPointDTO(entry.coordinates));
            }
        }

        allPoints.add(parseToGeoPointDTO(destCoords));

        // Draw route segments
        for (int i = 0; i < allPoints.size() - 1; i++) {
            mapRouteHelper.fetchRoute(allPoints.get(i), allPoints.get(i + 1));
        }

        // Draw markers
        for (GeoPointDTO point : allPoints) {
            drawMarkerHelper.drawMarkers(point, stopIcon);
        }

        // Zoom to fit all points
        zoomToFitPoints(allPoints);
    }

    private GeoPointDTO parseToGeoPointDTO(String coords) {
        String[] parts = coords.split(",");
        double lat = Double.parseDouble(parts[0].trim());
        double lon = Double.parseDouble(parts[1].trim());
        GeoPointDTO dto = new GeoPointDTO();
        dto.setLatitude(lat);
        dto.setLongitude(lon);
        return dto;
    }

    private void zoomToFitPoints(List<GeoPointDTO> points) {
        if (points.isEmpty()) return;

        double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;

        for (GeoPointDTO p : points) {
            minLat = Math.min(minLat, p.getLatitude());
            maxLat = Math.max(maxLat, p.getLatitude());
            minLon = Math.min(minLon, p.getLongitude());
            maxLon = Math.max(maxLon, p.getLongitude());
        }

        double padding = 0.01;
        BoundingBox box = new BoundingBox(
                maxLat + padding, maxLon + padding,
                minLat - padding, minLon - padding);

        mapResult.post(() -> mapResult.zoomToBoundingBox(box, true));
    }

    private void showError(String msg) {
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(msg);
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnEstimate.setEnabled(!loading);
    }

    private String shortenAddress(String address) {
        if (address == null) return "";
        return address.length() <= 60 ? address : address.substring(0, 60) + "...";
    }

    // ── Inner helper class for stops ─────────────────────────────

    private static class StopEntry {
        View view;
        AutoCompleteTextView editText;
        String coordinates = "";
        List<NominatimResultDTO> suggestions = new ArrayList<>();
    }

    // ── Debounced TextWatcher ────────────────────────────────────

    private abstract class DebouncedTextWatcher implements TextWatcher {
        private final Handler handler = new Handler(Looper.getMainLooper());
        private Runnable runnable;

        abstract void onDebouncedTextChanged(String text);

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (runnable != null) handler.removeCallbacks(runnable);
            runnable = () -> onDebouncedTextChanged(s.toString());
            handler.postDelayed(runnable, 350);
        }
    }
}