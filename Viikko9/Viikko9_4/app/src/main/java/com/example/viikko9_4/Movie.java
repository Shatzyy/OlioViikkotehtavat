package com.example.viikko9_4;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.time.LocalTime;

public class Movie {
    private String title = null;
    private int movieId = 0;
    private String theatreAuditorium = null;
    private int theatreId = 0;
    private LocalTime showStart = null;


    public Movie(String t, int i, String a, int x, String s) throws ParseException {
        this.title = t;
        this.movieId = i;
        this.theatreAuditorium = a;
        this.theatreId = x;
        String arrString[] = s.split("T", 2);
        String arrTimeString[] = arrString[1].split(":", 3);
        this.showStart = LocalTime.parse(arrTimeString[0] + ":" + arrTimeString[1]);
    }

    public String getTitle() {
        return title;
    }

    public int getMovieId() {
        return movieId;
    }

    public int getTheatreId() {
        return theatreId;
    }

    public String getTheatreAuditorium() {
        return theatreAuditorium;
    }

    public LocalTime getShowStart() {
        return showStart;
    }
}
