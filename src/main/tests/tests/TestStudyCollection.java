package tests;

import model.Confidence;
import model.StudyCollection;
import model.StudyMaterial;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TestStudyCollection<M extends StudyMaterial> {
    StudyCollection<M> sc1;
    StudyCollection<M> sc2;

    Map<String, M> map1;
    Map<String, M> map2;

    M highPrioritySM;
    M lowPrioritySM;

    @Test
    void testCollectionManipulate() {
        int size1 = sc1.size();

        sc1.add(highPrioritySM);
        sc1.add(lowPrioritySM);
        map1.put(highPrioritySM.getName(), highPrioritySM);
        map1.put(lowPrioritySM.getName(), lowPrioritySM);

        assertEquals(map1, sc1.getAll());
        assertTrue(sc1.contains(highPrioritySM));
        assertTrue(sc1.contains(highPrioritySM.getName()));
        assertTrue(sc1.contains(lowPrioritySM));
        assertTrue(sc1.contains(lowPrioritySM.getName()));
        assertEquals(size1 + 2, sc1.size());

        sc1.remove(lowPrioritySM.getName());
        map1.remove(lowPrioritySM.getName());

        assertEquals(map1, sc1.getAll());
        assertFalse(sc1.contains(lowPrioritySM));
        assertEquals(size1 + 1, sc1.size());

        sc1.remove(highPrioritySM);
        map1.remove(highPrioritySM.getName());

        assertEquals(map1, sc1.getAll());
        assertFalse(sc1.contains(highPrioritySM));
        assertEquals(size1, sc1.size());
    }

    @Test
    void testEditName() {
        int size1 = sc1.size();
        String oldName = highPrioritySM.getName();
        sc1.add(highPrioritySM);

        assertEquals(highPrioritySM, sc1.editName(highPrioritySM.getName(), "high edit"));
        assertTrue(sc1.contains(highPrioritySM));
        assertTrue(sc1.contains("high edit"));
        assertFalse(sc1.contains(oldName));
        assertEquals(size1 + 1, sc1.size());

        assertEquals(highPrioritySM, sc1.editName(highPrioritySM.getName(), "high edit2"));
        assertTrue(sc1.contains(highPrioritySM));
        assertTrue(sc1.contains("high edit2"));
        assertFalse(sc1.contains("high edit"));
        assertEquals(size1 + 1, sc1.size());
    }

    @Test
    void testGetAtConfidence() {
        Map<String, M> low = new HashMap<>();
        for (M m : map1.values()) {
            if (m.getConfidence() == Confidence.LOW) {
                low.put(m.getName(), m);
            }
        }

        Map<String, M> med = new HashMap<>();
        for (M m : map1.values()) {
            if (m.getConfidence() == Confidence.MEDIUM) {
                med.put(m.getName(), m);
            }
        }

        Map<String, M> high = new HashMap<>();
        for (M m : map1.values()) {
            if (m.getConfidence() == Confidence.HIGH) {
                high.put(m.getName(), m);
            }
        }

        assertEquals(low, sc1.getAtConfidence(Confidence.LOW));
        assertEquals(med, sc1.getAtConfidence(Confidence.MEDIUM));
        assertEquals(high, sc1.getAtConfidence(Confidence.HIGH));

        lowPrioritySM.trackStudy(Confidence.HIGH);
        high.put(lowPrioritySM.getName(), lowPrioritySM);
        sc1.add(lowPrioritySM);

        assertEquals(high, sc1.getAtConfidence(Confidence.HIGH));
    }

    @Test
    void testGetBelowConfidence() {
        Map<String, M> belowLow = new HashMap<>();
        for (M m : map1.values()) {
            if (m.getConfidence().ordinal() <= Confidence.LOW.ordinal()) {
                belowLow.put(m.getName(), m);
            }
        }

        Map<String, M> belowMed = new HashMap<>();
        for (M m : map1.values()) {
            if (m.getConfidence().ordinal() <= Confidence.MEDIUM.ordinal()) {
                belowMed.put(m.getName(), m);
            }
        }

        Map<String, M> belowHigh = new HashMap<>();
        for (M m : map1.values()) {
            if (m.getConfidence().ordinal() <= Confidence.HIGH.ordinal()) {
                belowHigh.put(m.getName(), m);
            }
        }

        assertEquals(belowLow, sc1.getBelowConfidence(Confidence.LOW));
        assertEquals(belowMed, sc1.getBelowConfidence(Confidence.MEDIUM));
        assertEquals(belowHigh, sc1.getBelowConfidence(Confidence.HIGH));


        belowLow.put(highPrioritySM.getName(), highPrioritySM);
        belowMed.put(highPrioritySM.getName(), highPrioritySM);
        belowHigh.put(highPrioritySM.getName(), highPrioritySM);
        sc1.add(highPrioritySM);

        assertEquals(belowLow, sc1.getBelowConfidence(Confidence.LOW));
        assertEquals(belowMed, sc1.getBelowConfidence(Confidence.MEDIUM));
        assertEquals(belowHigh, sc1.getBelowConfidence(Confidence.HIGH));

    }

    @Test
    void testGetSorted() {
        List<M> sorted1 = new ArrayList<>(map1.values());
        Collections.sort(sorted1);
        List<M> sorted2 = new ArrayList<>(map2.values());
        Collections.sort(sorted2);

        assertEquals(sorted1, sc1.getSortedByPriority());
        assertEquals(sorted2, sc2.getSortedByPriority());

        sorted1.add(0, highPrioritySM);
        sorted1.add(lowPrioritySM);
        sorted2.add(0, highPrioritySM);
        sorted2.add(lowPrioritySM);

        sc1.add(highPrioritySM);
        sc1.add(lowPrioritySM);
        sc2.add(highPrioritySM);
        sc2.add(lowPrioritySM);

        assertEquals(sorted1, sc1.getSortedByPriority());
        assertEquals(sorted2, sc2.getSortedByPriority());
    }


}
