package tests;

import model.Card;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestCard {
    Card c1;
    Card c2;

    @Test
    void testCard() {
        c1 = new Card("What is the powerhouse of the cell?", "Mitochondria");
        c2 = new Card("What is the area of a circle?  ", " A = πr^2");

        assertEquals("Mitochondria", c1.getAnswer());
        assertEquals("What is the powerhouse of the cell?", c1.getQuestion());
        assertEquals("A = πr^2", c2.getAnswer());
        assertEquals("What is the area of a circle?", c2.getQuestion());
    }

}