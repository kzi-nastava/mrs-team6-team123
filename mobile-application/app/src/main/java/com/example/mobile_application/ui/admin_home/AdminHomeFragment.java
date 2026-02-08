package com.example.mobile_application.ui.admin_home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mobile_application.R;
import com.example.mobile_application.adapter.RideMonitoringAdapter;
import com.example.mobile_application.dto.RideMonitoringDTO;
import com.example.mobile_application.repository.RideMonitoringRepository;
import com.example.mobile_application.ui.track_ride.TrackRideFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminHomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private RideMonitoringAdapter adapter;
    private RideMonitoringRepository repository;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        repository = new RideMonitoringRepository();
        recyclerView = view.findViewById(R.id.rvRides);
        searchView = view.findViewById(R.id.searchDrivers);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RideMonitoringAdapter(new ArrayList<>(), rideId -> {
            Fragment fragment = TrackRideFragment.newInstance(rideId);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(adapter);

        loadRides();
        setupSearch();

        return view;
    }

    private void loadRides() {
        repository.getAllActiveRides(new Callback<List<RideMonitoringDTO>>() {
            @Override
            public void onResponse(
                    @NonNull Call<List<RideMonitoringDTO>> call,
                    @NonNull Response<List<RideMonitoringDTO>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    adapter.setData(response.body());
                }
            }
            @Override
            public void onFailure(
                    @NonNull Call<List<RideMonitoringDTO>> call,
                    @NonNull Throwable t) {
                if (isAdded())
                    showToast("Failed loading active rides");
            }
        });
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
    }

    private void showToast(String message) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }
}