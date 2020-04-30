package com.example.viikko9_5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.time.LocalTime;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // Defines
    Spinner spin;
    EditText dateSelect, startTime, endTime, search;
    ArrayList<String> spinnerList = null;
    ArrayAdapter<String> adapter = null;
    RecyclerView recycle;
    Context context;
    String date = "13.08.2020";

    // Get instance of TheatreManager
    TheatreManager tm = TheatreManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Link views
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spin = findViewById(R.id.spinner);
        recycle = findViewById(R.id.movieView);
        dateSelect = findViewById(R.id.dateSelect);
        startTime = findViewById(R.id.startTimeSelect);
        endTime = findViewById(R.id.endTimeSelect);
        search = findViewById(R.id.Search);
        context = this;

        // Create list of Finnkino cinemas
        tm.getTheatreData();
        spinnerList = tm.getSpinnerData();

        // Setting adapter for spinner
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        // Setting listener for dateSelect
        dateSelect.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Auto-generated stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Auto-generated stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (dateSelect.getText().toString().equals("13.08.2020")) {
                    date = dateSelect.getText().toString();
                } else if (dateSelect.getText().toString().equals("01.09.2020")) {
                    date = dateSelect.getText().toString();
                } else {
                    date = "13.08.2020";
                    Toast toast = Toast.makeText(context, "Only 13.08.2020 or 01.09.2020 are acceptable dates", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        // Setting listener for spinner selection
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Set adapter for recycleView
                RecycleAdapter recycleAdapter = new RecycleAdapter(context, tm.getMovieData(tm.getTheatreId(spin.getSelectedItem().toString()), date));
                recycle.setAdapter(recycleAdapter);
                recycle.setLayoutManager(new LinearLayoutManager(context));
                dateSelect.setText(date);
                endTime.setText(null);
                startTime.setText(null);
                search.setText(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void updateSelection(View v) throws ParseException {
        String s = search.getText().toString();
        if (tm.getTheatreId(spin.getSelectedItem().toString()) == 1029) {
            ArrayList<Movie> movieList = new ArrayList<Movie>();
            ArrayList<Movie> finalList = new ArrayList<Movie>();
            ArrayList<Movie> tmpList = null;
            int idList[] = {1014, 1015, 1016, 1017, 1041, 1018, 1019, 1021, 1022};

            for(int q=0;q<idList.length;q++) {
                int id = idList[q];
                tmpList = tm.getMovieData(id, date);
                for (int w=0;w<tmpList.size();w++) {
                    movieList.add(tmpList.get(w));
                }
            }

            try {
                LocalTime start = LocalTime.parse(startTime.getText().toString());
                LocalTime end = LocalTime.parse(endTime.getText().toString());

                for (int a=0;a<movieList.size();a++) {
                    if (movieList.get(a).getShowStart().isAfter(start) && movieList.get(a).getShowStart().isBefore(end)) {
                        if (s == null) {
                            finalList.add(movieList.get(a));
                        } else {
                            if (movieList.get(a).getTitle().contains(s)) {
                                finalList.add(movieList.get(a));
                            }
                        }
                    }
                }

                RecycleAdapter recycleAdapter = new RecycleAdapter(context, finalList);
                recycle.setAdapter(recycleAdapter);
                recycle.setLayoutManager(new LinearLayoutManager(context));
                return;
            } catch (Exception e) {

            }
            for (int a=0;a<movieList.size();a++) {
                if (s == null) {
                    finalList.add(movieList.get(a));
                } else {
                    if (movieList.get(a).getTitle().contains(s)) {
                        finalList.add(movieList.get(a));
                    }
                }
            }

            RecycleAdapter recycleAdapter = new RecycleAdapter(context, finalList);
            recycle.setAdapter(recycleAdapter);
            recycle.setLayoutManager(new LinearLayoutManager(context));
            dateSelect.setText(date);
            return;

        }
        try {
            ArrayList<Movie> tmpList = new ArrayList<Movie>();
            LocalTime start = LocalTime.parse(startTime.getText().toString());
            LocalTime end = LocalTime.parse(endTime.getText().toString());
            ArrayList<Movie> movieList = tm.getMovieData(tm.getTheatreId(spin.getSelectedItem().toString()), date);

            for (int a=0;a<movieList.size();a++) {
                if (movieList.get(a).getShowStart().isAfter(start) && movieList.get(a).getShowStart().isBefore(end)) {
                    if (s == null) {
                        tmpList.add(movieList.get(a));
                    } else {
                        if (movieList.get(a).getTitle().contains(s)) {
                            tmpList.add(movieList.get(a));
                        }
                    }
                }
            }

            RecycleAdapter recycleAdapter = new RecycleAdapter(context, tmpList);
            recycle.setAdapter(recycleAdapter);
            recycle.setLayoutManager(new LinearLayoutManager(context));
            dateSelect.setText(date);
            return;

        } catch (Exception e) {

        }

        ArrayList<Movie> tmpList = new ArrayList<Movie>();
        ArrayList<Movie> movieList = tm.getMovieData(tm.getTheatreId(spin.getSelectedItem().toString()), date);

        for (int a=0;a<movieList.size();a++) {
            if (s == null) {
                tmpList.add(movieList.get(a));
            } else {
                if (movieList.get(a).getTitle().contains(s)) {
                    tmpList.add(movieList.get(a));
                }
            }
        }

        RecycleAdapter recycleAdapter = new RecycleAdapter(context, tmpList);
        recycle.setAdapter(recycleAdapter);
        recycle.setLayoutManager(new LinearLayoutManager(context));
        dateSelect.setText(date);
    }
} 
