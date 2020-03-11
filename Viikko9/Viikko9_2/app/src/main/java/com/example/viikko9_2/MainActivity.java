package com.example.viikko9_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // Defines
    Spinner spin;
    ArrayList<String> spinnerList = null;
    ArrayAdapter<String> adapter = null;

    // Get instance of TheatreManager
    TheatreManager tm = TheatreManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Link views
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spin = findViewById(R.id.spinner);

        // Create list of Finnkino cinemas
        tm.getTheatreData();
        spinnerList = tm.getSpinnerData();


        // Setting adapter for spinner
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
    }


}
