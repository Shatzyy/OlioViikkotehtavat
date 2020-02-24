package Viikko8_1;
import java.util.Scanner;

public class Mainclass {

	public static void main(String[] args) {
		BottleDispenser bd = BottleDispenser.getInstance();
		do {
			System.out.println("\n*** BOTTLE DISPENSER ***");
			System.out.println("1) Add money to the machine");
			System.out.println("2) Buy a bottle");
			System.out.println("3) Take money out");
			System.out.println("4) List bottles in the dispenser");
			System.out.println("0) End");
			System.out.print("Your choice: ");
			Scanner scanner = new Scanner(System.in);
			
			switch (scanner.nextInt()) {
				case 0:
					System.exit(0);
					scanner.close();
					break;
				case 1:
					bd.addMoney();
					break;
				case 2:
					bd.listBottles();
					System.out.print("Your choice: ");
					Scanner scanner2 = new Scanner(System.in);
					bd.buyBottle(scanner2.nextInt());
					break;
				case 3:
					bd.returnMoney();
					break;
				case 4:
					bd.listBottles();
					break;
				default:
					System.out.println("Invalid selection!");
					break;
			}
			
		} while(true);
		
	}

}
