package tests;

import model.Card;
import model.Confidence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestCard extends TestStudyMaterial {
    
    @BeforeEach
    void setUp() {
        c0 = new Card("What is the powerhouse of the cell?", "Mitochondria", Confidence.NONE);
        c1 = new Card("What is the area of a circle?  ", " A = πr^2", Confidence.LOW);
        c2 = new Card("q2", "a2", Confidence.MEDIUM);
        c3 = new Card("q3", "a3", Confidence.HIGH);
    }

    @Test
    void testCard() {
        this.c0 = new Card("What is the powerhouse of the cell?", "Mitochondria");
        this.c1 = new Card("What is the area of a circle?  ", " A = πr^2");

        assertEquals("Mitochondria", this.c0.getAnswer());
        assertEquals("What is the powerhouse of the cell?", this.c0.getQuestion());
        assertEquals("A = πr^2", this.c1.getAnswer());
        assertEquals("What is the area of a circle?", this.c1.getQuestion());
    }

}