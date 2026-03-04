package com.paodekuai.service;

import com.paodekuai.model.GameState;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {
    private final ConcurrentHashMap<String, GameState> games = new ConcurrentHashMap<>();
    
    public GameState createGame(String gameId) {
        GameState gameState = new GameState(gameId);
        games.put(gameId, gameState);
        return gameState;
    }
    
    public GameState getGame(String gameId) {
        return games.get(gameId);
    }
    
    public void removeGame(String gameId) {
        games.remove(gameId);
    }
}