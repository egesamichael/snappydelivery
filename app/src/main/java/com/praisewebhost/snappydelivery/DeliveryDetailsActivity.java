package com.praisewebhost.snappydelivery;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DeliveryDetailsActivity extends AppCompatActivity {

    private static final String TAG = "Maps Log";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    EditText deliveryTxt, phoneNumber;
    String deliveryTo;
    private int ConatctrequestCode = 10;
    private int ContactresultCode = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);
        deliveryTxt = findViewById(R.id.deliverytxt);
        phoneNumber = findViewById(R.id.phonenumber);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCjRwEdGrBq3BrFIrihY1zHmw3awBGlY7k");
        }
        PlacesClient placesClient = Places.createClient(this);


    }

    public void startAutoCompleteActivity(View view) {

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, Arrays.asList(Place.Field.ID, Place.Field.NAME))
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setCountry("UG")
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                deliveryTo = place.getName();
                deliveryTxt.setText(deliveryTo);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }

            if ((requestCode == ConatctrequestCode) && (requestCode == RESULT_OK)) {
                Cursor cursor = null;
                try {
                    assert data != null;
                    Uri uri = data.getData();
                    cursor = getContentResolver().query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
                    if (cursor != null && cursor.moveToNext()) {
                        String phone = cursor.getString(0);
                        // Do something with phone
                        Toast.makeText(getApplicationContext(), "Phone " + phone, Toast.LENGTH_LONG).show();
                        phoneNumber.setText(phone);
                        Log.i(TAG, phone);
                    }
                } catch (Exception e) {
                    Log.i(TAG, "Message " + e);
                    e.printStackTrace();
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getContact(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, 10);
    }


}