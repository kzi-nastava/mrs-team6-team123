package com.example.mobile_application.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.FavoriteRouteDTO;

import java.util.List;

public class FavoriteRoutesAdapter extends RecyclerView.Adapter<FavoriteRoutesAdapter.ViewHolder> {
    private List<FavoriteRouteDTO> routes;
    private OnBookClickListener bookClickListener;
    private OnDeleteClickListener deleteClickListener;

    public interface OnBookClickListener {
        void onBookClick(FavoriteRouteDTO route);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position, FavoriteRouteDTO route);
    }

    public FavoriteRoutesAdapter(List<FavoriteRouteDTO> routes) {
        this.routes = routes;
    }

    public void setBookClickListener(OnBookClickListener listener) {
        this.bookClickListener = listener;
    }

    public void setDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_route, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteRouteDTO route = routes.get(position);
        holder.startLocation.setText(route.getStartLocation());
        holder.endLocation.setText(route.getEndLocation());

        holder.bookButton.setOnClickListener(v -> {
            if (bookClickListener != null) {
                bookClickListener.onBookClick(route);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(position, route);
            }
        });
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    public void removeItem(int position) {
        routes.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView startLocation;
        TextView endLocation;
        Button bookButton;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            startLocation = itemView.findViewById(R.id.start_location);
            endLocation = itemView.findViewById(R.id.end_location);
            bookButton = itemView.findViewById(R.id.book_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
