package com.example.mobile_application.ui.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mobile_application.R;
import com.example.mobile_application.adapter.MessagesAdapter;
import com.example.mobile_application.dto.ChatDTO;
import com.example.mobile_application.dto.MessageRequestDTO;
import com.example.mobile_application.dto.MessageResponseDTO;
import com.example.mobile_application.repository.MessageRepository;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.TokenManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private MessagesAdapter adapter;
    private MessageRepository repository;
    private ChatDTO chat;
    private EditText etMessage;
    private ImageButton btnSend;

    public static ChatFragment newInstance(ChatDTO chat) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable("chat", chat);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> sendMessage());

        repository = new MessageRepository();

        recyclerView = view.findViewById(R.id.rvMessages);
        adapter = new MessagesAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            chat = (ChatDTO) getArguments().getSerializable("chat");
        }

        if (chat != null)
            loadMessages();

        return view;
    }

    public void loadMessages() {
        TokenManager tokenManager = ApiClient.getTokenManager();
        Long userId = tokenManager.getUserId();
        if (userId == -1L) {
            showToast("User must be logged in");
            return;
        }
        repository.getChatMessages(chat.getChatId(), userId,
                new Callback<List<MessageResponseDTO>>() {
            @Override
            public void onResponse(
                    @NonNull Call<List<MessageResponseDTO>> call,
                    @NonNull Response<List<MessageResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MessageResponseDTO> messages = response.body();
                    adapter.setMessages(messages);
                    if (messages.isEmpty())
                        if (isAdded())
                            showToast("No messages to show");
                } else {
                    if (isAdded())
                        showToast("Error loading chat messages");
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<List<MessageResponseDTO>> call,
                    @NonNull Throwable t) {
                if (isAdded())
                    showToast("Failed loading chat messages");
            }
        });
    }

    public void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (message.isEmpty()) {
            showToast("You can't send an empty message");
            return;
        }
        MessageRequestDTO dto = new MessageRequestDTO();
        dto.setChatId(chat.getChatId());
        TokenManager tokenManager = ApiClient.getTokenManager();
        Long userId = tokenManager.getUserId();
        if (userId == -1L) {
            showToast("User must be logged in");
            return;
        }
        dto.setSenderId(userId);
        dto.setContent(message);
        repository.sendMessage(dto, new Callback<Void>() {
            @Override
            public void onResponse(
                    @NonNull Call<Void> call,
                    @NonNull Response<Void> response) {
                etMessage.setText("");
                if (!response.isSuccessful()) {
                    showToast("Error sending message");
                    return;
                }
                showToast("Message sent");
                loadMessages();
            }

            @Override
            public void onFailure(
                    @NonNull Call<Void> call,
                    @NonNull Throwable t) {
                if (isAdded())
                    showToast("Failed sending message");
            }
        });
    }

    private void showToast(String message) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }
}