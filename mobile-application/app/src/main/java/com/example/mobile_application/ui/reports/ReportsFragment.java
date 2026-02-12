package com.example.mobile_application.ui.reports;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.RideDataPointDTO;
import com.example.mobile_application.dto.StatisticsDTO;
import com.example.mobile_application.dto.UserBasicInfoDTO;
import com.example.mobile_application.repository.ReportsRepository;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.TokenManager;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportsFragment extends Fragment {

    private TextInputEditText etDateFrom;
    private TextInputEditText etDateTo;
    private Button btnApply;
    private Button btnClear;
    private Spinner spinnerUser;
    private ProgressBar progressBar;
    private TextView tvTotalRides;
    private TextView tvAvgRidesPerDay;
    private TextView tvTotalKm;
    private TextView tvAvgKmPerDay;
    private TextView tvTotalAmount;
    private TextView tvAvgAmountPerDay;
    private TextView tvNoData;
    private LinearLayout chartContainerRides;
    private LinearLayout chartContainerKm;
    private LinearLayout chartContainerAmount;

    private ReportsRepository repository;
    private List<UserBasicInfoDTO> userList = new ArrayList<>();
    private LocalDate fromDate = null;
    private LocalDate toDate = null;
    private UserBasicInfoDTO selectedUser = null;
    private TokenManager tokenManager;
    private Long currentUserId;
    private String currentUserRole;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        etDateFrom = view.findViewById(R.id.etReportsDateFrom);
        etDateTo = view.findViewById(R.id.etReportsDateTo);
        btnApply = view.findViewById(R.id.btnReportsApply);
        btnClear = view.findViewById(R.id.btnReportsClear);
        spinnerUser = view.findViewById(R.id.spinnerReportsUser);
        progressBar = view.findViewById(R.id.progressBarReports);
        tvTotalRides = view.findViewById(R.id.tvTotalRides);
        tvAvgRidesPerDay = view.findViewById(R.id.tvAvgRidesPerDay);
        tvTotalKm = view.findViewById(R.id.tvTotalKm);
        tvAvgKmPerDay = view.findViewById(R.id.tvAvgKmPerDay);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        tvAvgAmountPerDay = view.findViewById(R.id.tvAvgAmountPerDay);
        tvNoData = view.findViewById(R.id.tvReportsNoData);
        chartContainerRides = view.findViewById(R.id.chartContainerRides);
        chartContainerKm = view.findViewById(R.id.chartContainerKm);
        chartContainerAmount = view.findViewById(R.id.chartContainerAmount);

        repository = new ReportsRepository();
        tokenManager = ApiClient.getTokenManager();
        currentUserId = tokenManager.getUserId();
        currentUserRole = tokenManager.getRole();

        setupDatePickers();
        setupUserSpinner();
        setupButtons();
        loadInitialStatistics();

        return view;
    }

    private void setupDatePickers() {
        etDateFrom.setOnClickListener(v -> showDatePickerDialog((date) -> {
            fromDate = date;
            etDateFrom.setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }));

        etDateTo.setOnClickListener(v -> showDatePickerDialog((date) -> {
            toDate = date;
            etDateTo.setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }));
    }

    private void showDatePickerDialog(DateSelectedListener listener) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    LocalDate selected = LocalDate.of(year, month + 1, dayOfMonth);
                    listener.onDateSelected(selected);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void setupUserSpinner() {
        if ("ADMIN".equalsIgnoreCase(currentUserRole)) {
            spinnerUser.setVisibility(View.VISIBLE);
            loadUsers();
        } else {
            spinnerUser.setVisibility(View.GONE);
        }
    }

    private void loadUsers() {
        repository.getAllActiveUsers(currentUserId, new Callback<List<UserBasicInfoDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserBasicInfoDTO>> call,
                                   @NonNull Response<List<UserBasicInfoDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList = response.body();
                    setupUserAdapter();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserBasicInfoDTO>> call, @NonNull Throwable t) {
            }
        });
    }

    private void setupUserAdapter() {
        List<String> userDisplayList = new ArrayList<>();
        userDisplayList.add("All Rides");
        for (UserBasicInfoDTO user : userList) {
            userDisplayList.add(user.getFirstName() + " " + user.getLastName() + 
                               " (" + user.getUserRole() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                userDisplayList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUser.setAdapter(adapter);

        spinnerUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedUser = null;
                } else {
                    selectedUser = userList.get(position - 1);
                }
                loadStatistics();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupButtons() {
        btnApply.setOnClickListener(v -> {
            loadStatistics();
        });

        btnClear.setOnClickListener(v -> {
            fromDate = null;
            toDate = null;
            etDateFrom.setText("");
            etDateTo.setText("");
            spinnerUser.setSelection(0);
            loadStatistics();
        });
    }

    private void loadInitialStatistics() {
        toDate = LocalDate.now();
        fromDate = toDate.minusMonths(12);
        
        etDateFrom.setText(fromDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        etDateTo.setText(toDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        loadStatistics();
    }

    private void loadStatistics() {
        if (currentUserId == null || currentUserId < 0 || currentUserRole == null) {
            tvNoData.setVisibility(View.VISIBLE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);

        Long userId = selectedUser != null ? selectedUser.getId() : currentUserId;
        String userType = selectedUser != null ? selectedUser.getUserRole() : currentUserRole;

        Long filteredUserId = null;
        String filteredUserType = null;

        if ("ADMIN".equalsIgnoreCase(currentUserRole) && selectedUser != null) {
            filteredUserId = selectedUser.getId();
            filteredUserType = selectedUser.getUserRole();
            userId = currentUserId;
            userType = "ADMIN";
        }

        repository.getStatistics(
                userId,
                userType,
                filteredUserId,
                filteredUserType,
                fromDate,
                toDate,
                new Callback<StatisticsDTO>() {
                    @Override
                    public void onResponse(@NonNull Call<StatisticsDTO> call,
                                           @NonNull Response<StatisticsDTO> response) {
                        progressBar.setVisibility(View.GONE);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            displayStatistics(response.body());
                        } else {
                            tvNoData.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<StatisticsDTO> call, @NonNull Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    private void displayStatistics(StatisticsDTO stats) {
        if (stats.getTotalRides() == 0) {
            tvNoData.setVisibility(View.VISIBLE);
            tvTotalRides.setText("0");
            tvAvgRidesPerDay.setText("0.0");
            tvTotalKm.setText("0.0 km");
            tvAvgKmPerDay.setText("0.0 km");
            tvTotalAmount.setText("0.0 €");
            tvAvgAmountPerDay.setText("0.0 €");
            return;
        }

        tvTotalRides.setText(String.valueOf(stats.getTotalRides()));
        tvAvgRidesPerDay.setText(String.format("%.2f", stats.getAvgRidesPerDay()));
        tvTotalKm.setText(String.format("%.2f km", stats.getTotalKmTraveled()));
        tvAvgKmPerDay.setText(String.format("%.2f km", stats.getAvgKmPerDay()));
        tvTotalAmount.setText(String.format("%.2f €", stats.getTotalAmountSpent()));
        tvAvgAmountPerDay.setText(String.format("%.2f €", stats.getAvgAmountPerDay()));

        displayBarChart(chartContainerRides, stats.getRidesData(), "Rides", 0xFF4CAF50);
        displayBarChart(chartContainerKm, stats.getKmData(), "Kilometers", 0xFF2196F3);
        displayBarChart(chartContainerAmount, stats.getAmountData(), "Amount (€)", 0xFFFF9800);
    }

    private void displayBarChart(LinearLayout container, List<RideDataPointDTO> data, String label, int color) {
        container.removeAllViews();
        
        if (data == null || data.isEmpty()) {
            container.setVisibility(View.GONE);
            return;
        }

        container.setVisibility(View.VISIBLE);
        double maxValue = data.stream()
                .mapToDouble(RideDataPointDTO::getValue)
                .max()
                .orElse(1.0);
        for (RideDataPointDTO point : data) {
            View barView = createBar(point, maxValue, color);
            container.addView(barView);
        }
    }

    private View createBar(RideDataPointDTO point, double maxValue, int color) {
        LinearLayout wrapper = new LinearLayout(requireContext());
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.setLayoutParams(new LinearLayout.LayoutParams(80, LinearLayout.LayoutParams.WRAP_CONTENT));
        wrapper.setPadding(4, 0, 4, 0);

        TextView valueLabel = new TextView(requireContext());
        valueLabel.setText(String.format("%.1f", point.getValue()));
        valueLabel.setTextSize(8);
        valueLabel.setGravity(android.view.Gravity.CENTER);
        valueLabel.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        View bar = new View(requireContext());
        double heightPercent = (point.getValue() / maxValue) * 100;
        int height = (int) (200 * heightPercent / 100);
        LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                Math.max(height, 4)
        );
        bar.setLayoutParams(barParams);
        bar.setBackgroundColor(color);
        bar.setContentDescription(String.format("%.0f on %s", point.getValue(), point.getDate()));

        TextView label = new TextView(requireContext());
        String dateStr = point.getDate().toString();
        if (dateStr.contains("-")) {
            // Format ISO date as MM/dd.
            LocalDate date = LocalDate.parse(dateStr);
            label.setText(date.format(DateTimeFormatter.ofPattern("MM/dd")));
        } else {
            label.setText(dateStr);
        }
        label.setTextSize(10);
        label.setGravity(android.view.Gravity.CENTER);
        label.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        wrapper.addView(valueLabel);
        wrapper.addView(bar);
        wrapper.addView(label);
        return wrapper;
    }

    @FunctionalInterface
    private interface DateSelectedListener {
        void onDateSelected(LocalDate date);
    }
}

