package com.example.mobile_application.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.mobile_application.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;

public class RideHistoryFragment extends Fragment {

    private TextInputEditText etDateFrom;
    private TextInputEditText etDateTo;
    private Button btnApply;
    private Button btnClear;
    private MaterialCardView selectedCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_history, container, false);

        LinearLayout layout = view.findViewById(R.id.ride_history_view);

        etDateFrom = view.findViewById(R.id.etFromDate);
        etDateTo = view.findViewById(R.id.etToDate);
        btnApply = view.findViewById(R.id.btnApply);
        btnClear = view.findViewById(R.id.btnClear);
        selectedCard = view.findViewById(R.id.card1);

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

        selectedCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRideDetails();
            }
        });

        return view;
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

    private void clearInput() {
        etDateFrom.setText("");
        etDateTo.setText("");
    }

    private void showRideDetails() {
        RideDetailsFragment fragment = new RideDetailsFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }
}