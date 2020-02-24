package com.example.viikko8_3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView view;
    TextView amount;
    SeekBar seek;
    BottleDispenser bd = BottleDispenser.getInstance();
    double double_value = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.TextView);
        amount = findViewById(R.id.TextView2);
        seek = findViewById(R.id.seekBar);

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
        String s = bd.buyBottle(1);
        this.textLog(s);
    }

    public void textLog(String s) {
        view.setText(s);
    }
}
