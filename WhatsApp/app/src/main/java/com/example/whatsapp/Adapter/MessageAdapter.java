package com.example.whatsapp.Adapter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Message;

import com.example.whatsapp.R;

import java.util.List;

public class MessageAdapter extends AppCompatActivity {

    public MessageAdapter(List<Message> messagesList) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_adapter);
    }
}