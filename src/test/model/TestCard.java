package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestCard extends TestStudyMaterial {
    Card c1;
    Card c2;
    Card c3;
    Card c4;

    @BeforeEach
    void setUp() {
        c1 = new Card("What is the powerhouse of the cell?", "Mitochondria", Confidence.NONE);
        c2 = new Card("What is the area of a circle?", " A = πr^2", Confidence.LOW);
        c3 = new Card("q2", "a2", Confidence.MEDIUM);
        c4 = new Card("q3", "a3", Confidence.HIGH);

        sm1 = c1;
        sm2 = c2;
        sm3 = c3;
        sm4 = c4;
    }

    @Test
    void testCard() {
        c1 = new Card("What is the powerhouse of the cell?", "Mitochondria");
        c2 = new Card("What is the area of a circle?", "A = πr^2");

        assertEquals("Mitochondria", c1.getAnswer());
        assertEquals("What is the powerhouse of the cell?", c1.getName());
        assertEquals(c1.getQuestion(), c1.getName());
        assertEquals("A = πr^2", c2.getAnswer());
        assertEquals(c2.getQuestion(), c2.getName());
    }

    @Test
    void testEquals() {
        Card c1Clone = new Card("What is the powerhouse of the cell?", "Mitochondria", Confidence.NONE);
        Card notClone = new Card("What is the powerhouse of the cell?", "Chloroplast", Confidence.NONE);
        assertTrue(c1.equals(c1));
        assertTrue(c1.equals(c1Clone));

        assertFalse(c1.equals(null));
        assertFalse(c1.equals(new Topic("topic")));
        assertFalse(c1.equals(notClone));

        assertTrue(c1.hashCode() == c1Clone.hashCode());
        assertTrue(c1.hashCode() != notClone.hashCode());
    }
}