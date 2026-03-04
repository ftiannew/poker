package com.paodekuai.service;

import com.paodekuai.model.Card;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RuleValidatorTest {
    
    private RuleValidator ruleValidator = new RuleValidator();
    
    @Test
    public void testSingleCardValid() {
        Card card = new Card(0, 0); // Heart Ace
        assertTrue(ruleValidator.isValidPlay(Arrays.asList(card)));
    }
    
    @Test
    public void testPairValid() {
        Card card1 = new Card(0, 5); // Heart 6
        Card card2 = new Card(1, 5); // Diamond 6
        assertTrue(ruleValidator.isValidPlay(Arrays.asList(card1, card2)));
    }
    
    @Test
    public void testInvalidPlay() {
        Card card1 = new Card(0, 5); // Heart 6
        Card card2 = new Card(1, 7); // Diamond 8
        assertFalse(ruleValidator.isValidPlay(Arrays.asList(card1, card2)));
    }
    
    @Test
    public void testBombValid() {
        Card card1 = new Card(0, 5); // Heart 6
        Card card2 = new Card(1, 5); // Diamond 6
        Card card3 = new Card(2, 5); // Club 6
        Card card4 = new Card(3, 5); // Spade 6
        assertTrue(ruleValidator.isValidPlay(Arrays.asList(card1, card2, card3, card4)));
    }
}