package com.praisewebhost.snappydelivery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

public class LocationsActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener {

    private static final String TAG = "Maps Log";
    private static final int REQUEST_CODE = 101;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    private static int AUTOCOMPLETE_PICKUP_REQUEST_CODE = 1;
    private static int AUTOCOMPLETE_DELIVERY_REQUEST_CODE = 2;
    TextView pickup, delivery;
    Button savecontinue;
    ArrayList<LatLng> mMarkerPoints;
    String pickupLocation, deliveryLocation;
    LatLng pickupLatLng;
    LatLng deliveryLatLng;
    Marker pickupMarker;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    Marker deliveryMarker;
    BottomSheetDetailsFragment bottomSheetFragment;
    private GoogleMap mMap;
    private Polyline mPolyline;
    private List<Polyline> polylinez;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        polylinez = new ArrayList<>();
        pickup = findViewById(R.id.pickupTextView);
        delivery = findViewById(R.id.deliveryTextView);
        savecontinue = findViewById(R.id.btn_continue);
        pickupLocation = pickup.getText().toString();
        deliveryLocation = delivery.getText().toString();
        savecontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogFragment();
            }
        });
        checkLocations();

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAJaScAiFfidPhma5Hd7dRZYorH8oRWl5w");
        }
        PlacesClient placesClient = Places.createClient(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
    }

    private void checkLocations() {
        if (!pickupLocation.equals("Select Pick Up Location") && !deliveryLocation.equals("Select Delivery Location")) {
            savecontinue.setBackground(getDrawable(R.drawable.round));
            savecontinue.setEnabled(true);
            savecontinue.setTextColor(getColor(R.color.white));
        }
    }

    public void showBottomSheetDialogFragment() {
        bottomSheetFragment = new BottomSheetDetailsFragment();
        bottomSheetFragment.show(getSupportFragmentManager(), "TAG");
    }

    public void startAutoCompleteActivity(View view) {

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setCountry("RW")
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_PICKUP_REQUEST_CODE);
    }

    public void startAutoCompleteActivity2(View view) {

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setCountry("RW")
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_DELIVERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_PICKUP_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                pickupLocation = place.getName();
                pickup.setText(pickupLocation);
                pickupLatLng = place.getLatLng();

                if (pickupMarker != null) {
                    pickupMarker.remove();
                }
                pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Pick Up Location"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(pickupLatLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupLatLng, 18));
                // getRouteToMarker(driverLatLng);

                getRouteToMarker();
                checkLocations();
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }


            return;
        }

        if (requestCode == AUTOCOMPLETE_DELIVERY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                deliveryLocation = place.getName();
                delivery.setText(deliveryLocation);
                deliveryLatLng = place.getLatLng();
                if (deliveryMarker != null) {
                    deliveryMarker.remove();
                }
                deliveryMarker = mMap.addMarker(new MarkerOptions().position(deliveryLatLng).title("Delivery Location"));
                //  erasePolylines();
                getRouteToMarker();
                checkLocations();
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }


            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(LocationsActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        //   MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        // mMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
    }

    public void onResume() {

        checkLocations();
        super.onResume();
    }

    private void getRouteToMarker() {

        if (pickupLatLng != null && deliveryLatLng != null) {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .key("AIzaSyC5LKfNg7N2PSoNE_UJ_mGfgcc7e3340Ak")
                    .waypoints(deliveryLatLng, pickupLatLng)
                    .build();
            routing.execute();
        }
    }

    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Log.e(TAG, "Error: " + e.getMessage());
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRoutingStart() {
    }

    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (polylinez.size() > 0) {
            for (Polyline poly : polylinez) {
                poly.remove();
            }
        }

        polylinez = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylinez.add(polyline);
//            distance.setText("Agent is "+route.get(i).getDistanceValue()+"Away");
//            eta.setText("Agent ETA "+route.get(i).getDurationValue());
            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

    }

    public void onRoutingCancelled() {
    }

    private void erasePolylines() {
        for (Polyline line : polylinez) {
            line.remove();
        }
        polylinez.clear();
    }


    public void onConnected(@Nullable Bundle bundle) {

    }


    public void onConnectionSuspended(int i) {

    }


    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}