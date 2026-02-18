
package com.example.mobile_application.ui.admin_ride_history;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.AdminRideHistoryDTO;
import com.example.mobile_application.repository.AdminRideHistoryRepository;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRideHistoryFragment extends Fragment
        implements AdminRideHistoryAdapter.OnRideClickListener {

    private RecyclerView recyclerView;
    private AdminRideHistoryAdapter adapter;
    private TextInputEditText etFromDate, etToDate;
    private EditText etUserId;
    private Button btnApply, btnClear, btnSearchUser, btnShowAll;
    private ProgressBar progressBar;
    private TextView tvEmpty, tvViewIndicator;

    private AdminRideHistoryRepository repository;

    private String fromDate = null;
    private String toDate = null;
    private String currentSortBy = "date";
    private String currentSortOrder = "desc";
    private Long filterUserId = null;

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_ride_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new AdminRideHistoryRepository();

        bindViews(view);
        setupRecyclerView();
        setupDatePickers();
        setupButtons();

        loadAllRides();
    }

    private void bindViews(View v) {
        recyclerView = v.findViewById(R.id.rvAdminRideHistory);
        etFromDate = v.findViewById(R.id.etFromDate);
        etToDate = v.findViewById(R.id.etToDate);
        etUserId = v.findViewById(R.id.etUserId);
        btnApply = v.findViewById(R.id.btnApply);
        btnClear = v.findViewById(R.id.btnClear);
        btnSearchUser = v.findViewById(R.id.btnSearchUser);
        btnShowAll = v.findViewById(R.id.btnShowAll);
        progressBar = v.findViewById(R.id.progressBar);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        tvViewIndicator = v.findViewById(R.id.tvViewIndicator);
    }

    private void setupRecyclerView() {
        adapter = new AdminRideHistoryAdapter(this);
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
        btnApply.setOnClickListener(v -> {
            if (filterUserId != null) {
                loadUserRides(filterUserId);
            } else {
                loadAllRides();
            }
        });

        btnClear.setOnClickListener(v -> {
            fromDate = null;
            toDate = null;
            etFromDate.setText("");
            etToDate.setText("");
            if (filterUserId != null) {
                loadUserRides(filterUserId);
            } else {
                loadAllRides();
            }
        });

        btnSearchUser.setOnClickListener(v -> {
            String input = etUserId.getText().toString().trim();
            if (input.isEmpty()) {
                showToast("Please enter a user ID");
                return;
            }
            try {
                filterUserId = Long.parseLong(input);
                loadUserRides(filterUserId);
            } catch (NumberFormatException e) {
                showToast("Invalid user ID");
            }
        });

        btnShowAll.setOnClickListener(v -> {
            filterUserId = null;
            etUserId.setText("");
            loadAllRides();
        });
    }

    // ── Load data ────────────────────────────────────────────────

    private void loadAllRides() {
        filterUserId = null;
        tvViewIndicator.setText("Showing all rides");
        setLoading(true);

        repository.getAllRideHistory(fromDate, toDate,
                currentSortBy, currentSortOrder,
                new Callback<List<AdminRideHistoryDTO>>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<List<AdminRideHistoryDTO>> call,
                            @NonNull Response<List<AdminRideHistoryDTO>> resp) {
                        if (!isAdded()) return;
                        setLoading(false);
                        handleResponse(resp);
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<List<AdminRideHistoryDTO>> call,
                            @NonNull Throwable t) {
                        if (!isAdded()) return;
                        setLoading(false);
                        showToast("Network error");
                    }
                });
    }

    private void loadUserRides(Long userId) {
        tvViewIndicator.setText(String.format("Rides for User #%d", userId));
        setLoading(true);

        repository.getUserRideHistory(userId, fromDate, toDate,
                currentSortBy, currentSortOrder,
                new Callback<List<AdminRideHistoryDTO>>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<List<AdminRideHistoryDTO>> call,
                            @NonNull Response<List<AdminRideHistoryDTO>> resp) {
                        if (!isAdded()) return;
                        setLoading(false);
                        handleResponse(resp);
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<List<AdminRideHistoryDTO>> call,
                            @NonNull Throwable t) {
                        if (!isAdded()) return;
                        setLoading(false);
                        showToast("Network error");
                    }
                });
    }

    private void handleResponse(Response<List<AdminRideHistoryDTO>> resp) {
        if (resp.isSuccessful() && resp.body() != null) {
            List<AdminRideHistoryDTO> rides = resp.body();
            adapter.setRides(rides);
            tvEmpty.setVisibility(rides.isEmpty() ? View.VISIBLE : View.GONE);
            tvViewIndicator.append(String.format(" (%d rides)", rides.size()));
        } else {
            showToast("Failed to load rides");
        }
    }

    // ── Item click → details ─────────────────────────────────────

    @Override
    public void onViewDetails(AdminRideHistoryDTO ride) {
        AdminRideDetailFragment detail =
                AdminRideDetailFragment.newInstance(ride);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, detail)
                .addToBackStack(null)
                .commit();
    }

    // ── UI helpers ───────────────────────────────────────────────

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        tvEmpty.setVisibility(View.GONE);
    }

    private void showToast(String msg) {
        if (isAdded()) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}
