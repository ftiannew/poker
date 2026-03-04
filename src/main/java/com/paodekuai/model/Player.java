package com.paodekuai.model;

import lombok.Data;
import java.util.List;

@Data
public class Player {
    private String id;
    private String name;
    private List<Card> hand;
    private boolean isReady;
    
    public Player(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    // Explicit getters and setters to avoid LSP errors
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public List<Card> getHand() { return hand; }
    public void setHand(List<Card> hand) { this.hand = hand; }
    
    public boolean isReady() { return isReady; }
    public void setReady(boolean ready) { isReady = ready; }
}