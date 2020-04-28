package com.example.javabank;

// Class for managing all Bank activities
public class BankManager {
    private static BankManager bm = new BankManager();

    public BankManager () {

    }

    // Singleton for BankManager
    public static BankManager getInstance() {
        return bm;
    }


    // Methods for managing accounts
    public void createAccount() {
        //TODO account managing
    }

    public void deleteAccount() {
        //TODO account managing
    }

    public void updateAccount() {
        //TODO account managing
    }

    // Methods for managing bank cards
    public void createBankCard() {
        //TODO card managing
    }

    public void deleteBankCard() {
        //TODO card managing
    }

    public void updateBankCard() {
        //TODO card managing
    }

    // Methods for transfers, deposits & withdraws
    public void withdrawMoney() {
        // TODO withdraw
    }

    public void depositMoney() {
        // TODO deposit
    }

    public void transferMoney() {
        // TODO Transfer
    }

    // Method for writing account information into JSON -file
    public void writeJSON(String fname) {
        // TODO Write JSON
    }

}
