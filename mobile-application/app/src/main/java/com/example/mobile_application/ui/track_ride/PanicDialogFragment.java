
package com.example.mobile_application.ui.track_ride;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.PanicAlertRequestDTO;
import com.example.mobile_application.dto.PanicAlertResponseDTO;
import com.example.mobile_application.repository.PanicRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PanicDialogFragment extends DialogFragment {

    private static final String ARG_RIDE_ID = "ride_id";
    private static final String ARG_USER_ID = "user_id";
    private static final String ARG_DRIVER = "driver";
    private static final String ARG_LOCATION = "location";

    private Long rideId;
    private Long userId;
    private boolean panicActivated = false;

    private LinearLayout layoutPrePanic, layoutPostPanic;
    private TextView tvPanicDriver, tvPanicLocation, tvPanicError;
    private TextView tvStatusAlert, tvStatusLocation, tvStatusHelp;
    private Button btnActivate, btnCancelAlert, btnClosePanic;
    private PanicRepository repository;

    public static PanicDialogFragment newInstance(Long rideId, Long userId,
                                                  String driver, String location) {
        PanicDialogFragment fragment = new PanicDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        args.putLong(ARG_USER_ID, userId);
        args.putString(ARG_DRIVER, driver);
        args.putString(ARG_LOCATION, location);
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
        repository = new PanicRepository();
        setCancelable(false);
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
        return inflater.inflate(R.layout.dialog_panic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutPrePanic = view.findViewById(R.id.layoutPrePanic);
        layoutPostPanic = view.findViewById(R.id.layoutPostPanic);
        tvPanicDriver = view.findViewById(R.id.tvPanicDriver);
        tvPanicLocation = view.findViewById(R.id.tvPanicLocation);
        tvPanicError = view.findViewById(R.id.tvPanicError);
        tvStatusAlert = view.findViewById(R.id.tvStatusAlert);
        tvStatusLocation = view.findViewById(R.id.tvStatusLocation);
        tvStatusHelp = view.findViewById(R.id.tvStatusHelp);
        btnActivate = view.findViewById(R.id.btnActivatePanic);
        btnCancelAlert = view.findViewById(R.id.btnCancelPanicAlert);
        btnClosePanic = view.findViewById(R.id.btnClosePanic);

        Bundle args = getArguments();
        if (args != null) {
            tvPanicDriver.setText(args.getString(ARG_DRIVER, ""));
            tvPanicLocation.setText(args.getString(ARG_LOCATION, ""));
        }

        btnClosePanic.setOnClickListener(v -> {
            if (panicActivated) {
                showToast("Cannot close while alert is active.");
            } else {
                dismiss();
            }
        });

        btnActivate.setOnClickListener(v -> confirmAndActivate());
        btnCancelAlert.setOnClickListener(v -> confirmCancelAlert());
    }

    private void confirmAndActivate() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm PANIC")
                .setMessage("Are you sure? This will immediately notify all administrators.")
                .setPositiveButton("Yes, activate", (d, w) -> activatePanic())
                .setNegativeButton("No", null)
                .show();
    }

    private void activatePanic() {
        tvPanicError.setVisibility(View.GONE);
        btnActivate.setEnabled(false);
        btnActivate.setText("SENDING ALERT...");

        String location = getArguments() != null
                ? getArguments().getString(ARG_LOCATION, "45.2550, 19.8450")
                : "45.2550, 19.8450";

        PanicAlertRequestDTO request = new PanicAlertRequestDTO(
                rideId, userId, location);

        repository.triggerPanic(request, new Callback<PanicAlertResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<PanicAlertResponseDTO> call,
                                   @NonNull Response<PanicAlertResponseDTO> response) {
                if (!isAdded()) return;

                if (response.isSuccessful()) {
                    panicActivated = true;
                    layoutPrePanic.setVisibility(View.GONE);
                    layoutPostPanic.setVisibility(View.VISIBLE);
                    animateStatusMessages();
                } else {
                    showPanicError(response);
                }

                android.util.Log.d("PANIC_DEBUG", "Response code: " + response.code());
                if (!response.isSuccessful() && response.errorBody() != null) {
                    try {
                        android.util.Log.d("PANIC_DEBUG", "Error: " + response.errorBody().string());
                    } catch (Exception e) { }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PanicAlertResponseDTO> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                tvPanicError.setText("Network error. Call emergency services directly.");
                tvPanicError.setVisibility(View.VISIBLE);
                btnActivate.setEnabled(true);
                btnActivate.setText("ACTIVATE PANIC ALERT");
            }
        });
    }

    private void animateStatusMessages() {
        View view = getView();
        if (view == null) return;

        tvStatusAlert.setVisibility(View.GONE);
        tvStatusLocation.setVisibility(View.GONE);
        tvStatusHelp.setVisibility(View.GONE);

        view.postDelayed(() -> {
            if (isAdded()) tvStatusAlert.setVisibility(View.VISIBLE);
        }, 500);

        view.postDelayed(() -> {
            if (isAdded()) tvStatusLocation.setVisibility(View.VISIBLE);
        }, 1500);

        view.postDelayed(() -> {
            if (isAdded()) tvStatusHelp.setVisibility(View.VISIBLE);
        }, 2500);
    }

    private void confirmCancelAlert() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cancel Alert")
                .setMessage("Are you sure? Administrators have already been notified.")
                .setPositiveButton("Yes, false alarm", (d, w) -> {
                    panicActivated = false;
                    dismiss();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showPanicError(Response<PanicAlertResponseDTO> response) {
        try {
            String error = response.errorBody() != null
                    ? response.errorBody().string()
                    : "Failed to send alert.";
            tvPanicError.setText(error);
        } catch (Exception e) {
            tvPanicError.setText("Failed to send alert.");
        }
        tvPanicError.setVisibility(View.VISIBLE);
        btnActivate.setEnabled(true);
        btnActivate.setText("ACTIVATE PANIC ALERT");
    }

    private void showToast(String message) {
        if (isAdded()) {
            android.widget.Toast.makeText(requireContext(), message,
                    android.widget.Toast.LENGTH_SHORT).show();
        }
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