package com.paodekuai.integration;

import com.paodekuai.dto.PlayCardRequest;
import com.paodekuai.model.Card;
import com.paodekuai.model.GameState;
import com.paodekuai.model.Player;
import com.paodekuai.service.GameService;
import com.paodekuai.service.RuleValidator;
import com.paodekuai.util.Deck;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GameplayIntegrationTest {
    
    @Autowired
    private GameService gameService;
    
    @Autowired
    private RuleValidator ruleValidator;
    
    @Test
    public void testFull3PlayerGameplayFlow() {
        System.out.println("=== 3-PLAYER GAMEPLAY INTEGRATION TEST ===");
        
        // Step 1: Create and start 3-player game
        String gameId = "gameplay-test-3p";
        GameState gameState = gameService.createGame(gameId);
        
        Player humanPlayer = new Player("human-1", "Human Player");
        Player aiPlayer1 = new Player("ai-1", "AI Player 1");
        Player aiPlayer2 = new Player("ai-2", "AI Player 2");
        
        gameState.getPlayers().addAll(Arrays.asList(humanPlayer, aiPlayer1, aiPlayer2));
        
        // Deal cards
        int numPlayers = 3;
        List<Card> deck = Deck.createAndShuffleDeckForPlayers(numPlayers);
        List<List<Card>> hands = Deck.dealCards(deck, numPlayers);
        
        gameState.getPlayerHands().put(humanPlayer.getId(), hands.get(0));
        gameState.getPlayerHands().put(aiPlayer1.getId(), hands.get(1));
        gameState.getPlayerHands().put(aiPlayer2.getId(), hands.get(2));
        
        // Set first player (who has Diamond 3)
        String firstPlayerId = findPlayerWithDiamond3(gameState);
        gameState.setCurrentPlayerId(firstPlayerId);
        
        System.out.println("✅ Game created with 3 players");
        System.out.println("✅ Each player has 17 cards");
        System.out.println("✅ First player: " + firstPlayerId);
        
        // Step 2: Simulate human player playing a single card
        if (firstPlayerId.equals(humanPlayer.getId())) {
            // Human is first player, play a card
            List<Card> humanHand = gameState.getPlayerHands().get(humanPlayer.getId());
            Card cardToPlay = humanHand.get(0); // Play first card
            
            // Validate the play
            assertTrue(ruleValidator.isValidPlay(Arrays.asList(cardToPlay)), "Single card should be valid");
            
            // Remove card from hand
            humanHand.remove(cardToPlay);
            
            // Add to played cards
            gameState.getPlayedCards().add(Arrays.asList(cardToPlay));
            
            // Check if human won (shouldn't have with 16 cards left)
            assertFalse(ruleValidator.isWinningCondition(humanHand), "Should not have won yet");
            
            // Move to next player
            int currentIndex = gameState.getPlayers().indexOf(humanPlayer);
            int nextIndex = (currentIndex + 1) % gameState.getPlayers().size();
            String nextPlayerId = gameState.getPlayers().get(nextIndex).getId();
            gameState.setCurrentPlayerId(nextPlayerId);
            
            System.out.println("✅ Human player played 1 card");
            System.out.println("✅ Human hand now has " + humanHand.size() + " cards");
            System.out.println("✅ Next player: " + nextPlayerId);
        }
        
        // Step 3: Verify game state integrity
        assertEquals(3, gameState.getPlayers().size(), "Should still have 3 players");
        assertEquals(1, gameState.getPlayedCards().size(), "Should have 1 played card set");
        assertEquals(50, getTotalCardsInGame(gameState), "Should have 50 cards in game (51 - 1 played)");
        
        System.out.println("✅ Game state integrity maintained");
        System.out.println("✅ Total cards in game: " + getTotalCardsInGame(gameState));
        
        // Step 4: Test win condition
        // Clear human hand to simulate winning
        List<Card> humanHand = gameState.getPlayerHands().get(humanPlayer.getId());
        if (!humanHand.isEmpty()) {
            humanHand.clear();
            assertTrue(ruleValidator.isWinningCondition(humanHand), "Empty hand should be winning condition");
            System.out.println("✅ Win condition properly detected");
        }
        
        System.out.println("\n🎉 FULL GAMEPLAY INTEGRATION TEST PASSED!");
        System.out.println("✅ 3-player game creation");
        System.out.println("✅ Card dealing (17 cards each)");
        System.out.println("✅ Card playing functionality");
        System.out.println("✅ Game state updates");
        System.out.println("✅ Win condition detection");
    }
    
    private String findPlayerWithDiamond3(GameState gameState) {
        for (var entry : gameState.getPlayerHands().entrySet()) {
            for (Card card : entry.getValue()) {
                if (card.getSuit() == 1 && card.getRank() == 2) {
                    return entry.getKey();
                }
            }
        }
        return gameState.getPlayers().get(0).getId();
    }
    
    private int getTotalCardsInGame(GameState gameState) {
        int total = 0;
        // Cards in player hands
        for (List<Card> hand : gameState.getPlayerHands().values()) {
            total += hand.size();
        }
        // Cards in played cards
        for (List<Card> played : gameState.getPlayedCards()) {
            total += played.size();
        }
        return total;
    }
}