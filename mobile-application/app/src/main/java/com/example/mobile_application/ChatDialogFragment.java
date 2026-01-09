package com.example.mobile_application;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
                addMessage(layoutMessages, msg, true);
                etMessage.setText("");
                scrollMessages.post(() -> scrollMessages.fullScroll(View.FOCUS_DOWN));
            }
        });

        return view;
    }

    private void addMessage(LinearLayout container, String message, boolean sent) {
        TextView tv = new TextView(
                getContext(),
                null,
                0,
                sent ? R.style.ChatMessageSent : R.style.ChatMessageReceived);
        tv.setText(message);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(8, 4, 8, 4);
        params.gravity = sent ? Gravity.END : Gravity.START;

        tv.setLayoutParams(params);

        container.addView(tv);
    }
}