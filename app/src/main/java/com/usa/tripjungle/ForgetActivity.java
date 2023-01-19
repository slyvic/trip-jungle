package com.usa.tripjungle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class ForgetActivity extends AppCompatActivity {
    EditText email;
    TextView sendBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_forget);

        Objects.requireNonNull(getSupportActionBar()).hide();

        email = findViewById(R.id.forgot_email);
        sendBtn = findViewById(R.id.forgot_button);

        sendBtn.setOnClickListener(v -> {
            Intent i = new Intent(ForgetActivity.this, LoginActivity.class);
            startActivity(i);
        });
    }
}
