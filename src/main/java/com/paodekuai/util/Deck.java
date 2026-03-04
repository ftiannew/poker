package com.paodekuai.util;

import com.paodekuai.model.Card;
import java.util.*;

public class Deck {
    private static final int TOTAL_CARDS = 52;
    
    /**
     * Creates and shuffles a standard 52-card deck
     */
    public static List<Card> createAndShuffleDeck() {
        return createAndShuffleDeckForPlayers(4); // Default to 4 players (full deck)
    }
    
    /**
     * Creates and shuffles a deck appropriate for the given number of players
     * - 3 players: removes one 2 (Spade 2) to make 51 cards
     * - 4 players: uses full 52-card deck
     */
    public static List<Card> createAndShuffleDeckForPlayers(int numPlayers) {
        List<Card> deck = new ArrayList<>();
        
        // Create standard 52-card deck
        for (int suit = 0; suit < 4; suit++) {
            for (int rank = 0; rank < 13; rank++) {
                deck.add(new Card(suit, rank));
            }
        }
        
        // For 3 players, remove one 2 (Spade 2 - suit=3, rank=1)
        if (numPlayers == 3) {
            Card spadeTwo = new Card(3, 1); // Spades=3, 2=rank 1 (A=0, 2=1, 3=2, ...)
            deck.removeIf(card -> card.getSuit() == 3 && card.getRank() == 1);
        }
        
        // Shuffle the deck
        Collections.shuffle(deck);
        return deck;
    }
    
    /**
     * Deals cards from the deck to the specified number of players
     * Assumes the deck has been prepared appropriately for the player count
     */
    public static List<List<Card>> dealCards(List<Card> deck, int numPlayers) {
        List<List<Card>> hands = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            hands.add(new ArrayList<>());
        }
        
        // Deal cards one by one
        for (int i = 0; i < deck.size(); i++) {
            int playerIndex = i % numPlayers;
            hands.get(playerIndex).add(deck.get(i));
        }
        
        // Sort each hand
        for (List<Card> hand : hands) {
            hand.sort(Comparator.comparingInt(Card::getValue));
        }
        
        return hands;
    }
}