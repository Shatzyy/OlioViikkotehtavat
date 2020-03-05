package com.example.viikko9_1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Spinner spin;
    ArrayList<String> spinnerList = null;
    ArrayAdapter<String> adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Link views
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spin = findViewById(R.id.spinner);

        // Create list of Finnkino cinemas
        spinnerList = new ArrayList<String>();
        spinnerList.add("Cine Atlas - Tampere");
        spinnerList.add("Fantasia - Jyväskylä");
        spinnerList.add("Flamingo - Vantaa");
        spinnerList.add("Itis - Helsinki");
        spinnerList.add("Kinopalatsi - Helsinki");
        spinnerList.add("Kinopalatsi - Turku");
        spinnerList.add("Kuvapalatsi - Lahti");
        spinnerList.add("Maxim - Helsinki");
        spinnerList.add("Omena - Espoo");
        spinnerList.add("Plaza - Oulu");
        spinnerList.add("Plevna - Tampere");
        spinnerList.add("Promenadi - Pori");
        spinnerList.add("Sello - Espoo");
        spinnerList.add("Scala - Kuopio");
        spinnerList.add("Strand - Lappeenranta");
        spinnerList.add("Tennispalatsi - Helsinki");

        // Setting adapter for spinner
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
    }
}
