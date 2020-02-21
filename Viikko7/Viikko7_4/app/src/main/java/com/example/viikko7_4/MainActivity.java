package com.example.viikko7_4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView text;
    EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.textView);
        edit = (EditText) findViewById(R.id.editText);
        text.setText("Vaihda minut!");

        edit.addTextChangedListener(new TextWatcher() {

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
                text.setText(edit.getText());
            }
        });


    }

    public void updateField () {
        text.setText(edit.getText());
    }

}
