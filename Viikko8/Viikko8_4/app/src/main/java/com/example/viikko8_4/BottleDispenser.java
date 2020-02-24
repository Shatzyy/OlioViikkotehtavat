package com.example.viikko8_4;

import java.util.ArrayList;

public class BottleDispenser extends MainActivity {
    private static int bottles;
    private static double money;
    private static ArrayList<Bottle> bottle_array = new ArrayList<Bottle>();

    private static BottleDispenser bd = new BottleDispenser();

    public BottleDispenser() {
        bottles = 5;
        money = 0;

        // Add Bottle-objects to the array
        bottle_array.add(new Bottle());
        bottle_array.add(new Bottle("Pepsi Max", "Pepsi", 0.9, 1.5, 2.2));
        bottle_array.add(new Bottle("Coca-Cola Zero", "Coca-Cola", 0.01, 0.5, 2.0));
        bottle_array.add(new Bottle("Coca-Cola Zero", "Coca-Cola", 0.03, 1.5, 2.5));
        bottle_array.add(new Bottle("Fanta Zero", "Fanta", 0.02, 0.5, 1.95));
    }

    // Singleton for BottleDispenser
    public static BottleDispenser getInstance() {
        return bd;
    }

    public static void addMoney(double amount) {
        money += amount;
    }

    public static String buyBottle(int selection) {
        if (bottles == 0) {
            return "Dispenser is empty!";
        } else if (selection <= 0 || selection > bottles) {
            return "Invalid selection!";
        } else if (money >= bottle_array.get(selection-1).getPrice()) {
            money -= bottle_array.get(selection-1).getPrice();
            bottles -= 1;
            String temp = String.format("KACHUNK! %s came out of the dispenser!", bottle_array.get(selection-1).getName());
            bottle_array.remove(selection-1);
            return temp;
        } else {
            return "Not enough money!";
        }
    }

    public static double returnMoney() {
        double temp = money;
        money = 0;
        return temp;
    }

    public static ArrayList<String> getBottles() {
        ArrayList<String> temp = new ArrayList<String>();
        String tempName;
        double tempSize;
        double tempPrice;
        String tempString;
        for (int y = 0; y<bottles; y++) {
            tempName = bottle_array.get(y).getName();
            tempSize = bottle_array.get(y).getSize();
            tempPrice = bottle_array.get(y).getPrice();
            tempString = String.format("%s %2.1fl p: %2.1f €",tempName, tempSize, tempPrice);
            System.out.println(tempString);
            temp.add(tempString);
        }
        return temp;
    }

}
