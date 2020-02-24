package com.example.viikko8_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView view;
    BottleDispenser bd = BottleDispenser.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.TextView);
    }

    public void addMoney(View v) {
        bd.addMoney();
        this.textLog("Klink! Added more money!");
    }

    public void returnMoney(View v) {
        double money = bd.returnMoney();
        String s = String.format("Klink klink. Money came out! You got %2.2f€ back", money);
        System.out.println(s);
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
