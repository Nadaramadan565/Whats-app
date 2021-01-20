package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.whatsapp.MainActivity;
import com.example.whatsapp.PhoneEntry;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    private Button btn ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        btn=(Button) findViewById(R.id.button3);
        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
     if (currentUser!=null) {
            SendToMainActivity();
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phone_entry=new Intent(StartActivity.this, PhoneEntry.class);
                startActivity(phone_entry);
            }
        });

    }

    private void SendToMainActivity() {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}