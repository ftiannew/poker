package com.paodekuai.util;

import com.paodekuai.model.Card;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PaoDeKuaiDeckTest {
    
    @Test
    public void test3PlayerDeckHas51Cards() {
        List<Card> deck = Deck.createAndShuffleDeckForPlayers(3);
        assertEquals(51, deck.size(), "3-player deck should have 51 cards (one 2 removed)");
        
        // Verify that Spade 2 is not in the deck
        boolean hasSpadeTwo = deck.stream()
            .anyMatch(card -> card.getSuit() == 3 && card.getRank() == 1);
        assertFalse(hasSpadeTwo, "Spade 2 should be removed for 3-player games");
    }
    
    @Test
    public void test4PlayerDeckHas52Cards() {
        List<Card> deck = Deck.createAndShuffleDeckForPlayers(4);
        assertEquals(52, deck.size(), "4-player deck should have 52 cards (full deck)");
    }
    
    @Test
    public void test3PlayerEqualDistribution() {
        List<Card> deck = Deck.createAndShuffleDeckForPlayers(3);
        List<List<Card>> hands = Deck.dealCards(deck, 3);
        
        assertEquals(3, hands.size());
        assertEquals(17, hands.get(0).size(), "Player 1 should have 17 cards");
        assertEquals(17, hands.get(1).size(), "Player 2 should have 17 cards");
        assertEquals(17, hands.get(2).size(), "Player 3 should have 17 cards");
        
        int totalCards = hands.stream().mapToInt(List::size).sum();
        assertEquals(51, totalCards, "Total cards dealt should be 51");
    }
    
    @Test
    public void test4PlayerEqualDistribution() {
        List<Card> deck = Deck.createAndShuffleDeckForPlayers(4);
        List<List<Card>> hands = Deck.dealCards(deck, 4);
        
        assertEquals(4, hands.size());
        assertEquals(13, hands.get(0).size(), "Player 1 should have 13 cards");
        assertEquals(13, hands.get(1).size(), "Player 2 should have 13 cards");
        assertEquals(13, hands.get(2).size(), "Player 3 should have 13 cards");
        assertEquals(13, hands.get(3).size(), "Player 4 should have 13 cards");
        
        int totalCards = hands.stream().mapToInt(List::size).sum();
        assertEquals(52, totalCards, "Total cards dealt should be 52");
    }
    
    @Test
    public void testAllCardsUnique() {
        List<Card> deck = Deck.createAndShuffleDeckForPlayers(3);
        List<List<Card>> hands = Deck.dealCards(deck, 3);
        
        // Flatten all hands
        List<Card> allCards = hands.stream()
            .flatMap(List::stream)
            .toList();
        
        // Check for duplicates by comparing size with set size
        long uniqueCount = allCards.stream()
            .distinct()
            .count();
        
        assertEquals(allCards.size(), uniqueCount, "All dealt cards should be unique");
    }
}