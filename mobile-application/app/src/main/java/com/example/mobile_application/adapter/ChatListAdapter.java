package com.example.mobile_application.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.ChatListDTO;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends
        RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private final OnChatClickListener listener;
    private List<ChatListDTO> chats = new ArrayList<>();

    public ChatListAdapter(OnChatClickListener listener) {
        this.listener = listener;
    }

    public void setChats(List<ChatListDTO> chats) {
        this.chats = chats;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatListDTO chat = chats.get(position);
        holder.tvUsername.setText(chat.getUserName());
        holder.tvLastMessage.setText(chat.getLastMessage());
        holder.itemView.setOnClickListener(v -> listener.onChatListener(chat.getChatId()));
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvLastMessage;
        ViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
        }
    }

    public interface OnChatClickListener {
        void onChatListener(Long chatId);
    }
}
