package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class TestStudyMaterial {
    StudyMaterial sm1;
    StudyMaterial sm2;
    StudyMaterial sm3;
    StudyMaterial sm4;

    @Test
    void testCompareTo() {
        assertTrue(sm4.compareTo(sm1) > 0);
        assertTrue(sm3.compareTo(sm1) > 0);
        assertTrue(sm2.compareTo(sm1) > 0);
        assertTrue(sm3.compareTo(sm2) > 0);
        assertTrue(sm2.compareTo(sm4) < 0);
        assertEquals(0, sm3.compareTo(sm3));

        sm4.trackStudy(LocalDate.now().minusDays(4), Confidence.MEDIUM);

        assertTrue(sm3.compareTo(sm4) > 0);
        assertTrue(sm3.compareTo(sm1) > 0);
    }

    @Test
    void testStudy() {
        sm1.trackStudy(LocalDate.now().minusWeeks(1), Confidence.LOW);

        assertEquals(1, sm1.getTimesStudied());
        assertEquals(LocalDate.now().minusWeeks(1), sm1.getLastStudyDate());
        assertEquals(7, sm1.getDaysSinceStudied());
        assertEquals(Confidence.LOW, sm1.getConfidence());

        sm1.trackStudy(LocalDate.now().minusDays(4), Confidence.MEDIUM);

        assertEquals(2, sm1.getTimesStudied());
        assertEquals(LocalDate.now().minusDays(4), sm1.getLastStudyDate());
        assertEquals(4, sm1.getDaysSinceStudied());
        assertEquals(Confidence.MEDIUM, sm1.getConfidence());

        sm1.trackStudy(Confidence.HIGH);

        assertEquals(3, sm1.getTimesStudied());
        assertEquals(LocalDate.now(), sm1.getLastStudyDate());
        assertEquals(0, sm1.getDaysSinceStudied());
        assertEquals(Confidence.HIGH, sm1.getConfidence());
    }
}