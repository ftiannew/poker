package com.paodekuai.service;

import com.paodekuai.model.Card;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RuleValidator {
    
    public boolean isValidPlay(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return false;
        }
        
        // Single card is always valid
        if (cards.size() == 1) {
            return true;
        }
        
        // Check if all cards have the same rank (pairs, triples, etc.)
        int firstRank = cards.get(0).getRank();
        boolean allSameRank = cards.stream().allMatch(card -> card.getRank() == firstRank);
        
        if (allSameRank) {
            return true;
        }
        
        // Check for sequences (consecutive ranks of same suit)
        if (isSequence(cards)) {
            return true;
        }
        
        // Check for bombs (four of a kind)
        if (isBomb(cards)) {
            return true;
        }
        
        return false;
    }
    
    private boolean isSequence(List<Card> cards) {
        if (cards.size() < 3) {
            return false;
        }
        
        // Must be same suit
        int suit = cards.get(0).getSuit();
        if (!cards.stream().allMatch(card -> card.getSuit() == suit)) {
            return false;
        }
        
        // Get ranks and sort
        List<Integer> ranks = cards.stream()
            .map(Card::getRank)
            .sorted()
            .collect(Collectors.toList());
        
        // Check if consecutive
        for (int i = 1; i < ranks.size(); i++) {
            if (ranks.get(i) != ranks.get(i - 1) + 1) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isBomb(List<Card> cards) {
        return cards.size() == 4 && 
               cards.stream().allMatch(card -> card.getRank() == cards.get(0).getRank());
    }
    
    public boolean canFollowSuit(List<Card> lastPlayed, List<Card> currentPlay) {
        if (lastPlayed == null || lastPlayed.isEmpty()) {
            return true; // First play of the round
        }
        
        if (currentPlay == null || currentPlay.isEmpty()) {
            return false;
        }
        
        // If last play was a bomb, only higher bombs can follow
        if (isBomb(lastPlayed)) {
            return isBomb(currentPlay) && currentPlay.get(0).getRank() > lastPlayed.get(0).getRank();
        }
        
        // Same number of cards required
        if (lastPlayed.size() != currentPlay.size()) {
            return false;
        }
        
        // Same type of play required
        if (lastPlayed.size() == 1) {
            // Single card - must be higher rank
            return currentPlay.get(0).getRank() > lastPlayed.get(0).getRank();
        }
        
        if (isSequence(lastPlayed)) {
            // Sequence - must be same length and higher starting rank
            List<Integer> lastRanks = lastPlayed.stream().map(Card::getRank).sorted().collect(Collectors.toList());
            List<Integer> currentRanks = currentPlay.stream().map(Card::getRank).sorted().collect(Collectors.toList());
            
            if (lastRanks.size() != currentRanks.size()) {
                return false;
            }
            
            return currentRanks.get(0) > lastRanks.get(0);
        }
        
        // Same rank play (pairs, triples)
        return currentPlay.get(0).getRank() > lastPlayed.get(0).getRank();
    }
    
    public boolean isWinningCondition(List<Card> hand) {
        return hand == null || hand.isEmpty();
    }
}