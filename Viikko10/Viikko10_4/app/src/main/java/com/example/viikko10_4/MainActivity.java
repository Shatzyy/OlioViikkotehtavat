package com.example.viikko10_4;

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
    String lastURL = "";
    String nextURL = "";

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
                    if (edit.getText().toString().equals("index.html")) {
                        web.loadUrl("file:///android_asset/index.html");
                    } else {
                        lastURL = web.getUrl();
                        nextURL = "";
                        web.loadUrl("https://" + edit.getText());
                    }
                    return true;
                }
                return false;
            }
        });

    }

    public void goPage(View v) {
        if (edit.getText().toString().equals("index.html")) {
            web.loadUrl("file:///android_asset/index.html");
        } else {
            lastURL = web.getUrl();
            nextURL = "";
            web.loadUrl("https://" + edit.getText());
        }

    }

    public void refreshPage(View v) {
        web.loadUrl(web.getUrl());
    }

    public void lastPage(View v) {
        if (lastURL.equals("")) {
            return;
        } else {
            nextURL = web.getUrl();
            web.loadUrl(lastURL);
            lastURL = "";
        }

    }

    public void nextPage(View v) {
        if (nextURL.equals("")) {
            return;
        } else {
            lastURL = web.getUrl();
            web.loadUrl(nextURL);
            nextURL = "";
        }
    }

    public void shoutOut(View v) {
        web.evaluateJavascript("javascript:shoutOut()", null);
    }

    public void initialize(View v) {
        web.evaluateJavascript("javascript:initialize()", null);
    }
}
