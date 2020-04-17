package com.example.viikko9_3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // Defines
    Spinner spin;
    ArrayList<String> spinnerList = null;
    ArrayAdapter<String> adapter = null;
    RecyclerView recycle;
    Context context;

    // Get instance of TheatreManager
    TheatreManager tm = TheatreManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Link views
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spin = findViewById(R.id.spinner);
        recycle = findViewById(R.id.movieView);
        context = this;

        // Create list of Finnkino cinemas
        tm.getTheatreData();
        spinnerList = tm.getSpinnerData();

        // Setting adapter for spinner
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        // Setting listener for spinner selection
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Set adapter for recycleView
                RecycleAdapter recycleAdapter = new RecycleAdapter(context, tm.getMovieData(tm.getTheatreId(spin.getSelectedItem().toString()), "13.08.2020"));
                recycle.setAdapter(recycleAdapter);
                recycle.setLayoutManager(new LinearLayoutManager(context));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }


}
