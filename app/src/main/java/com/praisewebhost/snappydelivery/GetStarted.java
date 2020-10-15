package com.praisewebhost.snappydelivery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GetStarted extends AppCompatActivity {
    Button getStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_started_activity);
        getStarted = findViewById(R.id.get_started);
        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GetStarted.this, PhoneNumber.class));
            }
        });
    }
}