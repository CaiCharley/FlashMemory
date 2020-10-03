package tests;

import model.Card;
import model.Confidence;
import model.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTopicOld extends TestStudyCollectionOld {

    public static ArrayList<Card> testCards(int num) {
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Card c = new Card("q" + i, "a" + i);
            c.trackStudy(LocalDate.now().minusDays(2), Confidence.LOW);
            if (i > 8) {
                c.trackStudy(Confidence.HIGH);
            } else if (i < 5) {
                c.trackStudy(Confidence.MEDIUM);
            }
            cards.add(c);
        }
        return cards;
    }

    @BeforeEach
    void setUp() {
        t1 = new Topic("Biology");
        t2 = new Topic("Chemistry", Confidence.MEDIUM);

        deck1 = testCards(10);
        t1.addAll(deck1);

        for (Card c : deck1) {
            cardMap1.put(c.getName(), c);
        }

        deck2 = testCards(4);
        deck2.get(0).trackStudy(LocalDate.now().minusDays(2), Confidence.LOW);
        deck2.get(1).trackStudy(LocalDate.now().minusDays(1), Confidence.LOW);
        deck2.get(2).trackStudy(LocalDate.now().minusDays(3), Confidence.MEDIUM);
        deck2.get(3).trackStudy(LocalDate.now().minusDays(3), Confidence.HIGH);
        t2.addAll(deck2);

        for (Card c : deck2) {
            cardMap2.put(c.getName(), c);
        }
    }

    @Test
    void testEditCard() {
        t1.editCardAnswer("q9", "a9edited");
        assertEquals(10, t1.size());
        assertEquals("a9edited", t1.get("q9").getAnswer());

        t1.editCardAnswer("q5", "a5edited");
        assertEquals(10, t1.size());
        assertEquals("a5edited", t1.get("q5").getAnswer());

        t1.editCardQuestion("q1", "q1edited");
        assertEquals(10, t1.size());
        assertEquals("q1edited", t1.get("q1edited").getName());

        t1.editCardQuestion("q5", "q5edited");
        assertEquals(10, t1.size());
        assertEquals("q5edited", t1.get("q5edited").getName());
    }

}