package com.paodekuai.model;

import lombok.Data;
import java.util.*;

@Data
public class GameState {
    private String gameId;
    private List<Player> players;
    private Map<String, List<Card>> playerHands;
    private List<List<Card>> playedCards;
    private String currentPlayerId;
    private boolean gameOver;
    private Date createdAt;
    
    public GameState(String gameId) {
        this.gameId = gameId;
        this.players = new ArrayList<>();
        this.playerHands = new HashMap<>();
        this.playedCards = new ArrayList<>();
        this.createdAt = new Date();
        this.gameOver = false;
    }
    
    // Explicit getters and setters to avoid LSP errors
    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }
    
    public List<Player> getPlayers() { return players; }
    public void setPlayers(List<Player> players) { this.players = players; }
    
    public Map<String, List<Card>> getPlayerHands() { return playerHands; }
    public void setPlayerHands(Map<String, List<Card>> playerHands) { this.playerHands = playerHands; }
    
    public List<List<Card>> getPlayedCards() { return playedCards; }
    public void setPlayedCards(List<List<Card>> playedCards) { this.playedCards = playedCards; }
    
    public String getCurrentPlayerId() { return currentPlayerId; }
    public void setCurrentPlayerId(String currentPlayerId) { this.currentPlayerId = currentPlayerId; }
    
    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}