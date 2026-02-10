package com.example.mobile_application.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.MessageResponseDTO;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends
        RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private List<MessageResponseDTO> messages = new ArrayList<>();

    public MessagesAdapter() {}

    public void setMessages(List<MessageResponseDTO> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageResponseDTO message = messages.get(position);

        ConstraintLayout.LayoutParams params =
                (ConstraintLayout.LayoutParams) holder.tvMessage.getLayoutParams();

        if (message.isMine()) {
            holder.tvMessage.setBackgroundResource(R.drawable.sent_message_bg);
            params.startToStart = ConstraintLayout.LayoutParams.UNSET;
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        } else {
            holder.tvMessage.setBackgroundResource(R.drawable.received_message_bg);
            params.endToEnd = ConstraintLayout.LayoutParams.UNSET;
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        }

        holder.tvMessage.setLayoutParams(params);
        holder.tvMessage.setText(message.getContent());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        ViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }
}
