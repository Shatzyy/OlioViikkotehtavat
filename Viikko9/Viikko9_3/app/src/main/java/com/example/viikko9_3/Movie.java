package com.example.viikko9_3;

import java.sql.Time;
import java.text.ParseException;

public class Movie {
    private String title = null;
    private int movieId = 0;
    private String theatreAuditorium = null;
    private int theatreId = 0;
    private Time showStart = null;

    public Movie(String t, int i, String a, int x, String s) throws ParseException {
        this.title = t;
        this.movieId = i;
        this.theatreAuditorium = a;
        this.theatreId = x;
        String arrString[] = s.split("T", 2);
        this.showStart = Time.valueOf(arrString[1]);
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

    public Time getShowStart() {
        return showStart;
    }
}
