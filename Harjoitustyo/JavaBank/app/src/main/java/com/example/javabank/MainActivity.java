package com.example.javabank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    BankManager bm = BankManager.getInstance();

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

        // Set userRef in BankManager
        String userRef = getIntent().getStringExtra("userRef");
        bm.setUserRef(userRef);

        // Set userRef in Navigation Drawer
        TextView user = navigationView.getHeaderView(0).findViewById(R.id.userHeader);
        user.setText(userRef);

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

            case R.id.nav_transactions:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new TransactionFragment()).commit();
                break;

            case R.id.nav_cards:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new CardFragment()).commit();
                break;

            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();
                break;

            case R.id.nav_logout:
                Intent intent = new Intent(MainActivity.this, Login.class);
                bm.setUserRef("");
                Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
