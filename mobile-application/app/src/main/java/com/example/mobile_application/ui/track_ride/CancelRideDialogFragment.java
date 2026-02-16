
package com.example.mobile_application.ui.track_ride;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.CancelRideRequestDTO;
import com.example.mobile_application.dto.CancelRideResponseDTO;
import com.example.mobile_application.repository.RideCancelRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CancelRideDialogFragment extends DialogFragment {

    private static final String ARG_RIDE_ID = "ride_id";
    private static final String ARG_USER_ID = "user_id";
    private static final String ARG_FROM = "from";
    private static final String ARG_TO = "to";
    private static final String ARG_DRIVER = "driver";

    private Long rideId;
    private Long userId;

    private EditText etReason;
    private Button btnCancel, btnGoBack;
    private TextView tvFrom, tvTo, tvDriver, tvError;
    private LinearLayout layoutForm, layoutSuccess;
    private RideCancelRepository repository;

    public interface OnRideCancelledListener {
        void onRideCancelled();
    }

    public static CancelRideDialogFragment newInstance(Long rideId, Long userId,
                                                       String from, String to,
                                                       String driver) {
        CancelRideDialogFragment fragment = new CancelRideDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        args.putLong(ARG_USER_ID, userId);
        args.putString(ARG_FROM, from);
        args.putString(ARG_TO, to);
        args.putString(ARG_DRIVER, driver);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID);
            userId = getArguments().getLong(ARG_USER_ID);
        }
        repository = new RideCancelRepository();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_cancel_ride, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvFrom = view.findViewById(R.id.tvCancelFrom);
        tvTo = view.findViewById(R.id.tvCancelTo);
        tvDriver = view.findViewById(R.id.tvCancelDriver);
        tvError = view.findViewById(R.id.tvCancelError);
        etReason = view.findViewById(R.id.etCancelReason);
        btnCancel = view.findViewById(R.id.btnConfirmCancel);
        btnGoBack = view.findViewById(R.id.btnCancelGoBack);
        layoutForm = view.findViewById(R.id.layoutCancelForm);
        layoutSuccess = view.findViewById(R.id.layoutCancelSuccess);

        Bundle args = getArguments();
        if (args != null) {
            tvFrom.setText(args.getString(ARG_FROM, ""));
            tvTo.setText(args.getString(ARG_TO, ""));
            tvDriver.setText(args.getString(ARG_DRIVER, ""));
        }

        btnGoBack.setOnClickListener(v -> dismiss());
        btnCancel.setOnClickListener(v -> performCancel());
    }

    private void performCancel() {
        String reason = etReason.getText().toString().trim();
        if (reason.isEmpty()) {
            tvError.setText("Please provide a reason for cancellation");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        tvError.setVisibility(View.GONE);
        btnCancel.setEnabled(false);
        btnCancel.setText("Cancelling...");
        btnGoBack.setEnabled(false);

        CancelRideRequestDTO request = new CancelRideRequestDTO(userId, reason);

        repository.cancelRide(rideId, request, new Callback<CancelRideResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<CancelRideResponseDTO> call,
                                   @NonNull Response<CancelRideResponseDTO> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    layoutForm.setVisibility(View.GONE);
                    layoutSuccess.setVisibility(View.VISIBLE);

                    View root = getView();
                    if (root != null) {
                        root.postDelayed(() -> {
                            if (isAdded()) {
                                if (getParentFragment() instanceof OnRideCancelledListener) {
                                    ((OnRideCancelledListener) getParentFragment())
                                            .onRideCancelled();
                                }
                                dismiss();
                            }
                        }, 2000);
                    }
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CancelRideResponseDTO> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                showError("Network error. Please try again.");
            }
        });
    }

    private void handleError(Response<CancelRideResponseDTO> response) {
        try {
            String errorBody = response.errorBody() != null
                    ? response.errorBody().string() : "Failed to cancel ride.";
            showError(errorBody);
        } catch (Exception e) {
            showError("Failed to cancel ride.");
        }
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        btnCancel.setEnabled(true);
        btnCancel.setText("Cancel Ride");
        btnGoBack.setEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}