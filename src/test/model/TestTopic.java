package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTopic extends TestStudyCollection<Card> {
    Topic t1;
    Topic t2;

    //effects: returns Map of num cards studied daysAgo, cycling confidence
    public static Map<String, Card> makeTestCards(int num, int daysAgo) {
        Map<String, Card> cards = new HashMap<>();
        for (int i = 0; i < num; i++) {
            Card c = new Card("q" + i, "a" + i);
            switch (i % 4) {
                case 0:
                    c.trackStudy(LocalDate.now().minusDays(daysAgo), Confidence.NONE);
                    break;
                case 1:
                    c.trackStudy(LocalDate.now().minusDays(daysAgo), Confidence.LOW);
                    break;
                case 2:
                    c.trackStudy(LocalDate.now().minusDays(daysAgo), Confidence.MEDIUM);
                    break;
                case 3:
                    c.trackStudy(LocalDate.now().minusDays(daysAgo), Confidence.HIGH);
                    break;
            }
            cards.put(c.getName(), c);
        }
        return cards;
    }

    @BeforeEach
    void setUp() {
        t1 = new Topic("Biology");
        t2 = new Topic("Chemistry", Confidence.MEDIUM);

        //set up as generic StudyCollection
        sc1 = t1;
        sc2 = t2;

        //set up unique StudyMaterials
        highPrioritySM = new Card("bad q", "bad q");
        lowPrioritySM = new Card("good q", "good a");
        highPrioritySM.trackStudy(LocalDate.now().minusDays(7), Confidence.NONE);
        lowPrioritySM.trackStudy(Confidence.HIGH);

        map1 = makeTestCards(10, 2);
        map2 = makeTestCards(4, 1);

        t1.addAll(map1.values());
        t2.addAll(map2.values());
    }

    @Test
    void testConstructor() {
        assertEquals("Biology", t1.getName());
        assertEquals("Chemistry", t2.getName());
        assertEquals("Biology", t1.toString());
        assertEquals("Chemistry", t2.toString());

        assertEquals(Confidence.NONE, t1.getConfidence());
        assertEquals(Confidence.MEDIUM, t2.getConfidence());
        assertEquals(Card.class, sc1.subtype);
        assertEquals(Card.class, sc2.subtype);
    }

    @Test
    void testEditCardQuestion() {
        assertEquals(map1.get("q1"), t1.editCardQuestion("q1", "q1edited"));
        assertEquals(map1.get("q5"), t1.editCardQuestion("q5", "q5edited"));

        assertEquals("q1edited", t1.get("q1edited").getQuestion());
        assertEquals("q5edited", t1.get("q5edited").getQuestion());
        assertEquals("q7", t1.get("q7").getQuestion());
        assertEquals(10, t1.countCards());
    }

    @Test
    void testEditCardAnswer() {
        assertEquals(map2.get("q0"), t2.editCardAnswer("q0", "a0edited"));
        assertEquals(map2.get("q3"), t2.editCardAnswer("q3", "a3edited"));

        assertEquals("a0edited", t2.get("q0").getAnswer());
        assertEquals("a3edited", t2.get("q3").getAnswer());
        assertEquals("a2", t2.get("q2").getAnswer());
        assertEquals(4, t2.countCards());
    }

    @Test
    void testAddCard() {
        t2.add("q add", "a add");
        assertEquals("q add", t2.get("q add").getQuestion());
        assertEquals("a add", t2.get("q add").getAnswer());
        assertEquals(Confidence.NONE, t2.get("q add").getConfidence());
        assertEquals(5, t2.countCards());

        t2.add("q add2", "a add2");
        assertEquals("q add2", t2.get("q add2").getQuestion());
        assertEquals("a add2", t2.get("q add2").getAnswer());
        assertEquals(Confidence.NONE, t2.get("q add2").getConfidence());
        assertEquals(6, t2.countCards());
    }

    @Test
    void testGetAllCards() {
        Collection<Card> cards1 = map1.values();
        assertTrue(cards1.containsAll(t1.getAllCards()));

        Collection<Card> cards2 = map2.values();
        assertTrue(cards2.containsAll(t2.getAllCards()));
    }
}