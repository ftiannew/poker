package com.paodekuai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paodekuai.model.Card;
import lombok.Data;

import java.util.List;

@Data
public class PlayCardRequest {
    @JsonProperty("gameId")
    private String gameId;
    
    @JsonProperty("playerId")  
    private String playerId;
    
    @JsonProperty("cards")
    private List<Card> cards;
    
    // Explicit getters and setters
    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }
    
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    
    public List<Card> getCards() { return cards; }
    public void setCards(List<Card> cards) { this.cards = cards; }
}