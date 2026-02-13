package com.example.mobile_application.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.GeocodingResult;
import com.example.mobile_application.service.GeocodingService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressAutocompleteAdapter extends ArrayAdapter<GeocodingResult> {
    private List<GeocodingResult> suggestions = new ArrayList<>();
    private final GeocodingService geocodingService;
    private final AddressFilter filter = new AddressFilter();

    public AddressAutocompleteAdapter(@NonNull Context context) {
        super(context, android.R.layout.simple_dropdown_item_1line);
        geocodingService = GeocodingService.getInstance();
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Nullable
    @Override
    public GeocodingResult getItem(int position) {
        return suggestions.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        GeocodingResult result = getItem(position);
        if (result != null) {
            textView.setText(result.getDisplayName());
        }

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    private class AddressFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() < 3) {
                results.values = new ArrayList<GeocodingResult>();
                results.count = 0;
                return results;
            }

            // This will be called on background thread
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (constraint == null || constraint.length() < 3) {
                suggestions.clear();
                notifyDataSetChanged();
                return;
            }

            // Fetch suggestions from Nominatim
            geocodingService.searchAddress(constraint.toString(), new Callback<List<GeocodingResult>>() {
                @Override
                public void onResponse(@NonNull Call<List<GeocodingResult>> call,
                        @NonNull Response<List<GeocodingResult>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        suggestions.clear();
                        suggestions.addAll(response.body());
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<GeocodingResult>> call, @NonNull Throwable t) {
                    suggestions.clear();
                    notifyDataSetChanged();
                }
            });
        }
    }
}
