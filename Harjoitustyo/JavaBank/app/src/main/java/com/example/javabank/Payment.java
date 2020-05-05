package com.example.javabank;

// Class for storing payment history
public class Payment {
    private String accFrom;
    private String accTo;
    private String date;
    private String amount;

    public Payment (String accF, String accT, String d, String amt) {
        this.accFrom = accF;
        this.accTo = accT;
        this.date = d;
        this.amount = amt;
    }

    public String getAccFrom() {
        return accFrom;
    }

    public String getAccTo() {
        return accTo;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

}
