package com.example.viikko8_3;

public class Bottle {
    private String name;
    private String manufacturer;
    private double total_energy;
    private double size;
    private double price;

    public Bottle(){
        name = "Pepsi Max";
        manufacturer = "Pepsi";
        total_energy = 0.3;
        size = 0.5;
        price = 1.8;
    }

    public Bottle(String n, String manuf, double totE, double s, double p){
        name = n;
        manufacturer = manuf;
        total_energy = totE;
        size = s;
        price = p;
    }

    public String getName(){
        return name;
    }

    public String getManufacturer(){
        return manufacturer;
    }

    public double getEnergy(){
        return total_energy;
    }

    public double getPrice(){
        return price;
    }

    public double getSize(){
        return size;
    }
}
