package com.example.viikko11_2;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private MenuItem item;
    private TextView text, font, width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        text = findViewById(R.id.textView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView nav = (NavigationView)findViewById(R.id.nav_view);
        MenuItem boldCheck = nav.getMenu().findItem(R.id.setting_bold);
        MenuItem italicCheck = nav.getMenu().findItem(R.id.setting_italic);

        CompoundButton boldView = (CompoundButton) boldCheck.getActionView();
        CompoundButton italicView = (CompoundButton) italicCheck.getActionView();

        // Setting Default typeface
        text.setTypeface(null, Typeface.NORMAL);

        // Listener for bold setting
        boldView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked() && text.getTypeface()!= null) {
                    if(text.getTypeface().isItalic()) {
                        text.setTypeface(null, Typeface.BOLD_ITALIC);
                    }
                } else if (buttonView.isChecked()) {
                    text.setTypeface(null, Typeface.BOLD);
                } else if (text.getTypeface() != null) {
                    if (text.getTypeface().isItalic()) {
                        text.setTypeface(null, Typeface.ITALIC);
                    } else {
                        text.setTypeface(null, Typeface.NORMAL);
                    }
                }
            }
        });

        // Listener for italic setting
        italicView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked() && text.getTypeface()!= null) {
                    if(text.getTypeface().isBold()) {
                        text.setTypeface(null, Typeface.BOLD_ITALIC);
                    }
                } else if (buttonView.isChecked()) {
                    text.setTypeface(null, Typeface.ITALIC);
                } else if (text.getTypeface() != null) {
                    if (text.getTypeface().isBold()) {
                        text.setTypeface(null, Typeface.BOLD);
                    } else {
                        text.setTypeface(null, Typeface.NORMAL);
                    }
                }
            }
        });


    }

    @Override
    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void increaseFont(View v) {
        font = findViewById(R.id.fontsize);
        int tmp = Math.round(text.getTextSize()/getResources().getDisplayMetrics().scaledDensity);
        tmp+=1;
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, tmp);
        String s = String.valueOf(tmp);
        font.setText(s);
    }

    public void decreaseFont(View v) {
        font = findViewById(R.id.fontsize);
        int tmp = Math.round(text.getTextSize()/getResources().getDisplayMetrics().scaledDensity);
        tmp-=1;
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, tmp);
        String s = String.valueOf(tmp);
        font.setText(s);
    }

    public void increaseWidth(View v) {
        width = findViewById(R.id.width);
        int tmp = text.getMeasuredWidth();
        tmp+=10;
        text.setLayoutParams(new RelativeLayout.LayoutParams(tmp, text.getMeasuredHeight()));
        String s = String.valueOf(tmp);
        width.setText(s);
    }

    public void decreaseWidth(View v) {
        width = findViewById(R.id.width);
        int tmp = text.getMeasuredWidth();
        tmp-=10;
        text.setLayoutParams(new RelativeLayout.LayoutParams(tmp, text.getMeasuredHeight()));
        String s = String.valueOf(tmp);
        width.setText(s);
    }

    public void increaseHeight(View v) {
        height = findViewById(R.id.height);
        int tmp = text.getMeasuredHeight();
        tmp+=10;
        text.setLayoutParams(new RelativeLayout.LayoutParams(text.getMeasuredWidth(),tmp));
        String s = String.valueOf(tmp);
        height.setText(s);
    }

    public void decreaseHeight(View v) {
        height = findViewById(R.id.height);
        int tmp = text.getMeasuredHeight();
        tmp-=10;
        text.setLayoutParams(new RelativeLayout.LayoutParams(text.getMeasuredWidth(),tmp));
        String s = String.valueOf(tmp);
        height.setText(s);
    }
}
