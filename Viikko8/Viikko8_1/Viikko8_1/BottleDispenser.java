package Viikko8_1;

import java.util.ArrayList;

public class BottleDispenser {
    private int bottles;
    private double money;
    private ArrayList<Bottle> bottle_array = new ArrayList<Bottle>();
    
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
    
    public static BottleDispenser getInstance() {
    	return bd;
    }
    
    public void addMoney() {
        money += 1;
        System.out.println("Klink! Added more money!");
    }

    public void buyBottle(int selection) {
    	if (selection <= 0 || selection > bottles) {
    		System.out.println("Invalid selection!");
    		return;
    	} else if (bottles == 0) {
    		System.out.println("Dispenser is empty!");
    	} else if (money >= bottle_array.get(selection-1).getPrice()) {
            money -= bottle_array.get(selection-1).getPrice();
            bottles -= 1;
            System.out.println("KACHUNK! " + bottle_array.get(selection-1).getName() + " came out of the dispenser!");
            bottle_array.remove(selection-1);
    	} else {
    		System.out.println("Add money first!");
    	}
    }

    public void returnMoney() {
        System.out.format("Klink klink. Money came out! You got %2.2f€ back\n", money);
        money = 0;
    }
    
    public void listBottles() {
    	for (int y = 0; y<bottles; y++) {
    		System.out.println(y+1 + ". Name: " + bottle_array.get(y).getName());
    		System.out.println("\tSize: " + bottle_array.get(y).getSize() + "\tPrice: " + bottle_array.get(y).getPrice());
    	}
    }

}