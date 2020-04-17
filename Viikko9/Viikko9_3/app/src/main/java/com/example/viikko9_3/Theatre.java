package com.example.viikko9_3;

public class Theatre {
    private String name = null;
    private int id = 0;

    public Theatre(String s, int i) {
        this.name = s;
        this.id = i;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
