package tests;

import model.Card;
import model.Confidence;
import model.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTopic extends TestStudyCollection<Card> {
    Topic t1;
    Topic t2;

    List<Card> cards1;
    List<Card> cards2;

    //effects: returns list of num cards studied daysAgo, cycling confidence
    public static List<Card> getTestCards(int num, int daysAgo) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Card c = new Card("q" + i, "a" + i);
            c.trackStudy(LocalDate.now().minusDays(daysAgo), Confidence.NONE);
            switch (i % 4) {
                case 0:
                    c.trackStudy(Confidence.NONE);
                    break;
                case 1:
                    c.trackStudy(Confidence.LOW);
                    break;
                case 2:
                    c.trackStudy(Confidence.MEDIUM);
                    break;
                case 3:
                    c.trackStudy(Confidence.HIGH);
                    break;
            }
            cards.add(c);
        }
        return cards;
    }

    @BeforeEach
    void setUp() {
        sc1 = new Topic("Biology");
        sc2 = new Topic("Chemistry", Confidence.MEDIUM);

        t1 = ((Topic) sc1);
        t2 = ((Topic) sc2);

        cards1 = getTestCards(10, 2);
        cards2 = getTestCards(4, 1);

        t1.addAll(cards1);
        t2.addAll(cards2);
    }

    @Test
    void testConstructor() {
        assertEquals("Biology", t1.getName());
        assertEquals("Chemistry", t2.getName());

        assertEquals(Confidence.NONE, t1.getConfidence());
        assertEquals(Confidence.MEDIUM, t2.getConfidence());
    }

    @Test
    void testEditCardQuestion() {
        assertEquals(cards1.get(1), t1.editCardQuestion("q1", "q1edited"));
        assertEquals(cards1.get(5), t1.editCardQuestion("q5", "q5edited"));

        assertEquals("q1edited", t1.get("q1edited").getQuestion());
        assertEquals("q5edited", t1.get("q5edited").getQuestion());
        assertEquals("q7", t1.get("q7").getQuestion());
        assertEquals(10, t1.countCards());
    }

    @Test
    void testEditCardAnswer() {
        assertEquals(cards2.get(0), t2.editCardAnswer("q0", "a0edited"));
        assertEquals(cards2.get(3), t2.editCardAnswer("q3", "a3edited"));

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
}