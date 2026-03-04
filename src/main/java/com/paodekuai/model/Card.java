package com.paodekuai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Card {
    @JsonProperty("suit")
    private int suit; // 0=Hearts, 1=Diamonds, 2=Clubs, 3=Spades
    
    @JsonProperty("rank") 
    private int rank; // 0=Ace, 1=2, ..., 12=King
    
    public Card(int suit, int rank) {
        this.suit = suit;
        this.rank = rank;
    }
    
    public int getSuit() {
        return suit;
    }
    
    public int getRank() {
        return rank;
    }
    
    public int getValue() {
        return suit * 13 + rank;
    }
    
    @Override
    public String toString() {
        String[] suits = {"♥", "♦", "♣", "♠"};
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        return suits[suit] + ranks[rank];
    }
}