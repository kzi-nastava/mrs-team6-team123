package com.example.mobile_application.ui.notifications;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mobile_application.R;
import com.example.mobile_application.adapter.NotificationAdapter;
import com.example.mobile_application.dto.NotificationDTO;
import com.example.mobile_application.dto.RateRideRequestDTO;
import com.example.mobile_application.repository.NotificationRepository;
import com.example.mobile_application.repository.RateRideRepository;
import com.example.mobile_application.ui.rate_ride.RateRideFragment;
import com.example.mobile_application.ui.track_ride.TrackRideFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationListFragment extends Fragment {

    private static final String ARG_UNREAD = "unread";
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private NotificationRepository repository;
    private RateRideRepository rateRideRepository;

    public static NotificationListFragment newInstance(boolean unread) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_UNREAD, unread);
        NotificationListFragment fragment = new NotificationListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_list, container, false);

        repository = new NotificationRepository();
        rateRideRepository = new RateRideRepository();
        boolean unread = getArguments() != null && getArguments().getBoolean(ARG_UNREAD);

        recyclerView = view.findViewById(R.id.rvNotifications);
        adapter = new NotificationAdapter(this::notificationClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        if (unread)
            loadUnread();
        else
            loadRead();

        return view;
    }

    private void loadUnread() {
        repository.getUnreadNotifications(2L, new Callback<List<NotificationDTO>>() {
            @Override
            public void onResponse(
                    @NonNull Call<List<NotificationDTO>> call,
                    @NonNull Response<List<NotificationDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NotificationDTO> notifications = response.body();
                    adapter.setNotifications(notifications);
                    if (notifications.isEmpty()) {
                        if (isAdded()) {
                            showToast("No unread notifications");
                        }
                    }
                } else {
                    if (isAdded()) {
                        showToast("Error loading unread notifications");
                    }
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<List<NotificationDTO>> call,
                    @NonNull Throwable t) {
                if (isAdded()) {
                    showToast("Failed loading unread notifications");
                }
            }
        });
    }

    private void loadRead() {
        repository.getReadNotifications(2L, new Callback<List<NotificationDTO>>() {
            @Override
            public void onResponse(
                    @NonNull Call<List<NotificationDTO>> call,
                    @NonNull Response<List<NotificationDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NotificationDTO> notifications = response.body();
                    adapter.setNotifications(notifications);
                    if (notifications.isEmpty()) {
                        if (isAdded()) {
                            showToast("No read notifications");
                        }
                    }
                } else {
                    if (isAdded()) {
                        showToast("Error loading read notifications");
                    }
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<List<NotificationDTO>> call,
                    @NonNull Throwable t) {
                if (isAdded()) {
                    showToast("Failed loading read notifications");
                }
            }
        });
    }

    private void notificationClick(NotificationDTO notification) {
        markAsRead(notification.getNotificationId());

        if (!notification.getLink().isEmpty()) {
            String link = notification.getLink();
            String rateRidePrefix = "/rate-ride?rideId=";
            String trackRidePrefix = "/track-ride-page?rideId=";
            if (link.startsWith(rateRidePrefix)) {
                String rideIdString = link.substring(rateRidePrefix.length());
                Long rideId = Long.parseLong(rideIdString);
                openRateRide(rideId);
            } else if (link.startsWith(trackRidePrefix)) {
                String rideIdString = link.substring(trackRidePrefix.length());
                Long rideId = Long.parseLong(rideIdString);
                openTrackRide(rideId);
            }
        }
    }

    private void markAsRead(Long notificationId) {
        repository.markAsRead(notificationId, new Callback<Void>() {
            @Override
            public void onResponse(
                    @NonNull Call<Void> call,
                    @NonNull Response<Void> response) {
                if (!response.isSuccessful() || !isAdded())
                    showToast("Error while marking notification as read");
                refresh();
            }

            @Override
            public void onFailure(
                    @NonNull Call<Void> call,
                    @NonNull Throwable t) {
                if (isAdded()) {
                    showToast("Failed reading the notification");
                }
            }
        });
    }

    public void refresh() {
        if (getArguments() != null && getArguments().getBoolean(ARG_UNREAD)) {
            loadUnread();
        } else {
            loadRead();
        }
    }

    private void showToast(String message) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }

    private void openRateRide(Long rideId) {
        final RateRideRequestDTO dto;
        rateRideRepository.getRideForRating(rideId, new Callback<RateRideRequestDTO>() {
            @Override
            public void onResponse(
                    @NonNull Call<RateRideRequestDTO> call,
                    @NonNull Response<RateRideRequestDTO> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    if (isAdded())
                        showToast("Ride for rating wasn't found");
                    return;
                }

                RateRideRequestDTO dto = response.body();

                Fragment fragment = RateRideFragment.newInstance(dto);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.main_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onFailure(
                    @NonNull Call<RateRideRequestDTO> call,
                    @NonNull Throwable t) {
                if (isAdded())
                    showToast("Failed loading the ride");
            }
        });
    }

    private void openTrackRide(Long rideId) {
        Fragment fragment = TrackRideFragment.newInstance(rideId);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}