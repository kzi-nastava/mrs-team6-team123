package com.example.mobile_application.ui.chat;

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
import com.example.mobile_application.adapter.ChatListAdapter;
import com.example.mobile_application.dto.ChatDTO;
import com.example.mobile_application.dto.ChatListDTO;
import com.example.mobile_application.repository.ChatRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatListAdapter adapter;
    private ChatRepository repository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        repository = new ChatRepository();

        recyclerView = view.findViewById(R.id.rvChatList);
        adapter = new ChatListAdapter(chat -> {
            ChatDTO dto = new ChatDTO();
            dto.setChatId(chat);
            ChatFragment fragment = ChatFragment.newInstance(dto);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadChats();

        return view;
    }

    public void loadChats() {
        // TODO: get id from authentication
        repository.getAdminChats(1L, new Callback<List<ChatListDTO>>() {
            @Override
            public void onResponse(
                    @NonNull Call<List<ChatListDTO>> call,
                    @NonNull Response<List<ChatListDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ChatListDTO> chats = response.body();
                    adapter.setChats(chats);
                    if (chats.isEmpty())
                        if (isAdded())
                            showToast("No chats to show");
                } else {
                    if (isAdded())
                        showToast("Error loading admin chats");
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<List<ChatListDTO>> call,
                    @NonNull Throwable t) {
                if (isAdded())
                    showToast("Failed loading admin chats");
            }
        });
    }

    private void showToast(String message) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
    }
}