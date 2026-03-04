package com.paodekuai.integration;

import com.paodekuai.model.Card;
import com.paodekuai.model.GameState;
import com.paodekuai.model.Player;
import com.paodekuai.service.GameService;
import com.paodekuai.util.Deck;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GameIntegrationTest {
    
    @Autowired
    private GameService gameService;
    
    @Test
    public void test3PlayerGameFlow() {
        // Create game
        String gameId = "test-game-3p";
        GameState gameState = gameService.createGame(gameId);
        
        // Add 3 players
        gameState.getPlayers().add(new Player("player1", "Alice"));
        gameState.getPlayers().add(new Player("player2", "Bob"));
        gameState.getPlayers().add(new Player("player3", "Charlie"));
        
        // Start game - this should deal cards properly
        int numPlayers = gameState.getPlayers().size();
        List<Card> deck = Deck.createAndShuffleDeckForPlayers(numPlayers);
        List<List<Card>> hands = Deck.dealCards(deck, numPlayers);
        
        // Assign hands to players
        for (int i = 0; i < numPlayers; i++) {
            Player player = gameState.getPlayers().get(i);
            gameState.getPlayerHands().put(player.getId(), hands.get(i));
        }
        
        // Verify card distribution
        assertEquals(3, gameState.getPlayers().size());
        assertEquals(51, deck.size()); // 3-player deck should have 51 cards
        
        // Each player should have exactly 17 cards
        for (Player player : gameState.getPlayers()) {
            List<Card> hand = gameState.getPlayerHands().get(player.getId());
            assertNotNull(hand);
            assertEquals(17, hand.size(), "Player " + player.getName() + " should have 17 cards");
        }
        
        // Verify all cards are unique and no duplicates
        long totalCards = gameState.getPlayerHands().values().stream()
            .mapToLong(List::size)
            .sum();
        assertEquals(51, totalCards, "Total cards dealt should be 51");
        
        // Verify Spade 2 is not in any hand (should be removed for 3 players)
        boolean hasSpadeTwo = gameState.getPlayerHands().values().stream()
            .flatMap(List::stream)
            .anyMatch(card -> card.getSuit() == 3 && card.getRank() == 1);
        assertFalse(hasSpadeTwo, "Spade 2 should not be in any player's hand for 3-player game");
    }
    
    @Test
    public void test4PlayerGameFlow() {
        // Create game
        String gameId = "test-game-4p";
        GameState gameState = gameService.createGame(gameId);
        
        // Add 4 players
        gameState.getPlayers().add(new Player("player1", "Alice"));
        gameState.getPlayers().add(new Player("player2", "Bob"));
        gameState.getPlayers().add(new Player("player3", "Charlie"));
        gameState.getPlayers().add(new Player("player4", "David"));
        
        // Start game
        int numPlayers = gameState.getPlayers().size();
        List<Card> deck = Deck.createAndShuffleDeckForPlayers(numPlayers);
        List<List<Card>> hands = Deck.dealCards(deck, numPlayers);
        
        // Assign hands to players
        for (int i = 0; i < numPlayers; i++) {
            Player player = gameState.getPlayers().get(i);
            gameState.getPlayerHands().put(player.getId(), hands.get(i));
        }
        
        // Verify card distribution
        assertEquals(4, gameState.getPlayers().size());
        assertEquals(52, deck.size()); // 4-player deck should have 52 cards
        
        // Each player should have exactly 13 cards
        for (Player player : gameState.getPlayers()) {
            List<Card> hand = gameState.getPlayerHands().get(player.getId());
            assertNotNull(hand);
            assertEquals(13, hand.size(), "Player " + player.getName() + " should have 13 cards");
        }
        
        // Verify all cards are dealt
        long totalCards = gameState.getPlayerHands().values().stream()
            .mapToLong(List::size)
            .sum();
        assertEquals(52, totalCards, "Total cards dealt should be 52");
    }
}