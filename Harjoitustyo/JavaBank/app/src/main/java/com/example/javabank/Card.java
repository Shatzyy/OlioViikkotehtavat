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

    String getCardId() {
        return cardId;
    }

    String getAccId() {
        return accId;
    }

    String getDailyLimit() {
        return dailyLimit;
    }
}