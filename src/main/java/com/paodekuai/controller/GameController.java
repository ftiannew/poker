package com.paodekuai.controller;

import com.paodekuai.dto.PlayCardRequest;
import com.paodekuai.model.Card;
import com.paodekuai.model.GameState;
import com.paodekuai.model.Player;
import com.paodekuai.service.GameService;
import com.paodekuai.service.RuleValidator;
import com.paodekuai.util.Deck;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
public class GameController {
    
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    
    @Autowired
    private GameService gameService;
    
    @Autowired
    private RuleValidator ruleValidator;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/game/create")
    @SendTo("/topic/game.created")
    public GameState createGame(String gameName) {
        String gameId = UUID.randomUUID().toString();
        GameState gameState = gameService.createGame(gameId);
        logger.info("Created game: {}", gameId);
        return gameState;
    }
    
    @MessageMapping("/game/join")
    public void joinGame(Map<String, Object> request) {
        try {
            String gameId = (String) request.get("gameId");
            // Convert player map to Player object
            Map<String, Object> playerMap = (Map<String, Object>) request.get("player");
            Player player = new Player((String) playerMap.get("id"), (String) playerMap.get("name"));
            // Set other properties if needed
            if (playerMap.containsKey("isAI")) {
                // We don't have isAI field in Player yet, but we can handle it
            }
            
            GameState gameState = gameService.getGame(gameId);
            if (gameState != null) {
                gameState.getPlayers().add(player);
                logger.info("Player {} joined game {}, total players: {}", 
                    player.getName(), gameId, gameState.getPlayers().size());
                messagingTemplate.convertAndSend("/topic/game/" + gameId + "/joined", player);
            } else {
                logger.warn("Game not found for join request: {}", gameId);
            }
        } catch (Exception e) {
            logger.error("Error in joinGame: {}", e.getMessage(), e);
        }
    }
    
    @MessageMapping("/game/start-direct")
    public void startDirectGame(List<Player> players) {
        try {
            if (players == null || players.size() < 3 || players.size() > 4) {
                logger.warn("Invalid player count for direct start: {}", players != null ? players.size() : "null");
                return;
            }
            
            // Create game ID
            String gameId = UUID.randomUUID().toString();
            GameState gameState = gameService.createGame(gameId);
            
            // Add all players
            gameState.getPlayers().addAll(players);
            
            logger.info("Starting direct game {} with {} players", gameId, players.size());
            
            // Create and shuffle deck appropriate for player count
            int numPlayers = players.size();
            List<Card> deck = Deck.createAndShuffleDeckForPlayers(numPlayers);
            List<List<Card>> hands = Deck.dealCards(deck, numPlayers);
            
            // Assign hands to players
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                gameState.getPlayerHands().put(player.getId(), hands.get(i));
            }
            
            // Set first player (who has Diamond 3)
            String firstPlayerId = findPlayerWithDiamond3(gameState);
            gameState.setCurrentPlayerId(firstPlayerId);
            
            // Broadcast game started
            messagingTemplate.convertAndSend("/topic/game/" + gameId + "/started", gameState);
            
        } catch (Exception e) {
            logger.error("Error in startDirectGame: {}", e.getMessage(), e);
        }
    }
    
    private String findPlayerWithDiamond3(GameState gameState) {
        // Diamond 3 is suit=1, rank=2 (0-indexed: A=0, 2=1, 3=2)
        for (Map.Entry<String, List<Card>> entry : gameState.getPlayerHands().entrySet()) {
            for (Card card : entry.getValue()) {
                if (card.getSuit() == 1 && card.getRank() == 2) {
                    return entry.getKey();
                }
            }
        }
        // Fallback to first player if Diamond 3 not found
        return gameState.getPlayers().get(0).getId();
    }
    
    @MessageMapping("/game/play")
    public void playCards(PlayCardRequest request) {
        try {
            String gameId = request.getGameId();
            String playerId = request.getPlayerId();
            List<Card> cards = request.getCards();
            
            GameState gameState = gameService.getGame(gameId);
            if (gameState == null || gameState.isGameOver()) {
                logger.warn("Game not found or already over: {}", gameId);
                return;
            }
            
            // Validate it's the player's turn
            if (!gameState.getCurrentPlayerId().equals(playerId)) {
                logger.warn("Not player's turn: {} vs {}", gameState.getCurrentPlayerId(), playerId);
                return;
            }
            
            // Validate the play
            if (!ruleValidator.isValidPlay(cards)) {
                logger.warn("Invalid play attempted by player: {}", playerId);
                return;
            }
            
            // Remove cards from player's hand
            List<Card> playerHand = gameState.getPlayerHands().get(playerId);
            if (playerHand == null) {
                logger.warn("Player hand not found for player: {}", playerId);
                return;
            }
            
            playerHand.removeAll(cards);
            
            // Add to played cards
            gameState.getPlayedCards().add(cards);
            
            // Check win condition
            if (ruleValidator.isWinningCondition(playerHand)) {
                gameState.setGameOver(true);
                messagingTemplate.convertAndSend("/topic/game/" + gameId + "/ended", playerId);
                gameService.removeGame(gameId);
                return;
            }
            
            // Determine next player
            int currentIndex = -1;
            for (int i = 0; i < gameState.getPlayers().size(); i++) {
                if (gameState.getPlayers().get(i).getId().equals(playerId)) {
                    currentIndex = i;
                    break;
                }
            }
            
            int nextIndex = (currentIndex + 1) % gameState.getPlayers().size();
            String nextPlayerId = gameState.getPlayers().get(nextIndex).getId();
            gameState.setCurrentPlayerId(nextPlayerId);
            
            // Broadcast updated game state
            messagingTemplate.convertAndSend("/topic/game/" + gameId + "/updated", gameState);
            
        } catch (Exception e) {
            logger.error("Error in playCards: {}", e.getMessage(), e);
        }
    }
}