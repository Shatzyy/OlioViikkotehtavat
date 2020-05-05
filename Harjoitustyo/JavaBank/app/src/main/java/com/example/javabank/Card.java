package com.example.javabank;

// Class for storing card information
public class Card {
    private String cardId;
    private String accId;
    private String dailyLimit;

    public Card (String card, String acc, String dLim) {
        this.cardId = card;
        this.accId = acc;
        this.dailyLimit = dLim;
    }

   public String getCardId() {
        return cardId;
    }

   public String getAccId() {
        return accId;
    }

   public String getDailyLimit() {
        return dailyLimit;
    }
}