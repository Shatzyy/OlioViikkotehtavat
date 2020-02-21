package com.example.viikko7_5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    EditText edit;
    EditText filename;
    Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        edit = findViewById(R.id.EditText);
        filename = findViewById(R.id.fileName);

        System.out.println("Kansion sijainti: " + context.getFilesDir());
    }

    public void readFile(View v) {
        try {
            String inputFile = filename.getText().toString();
            InputStream ins = context.openFileInput(inputFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(ins));
            String s = "";
            edit.setText("");
            while ((s = br.readLine()) != null) {
                edit.append(s + "\n");
            }
            ins.close();
        } catch (IOException e) {
            Log.e("IOException", "Virhe syötteessä");
        }
    }

    public void writeFile(View v) {
        try {
            String outputFile = filename.getText().toString();
            OutputStreamWriter ows = new OutputStreamWriter(context.openFileOutput(outputFile, context.MODE_PRIVATE));
            String output = edit.getText().toString();
            ows.write(output);
            ows.close();
        } catch (IOException e) {
            Log.e( "IOException", "Virhe syötteessä");
        }
    }

}
