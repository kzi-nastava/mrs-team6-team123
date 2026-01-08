package com.example.mobile_application;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ChatDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_dialog, container, false);

        ScrollView scrollMessages = view.findViewById(R.id.scrollMessages);
        LinearLayout layoutMessages = view.findViewById(R.id.layoutMessages);
        EditText etMessage = view.findViewById(R.id.etMessage);
        Button btnSend = view.findViewById(R.id.btnSend);

        btnSend.setOnClickListener(v -> {
            String msg = etMessage.getText().toString().trim();
            if(!msg.isEmpty()){
                // OVDE dodaj poruku u layout

                etMessage.setText("");
                scrollMessages.post(() -> scrollMessages.fullScroll(View.FOCUS_DOWN));

                // Za primer, simuliraj primljenu poruku posle 1s
                scrollMessages.postDelayed(() -> {
                    addMessage(layoutMessages, "Ovo je odgovor", false); // false = primljena poruka
                    scrollMessages.fullScroll(View.FOCUS_DOWN);
                }, 1000);
            }
        });

        return view;
    }
}