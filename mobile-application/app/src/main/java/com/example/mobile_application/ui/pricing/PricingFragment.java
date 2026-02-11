package com.example.mobile_application.ui.pricing;

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
import com.example.mobile_application.adapter.PricingAdapter;
import com.example.mobile_application.dto.ChangePriceDTO;
import com.example.mobile_application.dto.PricingDTO;
import com.example.mobile_application.repository.PricingRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PricingFragment extends Fragment {
    private RecyclerView recyclerView;
    private PricingAdapter adapter;
    private PricingRepository repository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pricing, container, false);

        repository = new PricingRepository();
        recyclerView = view.findViewById(R.id.rvPricing);
        adapter = new PricingAdapter(this::confirmPrice);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext())
        );
        recyclerView.setAdapter(adapter);

        loadPricing();

        return view;
    }

    private void loadPricing() {
        repository.getPricing(new Callback<List<PricingDTO>>() {
            @Override
            public void onResponse(
                    @NonNull Call<List<PricingDTO>> call,
                    @NonNull Response<List<PricingDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PricingDTO> pricing = response.body();
                    adapter.setPricing(pricing);

                    if (pricing.isEmpty())
                        if (isAdded())
                            showToast("No pricing to load");
                } else {
                    if (isAdded())
                        showToast("Error loading pricing");
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<List<PricingDTO>> call,
                    @NonNull Throwable t) {
                if (isAdded())
                    showToast("Failed loading pricing");
            }
        });
    }

    private void confirmPrice(PricingDTO pricing, String newPriceStr) {
        if (newPriceStr.isEmpty()) {
            showToast("Enter a new price");
            return;
        }

        double newPrice;
        try {
            newPrice = Double.parseDouble(newPriceStr);
        } catch (NumberFormatException e) {
            showToast("Invalid price");
            return;
        }

        ChangePriceDTO dto = new ChangePriceDTO();
        dto.setVehicleType(pricing.getVehicleType());
        dto.setPrice(pricing.getPrice());
        dto.setNewPrice(newPrice);

        repository.changePrice(dto, new Callback<Void>() {
            @Override
            public void onResponse(
                    @NonNull Call<Void> call,
                    @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    showToast("Price updated");
                    pricing.setPrice(newPrice);
                    adapter.notifyDataSetChanged();
                } else {
                    showToast("Error updating the price");
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<Void> call,
                    @NonNull Throwable t) {
                showToast("Failed to update the price");
            }
        });
    }

    private void showToast(String message) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }
}