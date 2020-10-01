package tests;

import model.Confidence;
import model.StudyMaterial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestStudyMaterial {
    StudyMaterial m0;
    StudyMaterial m1;
    StudyMaterial m2;
    StudyMaterial m3;

    @BeforeEach
    void setUp() {
        m0 = new StudyMaterial();
        m1 = new StudyMaterial(Confidence.LOW);
        m2 = new StudyMaterial(Confidence.MEDIUM);
        m3 = new StudyMaterial(Confidence.HIGH);
    }

    @Test
    void testCompareTo() {
        assertEquals(3, m3.compareTo(m0));
        assertEquals(2, m2.compareTo(m0));
        assertEquals(1, m1.compareTo(m0));
        assertEquals(1, m2.compareTo(m1));
        assertEquals(0, m2.compareTo(m2));
        assertEquals(-2, m1.compareTo(m3));
    }

    @Test
    void testStudy() {
        m0.study(LocalDate.now().minusWeeks(1), Confidence.LOW);

        assertEquals(1, m0.getTimesStudied());
        assertEquals(LocalDate.now().minusWeeks(1), m0.getLastStudyDate());
        assertEquals(7, m0.getDaysSinceStudied());
        assertEquals(Confidence.LOW, m0.getConfidence());

        m0.study(LocalDate.now().minusDays(4), Confidence.MEDIUM);

        assertEquals(2, m0.getTimesStudied());
        assertEquals(LocalDate.now().minusDays(4), m0.getLastStudyDate());
        assertEquals(4, m0.getDaysSinceStudied());
        assertEquals(Confidence.MEDIUM, m0.getConfidence());

        m0.study(Confidence.HIGH);

        assertEquals(3, m0.getTimesStudied());
        assertEquals(LocalDate.now(), m0.getLastStudyDate());
        assertEquals(0, m0.getDaysSinceStudied());
        assertEquals(Confidence.HIGH, m0.getConfidence());
    }
}