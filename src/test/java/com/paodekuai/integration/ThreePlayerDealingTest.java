package com.paodekuai.integration;

import com.paodekuai.model.Card;
import com.paodekuai.model.GameState;
import com.paodekuai.model.Player;
import com.paodekuai.service.GameService;
import com.paodekuai.util.Deck;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ThreePlayerDealingTest {
    
    @Autowired
    private GameService gameService;
    
    @Test
    public void testThreePlayerCardDealing() {
        // Create a 3-player game
        String gameId = "test-3p-dealing";
        GameState gameState = gameService.createGame(gameId);
        
        // Add 3 players (1 human + 2 AI)
        Player humanPlayer = new Player("human-1", "Human Test");
        Player aiPlayer1 = new Player("ai-1", "AI Player 1");
        Player aiPlayer2 = new Player("ai-2", "AI Player 2");
        
        gameState.getPlayers().add(humanPlayer);
        gameState.getPlayers().add(aiPlayer1);
        gameState.getPlayers().add(aiPlayer2);
        
        // Deal cards for 3 players
        int numPlayers = 3;
        List<Card> deck = Deck.createAndShuffleDeckForPlayers(numPlayers);
        List<List<Card>> hands = Deck.dealCards(deck, numPlayers);
        
        // Verify deck size for 3 players
        assertEquals(51, deck.size(), "3-player deck should have 51 cards (one 2 removed)");
        
        // Assign hands to players
        gameState.getPlayerHands().put(humanPlayer.getId(), hands.get(0));
        gameState.getPlayerHands().put(aiPlayer1.getId(), hands.get(1));
        gameState.getPlayerHands().put(aiPlayer2.getId(), hands.get(2));
        
        // Verify each player has exactly 17 cards
        assertEquals(17, gameState.getPlayerHands().get(humanPlayer.getId()).size(), 
            "Human player should have 17 cards");
        assertEquals(17, gameState.getPlayerHands().get(aiPlayer1.getId()).size(), 
            "AI Player 1 should have 17 cards");
        assertEquals(17, gameState.getPlayerHands().get(aiPlayer2.getId()).size(), 
            "AI Player 2 should have 17 cards");
        
        // Verify total cards dealt
        long totalCards = gameState.getPlayerHands().values().stream()
            .mapToLong(List::size)
            .sum();
        assertEquals(51, totalCards, "Total cards dealt should be 51");
        
        // Verify Spade 2 is not in any hand (should be removed for 3 players)
        boolean hasSpadeTwo = gameState.getPlayerHands().values().stream()
            .flatMap(List::stream)
            .anyMatch(card -> card.getSuit() == 3 && card.getRank() == 1);
        assertFalse(hasSpadeTwo, "Spade 2 should not be in any player's hand for 3-player game");
        
        System.out.println("✅ 3-player card dealing test passed!");
        System.out.println("✅ Each player has 17 cards");
        System.out.println("✅ Total cards: 51 (Spade 2 removed)");
        System.out.println("✅ Pao De Kuai rules correctly implemented");
    }
}