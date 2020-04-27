package com.example.javabank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.drm.DrmStore;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set drawer for navigation & listener for selection
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Show main fragment after creating MainActivity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new MainFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_main);
        }
    }



    // Method for closing drawer when back is pressed, if it is open
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Method for handling navigation to different fragments
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_main:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new MainFragment()).commit();
                break;

            case R.id.nav_accounts:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new AccountFragment()).commit();
                break;

            case R.id.nav_transfers:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new TransferFragment()).commit();
                break;

            case R.id.nav_payments:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new PaymentFragment()).commit();
                break;

            case R.id.nav_cards:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new CardFragment()).commit();
                break;

            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();
                break;

            case R.id.nav_logout:
                // TODO Return to login page + logout
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
