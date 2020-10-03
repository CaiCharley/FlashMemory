package tests;

import model.Card;
import model.Confidence;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TestStudyMaterial {
    Card c0;
    Card c1;
    Card c2;
    Card c3;

    @Test
    void testCompareTo() {
        assertEquals(3, c3.compareTo(c0));
        assertEquals(2, c2.compareTo(c0));
        assertEquals(1, c1.compareTo(c0));
        assertEquals(1, c2.compareTo(c1));
        assertEquals(0, c2.compareTo(c2));
        assertEquals(-2, c1.compareTo(c3));

        c3.trackStudy(LocalDate.now().minusDays(4), Confidence.MEDIUM);

        assertEquals(1, c2.compareTo(c3));
        assertEquals(2, c2.compareTo(c0));
    }

    @Test
    void testStudy() {
        c0.trackStudy(LocalDate.now().minusWeeks(1), Confidence.LOW);

        assertEquals(1, c0.getTimesStudied());
        assertEquals(LocalDate.now().minusWeeks(1), c0.getLastStudyDate());
        assertEquals(7, c0.getDaysSinceStudied());
        assertEquals(Confidence.LOW, c0.getConfidence());

        c0.trackStudy(LocalDate.now().minusDays(4), Confidence.MEDIUM);

        assertEquals(2, c0.getTimesStudied());
        assertEquals(LocalDate.now().minusDays(4), c0.getLastStudyDate());
        assertEquals(4, c0.getDaysSinceStudied());
        assertEquals(Confidence.MEDIUM, c0.getConfidence());

        c0.trackStudy(Confidence.HIGH);

        assertEquals(3, c0.getTimesStudied());
        assertEquals(LocalDate.now(), c0.getLastStudyDate());
        assertEquals(0, c0.getDaysSinceStudied());
        assertEquals(Confidence.HIGH, c0.getConfidence());
    }
}