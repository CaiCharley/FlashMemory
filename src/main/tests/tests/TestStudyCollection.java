package tests;

import model.Card;
import model.Confidence;
import model.Topic;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TestStudyCollection {
    Topic t1;
    Topic t2;
    ArrayList<Card> deck1;
    ArrayList<Card> deck2;
    Map<String, Card> cardMap1 = new HashMap<>();
    Map<String, Card> cardMap2 = new HashMap<>();

    @Test
    void testName() {
        assertEquals("Biology", t1.getName());
        assertEquals("Chemistry", t2.getName());
    }

    @Test
    void testAdd() {
        assertEquals(cardMap1, t1.getAll());
        assertEquals(10, t1.size());

        t1.add("q10", "a10");
        cardMap1.put("q10", t1.get("q10"));

        assertTrue(t1.contains("q10"));
        assertFalse(t1.contains("q15"));
        assertEquals(cardMap1, t1.getAll());
        assertEquals(11, t1.size());
        assertEquals(11, t1.countCards());

        t1.add("q11", "a11");
        cardMap1.put("q11", t1.get("q11"));

        assertTrue(t1.contains(cardMap1.get("q11")));
        assertFalse(t1.contains(new Card("new q", "new a")));
        assertEquals(cardMap1, t1.getAll());
        assertEquals(12, t1.size());
        assertEquals(12, t1.countCards());
    }

    @Test
    void testRemoveCard() {
        Card removedCard = cardMap1.remove("q1");

        assertEquals(removedCard, t1.remove("q1"));
        assertEquals(9, t1.size());
        assertEquals(9, t1.countCards());
        assertFalse(t1.getAll().containsKey("q1"));
        assertFalse(t1.getAll().containsValue(removedCard));
        assertEquals(cardMap1, t1.getAll());

        removedCard = cardMap1.remove("q5");

        assertEquals(removedCard, t1.remove("q5"));
        assertEquals(8, t1.size());
        assertEquals(8, t1.countCards());
        assertFalse(t1.getAll().containsKey("q5"));
        assertFalse(t1.getAll().containsValue(removedCard));
        assertEquals(cardMap1, t1.getAll());

        assertNull(t1.remove("na"));
    }

    @Test
    void testCardSubset() {
        Map<String, Card> cardMap2Subset = new HashMap<>();
        Map<String, Card> cardMap2Subset2 = new HashMap<>();
        cardMap2Subset.put("q0", cardMap2.get("q0"));
        cardMap2Subset.put("q1", cardMap2.get("q1"));
        cardMap2Subset2.put("q3", cardMap2.get("q3"));

        assertEquals(cardMap2Subset, t2.getAtConfidence(Confidence.LOW));
        assertEquals(cardMap2Subset2, t2.getAtConfidence(Confidence.HIGH));
        assertEquals(new HashMap<>(), t2.getAtConfidence(Confidence.NONE));

        cardMap2Subset.put("q2", cardMap2.get("q2"));

        assertEquals(cardMap2Subset, t2.getBelowConfidence(Confidence.MEDIUM));

        Card medCard = new Card("q4", "a4");
        medCard.trackStudy(Confidence.MEDIUM);
        cardMap2Subset.put(medCard.getName(), medCard);
        t2.add(medCard);

        assertEquals(cardMap2Subset, t2.getBelowConfidence(Confidence.MEDIUM));
    }

    @Test
    void testGetSorted() {
        assertEquals(deck2, t2.getSortedByPriority());

        Card c1 = new Card("q4", "a4");
        c1.trackStudy(LocalDate.now().minusDays(4), Confidence.MEDIUM);
        deck2.add(2, c1);
        t2.add(c1);

        assertEquals(deck2, t2.getSortedByPriority());

        Card c2 = new Card("q5", "a5");
        c2.trackStudy(LocalDate.now().minusDays(4), Confidence.HIGH);
        deck2.add(4, c2);
        t2.add(c2);

        assertEquals(deck2, t2.getSortedByPriority());

        Card c3 = new Card("q6", "a6");
        deck2.add(0, c3);
        t2.add(c3);

        assertEquals(deck2, t2.getSortedByPriority());
    }
}
