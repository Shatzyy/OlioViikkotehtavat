package com.example.viikko8_5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    TextView view;
    TextView amount;
    SeekBar seek;
    Spinner spin;
    BottleDispenser bd = BottleDispenser.getInstance();
    double double_value = 0;
    ArrayList<String> list = null;
    ArrayAdapter<String> adapter;
    String purchase = "";
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.TextView);
        amount = findViewById(R.id.TextView2);
        seek = findViewById(R.id.seekBar);
        spin = findViewById(R.id.spinner);
        list = bd.getBottles();
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, list);
        spin.setAdapter(adapter);


        seek.setOnSeekBarChangeListener(
                new OnSeekBarChangeListener() {
                    int progress_value = 0;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress_value = progress;
                        double_value = (double) progress_value/10;
                        String s = String.format("%2.2f €", double_value);
                        amount.setText(s);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        double_value = (double) progress_value/10;
                        String s = String.format("%2.2f €", double_value);
                        amount.setText(s);
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        double_value = (double) progress_value/10;
                        String s = String.format("%2.2f €", double_value);
                        amount.setText(s);
                    }
                }
        );
    }

    public void addMoney(View v) {
        bd.addMoney(this.double_value);
        String s = String.format("Klink! Added %2.2f €", double_value);
        this.textLog(s);
        seek.setProgress(0);
    }

    public void returnMoney(View v) {
        double money = bd.returnMoney();
        String s = String.format("Klink klink. Money came out! You got %2.2f€ back", money);
        this.textLog(s);
    }

    public void buyBottle(View v) {
        int tmp = 1;
        tmp += (int)spin.getSelectedItemId();
        String s = bd.buyBottle(tmp);
        this.textLog(s);
        if (s.equals("Not enough money!") || s.equals("Dispenser is empty!") || s.equals("Invalid selection!")) {
        }  else {
            purchase = list.get((int)spin.getSelectedItemId());
            list = bd.getBottles();
            adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, list);
            spin.setAdapter(adapter);
        }
    }

    public void textLog(String s) {
        view.setText(s);
    }

    public void printReceipt(View v){
        if (purchase.equals("")) {
            textLog("You didn't buy anything!");
        } else {
            try {
                context = MainActivity.this;
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                OutputStreamWriter ows = new OutputStreamWriter(context.openFileOutput("receipt.txt", context.MODE_PRIVATE));
                String output = String.format("RECEIPT FOR YOUR PURCHASE\n%s\n\n%s\n\nTHANK YOU, PLEASE COME BACK AGAIN!", dateFormat.format(date), purchase);
                ows.write(output);
                ows.close();
                textLog("Receipt printed!");
            } catch (IOException e) {
                Log.e( "IOException", "Virhe syötteessä");
            }
        }

    }
}
