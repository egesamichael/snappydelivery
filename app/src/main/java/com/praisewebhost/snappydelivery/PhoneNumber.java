package com.praisewebhost.snappydelivery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PhoneNumber extends AppCompatActivity {
    Button getStarted;
    EditText phoneNumber;
    com.hbb20.CountryCodePicker countryCodePicker;
    String code, phone, fullNumber;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        countryCodePicker = findViewById(R.id.countryCode);
        phoneNumber = findViewById(R.id.number);
        getStarted = findViewById(R.id.continue_button);
        code = countryCodePicker.getSelectedCountryCodeWithPlus();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = phoneNumber.getText().toString();
                fullNumber = code + phone;
                if (phone.length() == 9) {
                    new AlertDialog.Builder(context)
                            .setTitle("Confirm Your Number.")
                            .setMessage("Is " + fullNumber + " Your Phone Number?")
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(PhoneNumber.this, Verify.class).putExtra("phone", fullNumber));
                                }
                            })
                            .setNegativeButton(R.string.edit, null)
                            .show();
                }
            }
        });

    }
}