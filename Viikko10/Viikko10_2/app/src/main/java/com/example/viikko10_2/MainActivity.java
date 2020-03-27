package com.example.viikko10_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    WebView web;
    EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Link views
        setContentView(R.layout.activity_main);
        web = findViewById(R.id.WebView);
        edit = findViewById(R.id.editText);

        //Web settings
        web.setWebViewClient(new WebViewClient());
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl("https://www.google.com");

        //Enter listener
        edit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    goPage(v);
                    return true;
                }
                return false;
            }
        });

    }

    public void goPage(View v) {
        web.loadUrl("https://" + edit.getText());
    }

    public void refreshPage(View v) {
        web.loadUrl(web.getUrl());
    }
}
