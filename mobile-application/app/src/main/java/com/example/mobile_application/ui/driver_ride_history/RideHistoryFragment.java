package com.example.mobile_application.ui.driver_ride_history;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.mobile_application.R;
import com.example.mobile_application.adapter.DriverRideHistoryAdapter;
import com.example.mobile_application.dto.DriverRideHistoryDTO;
import com.example.mobile_application.repository.DriverRideHistoryRepository;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideHistoryFragment extends Fragment {

    private TextInputEditText etDateFrom;
    private TextInputEditText etDateTo;
    private Button btnApply;
    private Button btnClear;
    private RecyclerView recyclerView;
    private DriverRideHistoryAdapter adapter;
    private DriverRideHistoryRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_history, container, false);

        etDateFrom = view.findViewById(R.id.etFromDate);
        etDateTo = view.findViewById(R.id.etToDate);
        btnApply = view.findViewById(R.id.btnApply);
        btnClear = view.findViewById(R.id.btnClear);

        repository = new DriverRideHistoryRepository();

        recyclerView = view.findViewById(R.id.rvDriverRideHistory);
        adapter = new DriverRideHistoryAdapter(ride -> {
            RideDetailsFragment fragment = RideDetailsFragment.newInstance(ride);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext())
        );
        recyclerView.setAdapter(adapter);

        loadRides(null, null);

        etDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(etDateFrom);
            }
        });

        etDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(etDateTo);
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearInput();
            }
        });
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyInput();
            }
        });

        return view;
    }

    private void loadRides(@Nullable LocalDate from, @Nullable LocalDate to) {
        repository.getDriverRideHistory(3L, from, to, new Callback<List<DriverRideHistoryDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<DriverRideHistoryDTO>> call,
                                   @NonNull Response<List<DriverRideHistoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DriverRideHistoryDTO> rides = response.body();
                    adapter.setRides(rides);

                    if (rides.isEmpty()) {
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() ->
                                    showToast("No rides for wanted time period")
                            );
                        }
                    }
                } else {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() ->
                                showToast("Error while loading rides")
                        );
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<DriverRideHistoryDTO>> call, @NonNull Throwable t) {
                t.printStackTrace();
                if (isAdded()) {
                    requireActivity().runOnUiThread(() ->
                            showToast("Failed loading rides")
                    );
                }
            }
        });
    }

    private void showDatePicker(TextInputEditText dateInput) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(
                            android.widget.DatePicker view,
                            int selectedYear,
                            int selectedMonth,
                            int selectedDay) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(selectedYear, selectedMonth, selectedDay);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        dateInput.setText(sdf.format(selectedDate.getTime()));
                    }
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }

    private void applyInput() {
        LocalDate from = etDateFrom.getText().toString().isEmpty() ? null :
                LocalDate.parse(etDateFrom.getText().toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate to = etDateTo.getText().toString().isEmpty() ? null :
                LocalDate.parse(etDateTo.getText().toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        loadRides(from, to);
    }

    private void clearInput() {
        etDateFrom.setText("");
        etDateTo.setText("");
        loadRides(null, null);
    }

    private void showToast(String message) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }
}