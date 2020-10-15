package com.praisewebhost.snappydelivery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Verify extends AppCompatActivity {
    Button verify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        verify = findViewById(R.id.verify_button);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Verify.this, Register.class));
            }
        });
    }
}