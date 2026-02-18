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
import com.example.mobile_application.dto.GeocodingResult;
import com.example.mobile_application.dto.GeoPointDTO;
import com.example.mobile_application.dto.RideEstimationRequestDTO;
import com.example.mobile_application.dto.RideEstimationResponseDTO;
import com.example.mobile_application.enums.VehicleType;
import com.example.mobile_application.helper.DrawMarkerHelper;
import com.example.mobile_application.helper.MapRouteHelper;
import com.example.mobile_application.repository.RideEstimationRepository;
import com.example.mobile_application.service.GeocodingService;
import com.google.android.material.chip.ChipGroup;

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

    // ── Zamenjen NominatimRepository sa GeocodingService ────────
    private GeocodingService geocodingService;
    private RideEstimationRepository estimationRepo;

    // Koordinate kao double — direktno iz GeocodingResult
    private double startLat = 0, startLng = 0;
    private double destLat  = 0, destLng  = 0;
    private String acceptedStartText = "";
    private String acceptedDestText  = "";

    private VehicleType selectedVehicleType = VehicleType.STANDARD;
    private final List<StopEntry> stopEntries = new ArrayList<>();

    // Debounce handler
    private final Handler searchHandler = new Handler(Looper.getMainLooper());

    // Anti-double-submit flag
    private boolean isLoading = false;

    public RideEstimationFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ride_estimation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        geocodingService = GeocodingService.getInstance();
        estimationRepo   = new RideEstimationRepository();

        bindViews(view);
        setupMap();
        setupVehicleTypeChips();
        setupAutocomplete();
        setupListeners();
    }

    // ── View binding ─────────────────────────────────────────────

    private void bindViews(View v) {
        etStart              = v.findViewById(R.id.etStartAddress);
        etDestination        = v.findViewById(R.id.etDestinationAddress);
        chipGroupVehicleType = v.findViewById(R.id.chipGroupVehicleType);
        btnEstimate          = v.findViewById(R.id.btnEstimate);
        btnAddStop           = v.findViewById(R.id.btnAddStop);
        btnBack              = v.findViewById(R.id.btnBack);
        stopsContainer       = v.findViewById(R.id.stopsContainer);
        resultContainer      = v.findViewById(R.id.resultContainer);
        tvDistance           = v.findViewById(R.id.tvDistance);
        tvTime               = v.findViewById(R.id.tvTime);
        tvPrice              = v.findViewById(R.id.tvPrice);
        tvError              = v.findViewById(R.id.tvError);
        progressBar          = v.findViewById(R.id.progressBar);
        mapResult            = v.findViewById(R.id.mapResult);
    }

    // ── Map ───────────────────────────────────────────────────────

    private void setupMap() {
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        mapResult.setTileSource(TileSourceFactory.MAPNIK);
        mapResult.setMultiTouchControls(true);
        mapResult.getController().setZoom(14.0);
        mapResult.getController().setCenter(new GeoPoint(45.2576, 19.8442));
        mapRouteHelper  = new MapRouteHelper(mapResult);
        drawMarkerHelper = new DrawMarkerHelper(mapResult);

        Bitmap orig  = ((BitmapDrawable) ContextCompat.getDrawable(
                requireContext(), R.drawable.location_icon)).getBitmap();
        Bitmap small = Bitmap.createScaledBitmap(orig, 36, 36, true);
        stopIcon = new BitmapDrawable(getResources(), small);
    }

    // ── Vehicle type chips ────────────────────────────────────────

    private void setupVehicleTypeChips() {
        chipGroupVehicleType.setOnCheckedStateChangeListener((group, ids) -> {
            if (ids.isEmpty()) return;
            int id = ids.get(0);
            if      (id == R.id.chipStandard) selectedVehicleType = VehicleType.STANDARD;
            else if (id == R.id.chipLuxury)   selectedVehicleType = VehicleType.LUXURY;
            else if (id == R.id.chipVan)      selectedVehicleType = VehicleType.VAN;
        });
    }

    // ── Autocomplete ──────────────────────────────────────────────

    private void setupAutocomplete() {
        etStart.setThreshold(3);
        etDestination.setThreshold(3);

        etStart.addTextChangedListener(new DebouncedTextWatcher() {
            @Override void onDebouncedTextChanged(String text) {
                // Ako tekst odgovara poslednjem odabranom, koordinate su već sačuvane
                if (text.equals(acceptedStartText) && startLat != 0) return;
                clearStart();
                if (text.length() >= 3) fetchSuggestions(text, true);
            }
        });

        etDestination.addTextChangedListener(new DebouncedTextWatcher() {
            @Override void onDebouncedTextChanged(String text) {
                if (text.equals(acceptedDestText) && destLat != 0) return;
                clearDest();
                if (text.length() >= 3) fetchSuggestions(text, false);
            }
        });

        etStart.setOnItemClickListener((parent, v, pos, id) -> {
            GeocodingResult r = (GeocodingResult) parent.getItemAtPosition(pos);
            if (r == null) return;
            startLat = r.getLatitudeDouble();
            startLng = r.getLongitudeDouble();
            acceptedStartText = r.getDisplayName();
            etStart.setText(acceptedStartText, false);
            etStart.dismissDropDown();
        });

        etDestination.setOnItemClickListener((parent, v, pos, id) -> {
            GeocodingResult r = (GeocodingResult) parent.getItemAtPosition(pos);
            if (r == null) return;
            destLat = r.getLatitudeDouble();
            destLng = r.getLongitudeDouble();
            acceptedDestText = r.getDisplayName();
            etDestination.setText(acceptedDestText, false);
            etDestination.dismissDropDown();
        });
    }

    private void fetchSuggestions(String query, boolean isStart) {
        geocodingService.searchAddress(query, new Callback<List<GeocodingResult>>() {
            @Override
            public void onResponse(@NonNull Call<List<GeocodingResult>> call,
                                   @NonNull Response<List<GeocodingResult>> resp) {
                if (!isAdded() || resp.body() == null) return;

                List<GeocodingResult> results = resp.body();
                // GeocodingResult.toString() poziva getDisplayName() — formatiran string
                ArrayAdapter<GeocodingResult> adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        results);

                if (isStart) {
                    etStart.setAdapter(adapter);
                    etStart.showDropDown();
                } else {
                    etDestination.setAdapter(adapter);
                    etDestination.showDropDown();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GeocodingResult>> call,
                                  @NonNull Throwable t) { /* tiho ignorisi */ }
        });
    }

    // ── Intermediate stops ────────────────────────────────────────

    private void addStopView() {
        View stopView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_intermediate_stop, stopsContainer, false);

        AutoCompleteTextView etStop = stopView.findViewById(R.id.etStopAddress);
        Button btnRemove            = stopView.findViewById(R.id.btnRemoveStop);

        StopEntry entry = new StopEntry();
        entry.view     = stopView;
        entry.editText = etStop;

        etStop.setThreshold(3);
        etStop.addTextChangedListener(new DebouncedTextWatcher() {
            @Override void onDebouncedTextChanged(String text) {
                entry.lat = 0; entry.lng = 0;
                if (text.length() >= 3) fetchStopSuggestions(text, entry);
            }
        });

        etStop.setOnItemClickListener((parent, v, pos, id) -> {
            GeocodingResult r = (GeocodingResult) parent.getItemAtPosition(pos);
            if (r == null) return;
            entry.lat = r.getLatitudeDouble();
            entry.lng = r.getLongitudeDouble();
            etStop.setText(r.getDisplayName(), false);
            etStop.dismissDropDown();
        });

        btnRemove.setOnClickListener(v -> {
            stopsContainer.removeView(stopView);
            stopEntries.remove(entry);
        });

        stopEntries.add(entry);
        stopsContainer.addView(stopView);
    }

    private void fetchStopSuggestions(String query, StopEntry entry) {
        geocodingService.searchAddress(query, new Callback<List<GeocodingResult>>() {
            @Override
            public void onResponse(@NonNull Call<List<GeocodingResult>> call,
                                   @NonNull Response<List<GeocodingResult>> resp) {
                if (!isAdded() || resp.body() == null) return;
                ArrayAdapter<GeocodingResult> adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        resp.body());
                entry.editText.setAdapter(adapter);
                entry.editText.showDropDown();
            }
            @Override
            public void onFailure(@NonNull Call<List<GeocodingResult>> call,
                                  @NonNull Throwable t) {}
        });
    }

    // ── Listeners ─────────────────────────────────────────────────

    private void setupListeners() {
        btnAddStop.setOnClickListener(v -> addStopView());
        btnEstimate.setOnClickListener(v -> performEstimation());
        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
    }

    // ── Estimation ────────────────────────────────────────────────

    private void performEstimation() {
        // Fix: sprečava dupli submit ako korisnik klikne dva puta brzo
        if (isLoading) return;

        tvError.setVisibility(View.GONE);
        resultContainer.setVisibility(View.GONE);

        String startText = etStart.getText() != null
                ? etStart.getText().toString().trim() : "";
        String destText  = etDestination.getText() != null
                ? etDestination.getText().toString().trim() : "";

        if (startText.isEmpty()) { showError("Please enter starting location"); return; }
        if (destText.isEmpty())  { showError("Please enter destination"); return; }

        setLoading(true);

        // Geocode samo ako koordinate nisu već sačuvane (korisnik nije kliknuo dropdown)
        if (startLat == 0) {
            geocodeAndContinue(startText, true);
        } else if (destLat == 0) {
            geocodeAndContinue(destText, false);
        } else {
            geocodeStopsAndSend();
        }
    }

    private void geocodeAndContinue(String address, boolean isStart) {
        geocodingService.geocodeAddress(address, new Callback<List<GeocodingResult>>() {
            @Override
            public void onResponse(@NonNull Call<List<GeocodingResult>> call,
                                   @NonNull Response<List<GeocodingResult>> resp) {
                if (!isAdded()) return;
                if (resp.body() != null && !resp.body().isEmpty()) {
                    GeocodingResult r = resp.body().get(0);
                    if (isStart) {
                        startLat = r.getLatitudeDouble();
                        startLng = r.getLongitudeDouble();
                        // Ako i dest treba geocodirati, nastavi lanac
                        if (destLat == 0) {
                            String destText = etDestination.getText() != null
                                    ? etDestination.getText().toString().trim() : "";
                            geocodeAndContinue(destText, false);
                        } else {
                            geocodeStopsAndSend();
                        }
                    } else {
                        destLat = r.getLatitudeDouble();
                        destLng = r.getLongitudeDouble();
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
            public void onFailure(@NonNull Call<List<GeocodingResult>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                setLoading(false);
                showError("Geocoding failed. Please try again.");
            }
        });
    }

    private void geocodeStopsAndSend() {
        geocodeNextStop(0, new ArrayList<>());
    }

    private void geocodeNextStop(int idx, List<String> resolved) {
        // Preskoči prazne/već rešene stopove
        while (idx < stopEntries.size()) {
            StopEntry e = stopEntries.get(idx);
            String text = e.editText.getText() != null
                    ? e.editText.getText().toString().trim() : "";
            if (text.isEmpty()) { idx++; continue; }
            if (e.lat != 0) {
                resolved.add(buildCoordString(e.lat, e.lng));
                idx++;
                continue;
            }
            // Treba geocodirati ovaj stop
            final int next = idx;
            geocodingService.geocodeAddress(text, new Callback<List<GeocodingResult>>() {
                @Override
                public void onResponse(@NonNull Call<List<GeocodingResult>> call,
                                       @NonNull Response<List<GeocodingResult>> resp) {
                    if (!isAdded()) return;
                    if (resp.body() != null && !resp.body().isEmpty()) {
                        GeocodingResult r = resp.body().get(0);
                        stopEntries.get(next).lat = r.getLatitudeDouble();
                        stopEntries.get(next).lng = r.getLongitudeDouble();
                        resolved.add(buildCoordString(r.getLatitudeDouble(),
                                r.getLongitudeDouble()));
                    }
                    geocodeNextStop(next + 1, resolved);
                }
                @Override
                public void onFailure(@NonNull Call<List<GeocodingResult>> call,
                                      @NonNull Throwable t) {
                    if (!isAdded()) return;
                    geocodeNextStop(next + 1, resolved);
                }
            });
            return;
        }
        sendEstimationRequest(resolved);
    }

    private void sendEstimationRequest(List<String> intermediateStops) {
        RideEstimationRequestDTO req = new RideEstimationRequestDTO(
                buildCoordString(startLat, startLng),
                buildCoordString(destLat, destLng),
                intermediateStops.isEmpty() ? null : intermediateStops,
                selectedVehicleType);

        estimationRepo.estimateRide(req, new Callback<RideEstimationResponseDTO>() {
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

    // ── UI helpers ────────────────────────────────────────────────

    private void showResult(RideEstimationResponseDTO resp) {
        resultContainer.setVisibility(View.VISIBLE);
        tvDistance.setText(String.format("%.2f km",   resp.getEstimatedDistance()));
        tvTime.setText(String.format("%d min",         resp.getEstimatedTime()));
        tvPrice.setText(String.format("%.2f RSD",      resp.getEstimatedPrice()));
        drawRoute();
    }

    private void drawRoute() {
        mapResult.getOverlays().clear();

        List<GeoPointDTO> pts = new ArrayList<>();
        pts.add(makePoint(startLat, startLng));
        for (StopEntry e : stopEntries) {
            if (e.lat != 0) pts.add(makePoint(e.lat, e.lng));
        }
        pts.add(makePoint(destLat, destLng));

        for (int i = 0; i < pts.size() - 1; i++)
            mapRouteHelper.fetchRoute(pts.get(i), pts.get(i + 1));
        for (GeoPointDTO p : pts)
            drawMarkerHelper.drawMarkers(p, stopIcon);

        zoomToFit(pts);
    }

    private void zoomToFit(List<GeoPointDTO> pts) {
        if (pts.isEmpty()) return;
        double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
        double minLng = Double.MAX_VALUE, maxLng = -Double.MAX_VALUE;
        for (GeoPointDTO p : pts) {
            minLat = Math.min(minLat, p.getLatitude());
            maxLat = Math.max(maxLat, p.getLatitude());
            minLng = Math.min(minLng, p.getLongitude());
            maxLng = Math.max(maxLng, p.getLongitude());
        }
        double pad = 0.01;
        BoundingBox box = new BoundingBox(maxLat + pad, maxLng + pad,
                minLat - pad, minLng - pad);
        mapResult.post(() -> mapResult.zoomToBoundingBox(box, true));
    }

    private void showError(String msg) {
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(msg);
    }

    private void setLoading(boolean loading) {
        isLoading = loading;
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnEstimate.setEnabled(!loading);
    }

    private void clearStart() { startLat = 0; startLng = 0; acceptedStartText = ""; }
    private void clearDest()  { destLat  = 0; destLng  = 0; acceptedDestText  = ""; }

    /** "lat, lng" format koji backend očekuje — Locale.US da bi tačka bila decimalni separator */
    private String buildCoordString(double lat, double lng) {
        return String.format(java.util.Locale.US, "%.6f, %.6f", lat, lng);
    }

    private GeoPointDTO makePoint(double lat, double lng) {
        GeoPointDTO p = new GeoPointDTO();
        p.setLatitude(lat);
        p.setLongitude(lng);
        return p;
    }

    // ── Inner classes ─────────────────────────────────────────────

    private static class StopEntry {
        View view;
        AutoCompleteTextView editText;
        double lat = 0, lng = 0;  // double umesto String koordinata
    }

    private abstract class DebouncedTextWatcher implements TextWatcher {
        private final Handler h = new Handler(Looper.getMainLooper());
        private Runnable r;
        abstract void onDebouncedTextChanged(String text);

        @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
        @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
        @Override public void afterTextChanged(Editable s) {
            if (r != null) h.removeCallbacks(r);
            r = () -> onDebouncedTextChanged(s.toString());
            h.postDelayed(r, 350);
        }
    }
}