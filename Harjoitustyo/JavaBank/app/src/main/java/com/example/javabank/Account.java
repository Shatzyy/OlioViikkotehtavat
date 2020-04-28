package com.example.javabank;

// Abstract class for storing account information
public abstract class Account {
    private String accountNr = "";
    // Balance in cents, eg. balance of 12000 is equal to 120,00$
    private long balance = 0;
    private String ownerID = "";

    public Account() {

    }

    public Account(String accNr, long b, String oID) {
        this.accountNr = accNr;
        this.balance = b;
        this.ownerID = oID;
    }
    public String getAccountNr() {
        return accountNr;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long b) {
        this.balance = b;
    }
}
