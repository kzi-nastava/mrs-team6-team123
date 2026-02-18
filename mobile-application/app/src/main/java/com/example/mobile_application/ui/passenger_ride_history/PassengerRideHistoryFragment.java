
package com.example.mobile_application.ui.passenger_ride_history;

import android.app.DatePickerDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.PassengerRideHistoryDTO;
import com.example.mobile_application.repository.PassengerRideHistoryRepository;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.TokenManager;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerRideHistoryFragment extends Fragment
        implements PassengerRideHistoryAdapter.OnRideClickListener, SensorEventListener {

    private RecyclerView recyclerView;
    private PassengerRideHistoryAdapter adapter;
    private TextInputEditText etFromDate, etToDate;
    private Button btnApply, btnClear;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private PassengerRideHistoryRepository repository;
    private TokenManager tokenManager;

    private String fromDate = null;
    private String toDate = null;
    private String currentSortBy = "date";
    private String currentSortOrder = "desc";

    // Shake detection
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastShakeTime = 0;
    private static final int SHAKE_THRESHOLD = 12;
    private static final int SHAKE_COOLDOWN_MS = 1000;

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_ride_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new PassengerRideHistoryRepository();
        tokenManager = ApiClient.getTokenManager();

        bindViews(view);
        setupRecyclerView();
        setupDatePickers();
        setupButtons();
        setupShakeSensor();

        loadRides();
    }

    private void bindViews(View v) {
        recyclerView = v.findViewById(R.id.rvPassengerRideHistory);
        etFromDate = v.findViewById(R.id.etFromDate);
        etToDate = v.findViewById(R.id.etToDate);
        btnApply = v.findViewById(R.id.btnApply);
        btnClear = v.findViewById(R.id.btnClear);
        progressBar = v.findViewById(R.id.progressBar);
        tvEmpty = v.findViewById(R.id.tvEmpty);
    }

    private void setupRecyclerView() {
        adapter = new PassengerRideHistoryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    // ── Date pickers ─────────────────────────────────────────────

    private void setupDatePickers() {
        etFromDate.setOnClickListener(v -> showDatePicker(true));
        etToDate.setOnClickListener(v -> showDatePicker(false));
    }

    private void showDatePicker(boolean isFrom) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            LocalDate date = LocalDate.of(year, month + 1, day);
            String formatted = date.format(DATE_FMT);
            if (isFrom) {
                fromDate = formatted;
                etFromDate.setText(formatted);
            } else {
                toDate = formatted;
                etToDate.setText(formatted);
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    // ── Buttons ──────────────────────────────────────────────────

    private void setupButtons() {
        btnApply.setOnClickListener(v -> loadRides());
        btnClear.setOnClickListener(v -> {
            fromDate = null;
            toDate = null;
            etFromDate.setText("");
            etToDate.setText("");
            loadRides();
        });
    }

    // ── Load data ────────────────────────────────────────────────

    private void loadRides() {
        Long userId = tokenManager.getUserId();
        if (userId == null) return;

        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        repository.getRideHistory(userId, fromDate, toDate,
                currentSortBy, currentSortOrder,
                new Callback<List<PassengerRideHistoryDTO>>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<List<PassengerRideHistoryDTO>> call,
                            @NonNull Response<List<PassengerRideHistoryDTO>> resp) {
                        if (!isAdded()) return;
                        progressBar.setVisibility(View.GONE);

                        if (resp.isSuccessful() && resp.body() != null) {
                            List<PassengerRideHistoryDTO> rides = resp.body();
                            adapter.setRides(rides);
                            tvEmpty.setVisibility(
                                    rides.isEmpty() ? View.VISIBLE : View.GONE);
                        } else {
                            showToast("Failed to load ride history");
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<List<PassengerRideHistoryDTO>> call,
                            @NonNull Throwable t) {
                        if (!isAdded()) return;
                        progressBar.setVisibility(View.GONE);
                        showToast("Network error");
                    }
                });
    }

    // ── Item click → details ─────────────────────────────────────

    @Override
    public void onViewDetails(PassengerRideHistoryDTO ride) {
        PassengerRideDetailFragment detail =
                PassengerRideDetailFragment.newInstance(ride);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, detail)
                .addToBackStack(null)
                .commit();
    }

    // ── Shake sensor for sorting ─────────────────────────────────

    private void setupShakeSensor() {
        sensorManager = (SensorManager) requireContext()
                .getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        double magnitude = Math.sqrt(x * x + y * y + z * z);

        if (magnitude > SHAKE_THRESHOLD) {
            long now = System.currentTimeMillis();
            if (now - lastShakeTime > SHAKE_COOLDOWN_MS) {
                lastShakeTime = now;
                toggleSortOrder();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void toggleSortOrder() {
        currentSortOrder = currentSortOrder.equals("desc") ? "asc" : "desc";
        String msg = currentSortOrder.equals("asc")
                ? "Sorted: oldest first" : "Sorted: newest first";
        showToast(msg);
        loadRides();
    }

    private void showToast(String msg) {
        if (isAdded()) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}
