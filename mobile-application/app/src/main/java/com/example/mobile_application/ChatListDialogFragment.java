package com.example.mobile_application;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatListDialogFragment extends DialogFragment {

    LinearLayout chatContainer;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list_dialog, container, false);

        chatContainer = view.findViewById(R.id.chatContainer);

        String[] chats = {"User 1", "User 2", "User 3"};
        for (String chat : chats) {
            View chatItem = inflater.inflate(R.layout.item_chat, chatContainer, false);

            TextView tvChatName = chatItem.findViewById(R.id.tvChatName);
            TextView tvLastMessage = chatItem.findViewById(R.id.tvLastMessage);

            tvChatName.setText(chat);
            tvLastMessage.setText(R.string.last_message);

            chatItem.setOnClickListener(v -> {
                openChatDialog(chat, String.valueOf(R.string.last_message));
            });

            chatContainer.addView(chatItem);
        }

        return view;
    }

    private void openChatDialog(String chatName, String lastMessage) {
        ChatDialogFragment dialog = ChatDialogFragment.newInstance(chatName);
        dialog.show(getParentFragmentManager(), "ChatDialog");
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}