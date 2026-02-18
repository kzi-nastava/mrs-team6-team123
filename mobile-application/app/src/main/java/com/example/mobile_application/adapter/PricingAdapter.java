package com.example.mobile_application.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.PricingDTO;

import java.util.ArrayList;
import java.util.List;

public class PricingAdapter extends
        RecyclerView.Adapter<PricingAdapter.ViewHolder> {
    private List<PricingDTO> pricing = new ArrayList<>();
    private final OnPriceConfirmListener listener;

    public PricingAdapter(OnPriceConfirmListener listener) {
        this.listener = listener;
    }

    public void setPricing(List<PricingDTO> pricing) {
        this.pricing = pricing;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_price, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PricingDTO price = pricing.get(position);
        holder.tvVehicle.setText(price.getVehicleType());
        holder.tvPrice.setText(String.valueOf(price.getPrice()));
        holder.btnConfirm.setOnClickListener(v -> {
            String newPrice = holder.etNewPrice.getText().toString();
            listener.onPriceConfirm(price, newPrice);
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVehicle, tvPrice;
        EditText etNewPrice;
        Button btnConfirm;
        ViewHolder(View itemView) {
            super(itemView);
            tvVehicle = itemView.findViewById(R.id.tvVehicleType);
            tvPrice = itemView.findViewById(R.id.tvCurrentPrice);
            etNewPrice = itemView.findViewById(R.id.etNewPrice);
            btnConfirm = itemView.findViewById(R.id.btnConfirmPrice);
        }
    }

    @Override
    public int getItemCount() {
        return pricing.size();
    }

    public interface OnPriceConfirmListener {
        void onPriceConfirm(PricingDTO pricing, String newPrice);
    }
}
