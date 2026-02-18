package com.example.mobile_application.ui.irregularity_report;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.DriverRideHistoryDTO;
import com.example.mobile_application.dto.IrregularityReportDTO;
import com.example.mobile_application.dto.TrackRideDTO;
import com.example.mobile_application.repository.IrregularityReportRepository;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.TokenManager;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IrregularityReportFragment extends Fragment {
    private static final String ARG_RIDE = "ride";
    private TextView tvDriver;
    private EditText etComment;
    private Button btnReport, btnCancel;
    private TrackRideDTO ride;
    private IrregularityReportRepository repository;

    public static IrregularityReportFragment newInstance(TrackRideDTO ride) {
        IrregularityReportFragment fragment = new IrregularityReportFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RIDE, ride);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_irregularity_report, container, false);

        tvDriver = view.findViewById(R.id.tvDriver);
        etComment = view.findViewById(R.id.etComment);
        btnReport = view.findViewById(R.id.btnReport);
        btnCancel = view.findViewById(R.id.btnCancel);
        repository = new IrregularityReportRepository();

        if (getArguments() != null) {
            ride = (TrackRideDTO) getArguments().getSerializable("ride");
        }

        dataSetup();
        btnReport.setOnClickListener(v -> report());
        btnCancel.setOnClickListener(v -> cancel());

        return view;
    }

    public void dataSetup() {
        tvDriver.setText(String.format("Driver: %s", ride.getInfo().getDriver()));
    }

    public void cancel() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    public void report() {
        String comment = etComment.getText().toString().trim();

        if (comment.isEmpty()) {
            if (isAdded())
                showToast("Enter a comment");
            return;
        }

        IrregularityReportDTO dto = new IrregularityReportDTO();
        dto.setRideId(ride.getRideId());
        TokenManager tokenManager = ApiClient.getTokenManager();
        Long userId = tokenManager.getUserId();
        if (userId == -1L) {
            showToast("User must be logged in");
            return;
        }
        dto.setAuthorId(userId);
        dto.setComment(comment);
        btnReport.setEnabled(false);

        repository.reportDriver(dto, new Callback<Void>() {
            @Override
            public void onResponse(
                    @NonNull Call<Void> call,
                    @NonNull Response<Void> response) {
                btnReport.setEnabled(true);
                if (response.isSuccessful()) {
                    if (isAdded())
                        showToast("Report sent successfully!");
                } else {
                    if (isAdded())
                        showToast("Error while sending report");
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<Void> call,
                    @NonNull Throwable t) {
                btnReport.setEnabled(true);
                if (isAdded())
                    showToast("Failed sending report");
            }
        });

        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void showToast(String message) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }
}