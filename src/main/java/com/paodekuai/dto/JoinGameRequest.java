package com.paodekuai.dto;

import com.paodekuai.model.Player;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JoinGameRequest {
    @JsonProperty("gameId")
    private String gameId;
    
    @JsonProperty("player")
    private Player player;
    
    // Explicit getters and setters
    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }
    
    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
}