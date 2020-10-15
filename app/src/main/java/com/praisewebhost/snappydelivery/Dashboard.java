package com.praisewebhost.snappydelivery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.praisewebhost.snappydelivery.fragments.HistoryFragment;
import com.praisewebhost.snappydelivery.fragments.HomeFragment;
import com.praisewebhost.snappydelivery.fragments.ProfileFragment;
import com.praisewebhost.snappydelivery.fragments.SendFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class Dashboard extends AppCompatActivity {

    private static final String TAG = "Dashboard";
    BottomNavigationView bottomNavigation;
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            openFragment(HomeFragment.newInstance());
                            return true;
                        case R.id.navigation_send:
                            openFragment(SendFragment.newInstance());
                            return true;
                        case R.id.navigation_history:
                            openFragment(HistoryFragment.newInstance());
                            return true;
                        case R.id.navigation_profile:
                            openFragment(ProfileFragment.newInstance());
                            return true;
                    }
                    return false;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //show start activity

            startActivity(new Intent(Dashboard.this, GetStarted.class));
            Log.i(TAG, "First Run");
        }


        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();

        setContentView(R.layout.activity_dashboard);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(HomeFragment.newInstance());
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}