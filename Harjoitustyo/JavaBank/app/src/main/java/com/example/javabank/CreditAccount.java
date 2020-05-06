package com.example.javabank;

// Class for storing credit account information
public class CreditAccount extends Account {
    // Credit limit in cents
    private long creditLimit = 0;

    public CreditAccount(String accNr, long b, String oID, long cLimit) {
        super(accNr, b, oID);
        this.creditLimit = cLimit;
    }

    public long getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(long cLimit) {
        this.creditLimit = cLimit;
    }
}
