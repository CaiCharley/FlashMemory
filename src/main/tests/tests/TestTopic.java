package tests;

import model.Card;
import model.Confidence;
import model.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestTopic {
    Topic t1;
    Topic t2;
    ArrayList<Card> deck1;
    ArrayList<Card> deck2;
    Map<String, Card> cardMap1 = new HashMap<>();
    Map<String, Card> cardMap2 = new HashMap<>();

    public static ArrayList<Card> testDeck() {
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Card c = new Card("q" + i, "a" + i);
            c.study(LocalDate.now().minusDays(2), Confidence.LOW);
            if (i > 8) {
                c.study(Confidence.HIGH);
            } else if (i < 5) {
                c.study(Confidence.MEDIUM);
            }
            cards.add(c);
        }
        return cards;
    }

    @BeforeEach
    void setUp() {
        t1 = new Topic("Biology");
        t2 = new Topic("Chemistry");

        deck1 = testDeck();
        deck2 = new ArrayList<>();

        //add cards from deck1 to t1
        for (Card c : deck1) {
            t1.addCard(c);
        }

        //make expected HashMap of cards from deck1 for t1
        for (Card c : deck1) {
            cardMap1.put(c.getQuestion(), c);
        }

        deck2.add(new Card("q0", "a0"));
        deck2.get(0).study(LocalDate.now().minusDays(2), Confidence.LOW);
        deck2.add(new Card("q1", "a1"));
        deck2.get(1).study(LocalDate.now().minusDays(1), Confidence.LOW);
        deck2.add(new Card("q2", "a2"));
        deck2.get(2).study(LocalDate.now().minusDays(3), Confidence.MEDIUM);
        deck2.add(new Card("q3", "a3"));
        deck2.get(3).study(LocalDate.now().minusDays(3), Confidence.HIGH);

        for (Card c : deck2) {
            t2.addCard(c);
        }

        for (Card c : deck2) {
            cardMap2.put(c.getQuestion(), c);
        }
    }

    @Test
    void testName() {
        assertEquals("Biology", t1.getName());
        assertEquals("Chemistry", t2.getName());

        t1.setName("Physics");
        assertEquals("Physics", t1.getName());
    }

    @Test
    void testAddCard() {
        assertEquals(cardMap1, t1.getAllCards());
        assertEquals(10, t1.cardCount());

        t1.addCard("q10", "a10");
        cardMap1.put("q10", t1.getCard("q10"));

        assertEquals(cardMap1, t1.getAllCards());
        assertEquals(11, t1.cardCount());

        t1.addCard("q11", "a11");
        cardMap1.put("q11", t1.getCard("q11"));

        assertEquals(cardMap1, t1.getAllCards());
        assertEquals(12, t1.cardCount());
    }

    @Test
    void testRemoveCard() {
        Card removedCard = cardMap1.remove("q1");

        assertEquals(removedCard, t1.removeCard("q1"));
        assertEquals(9, t1.cardCount());
        assertFalse(t1.getAllCards().containsKey("q1"));
        assertFalse(t1.getAllCards().containsValue(removedCard));
        assertEquals(cardMap1, t1.getAllCards());

        removedCard = cardMap1.remove("q5");

        assertEquals(removedCard, t1.removeCard("q5"));
        assertEquals(8, t1.cardCount());
        assertFalse(t1.getAllCards().containsKey("q5"));
        assertFalse(t1.getAllCards().containsValue(removedCard));
        assertEquals(cardMap1, t1.getAllCards());

        assertNull(t1.removeCard("na"));
    }

    @Test
    void testEditCard() {
        t1.editCardAnswer("q9", "a9edited");
        assertEquals(10, t1.cardCount());
        assertEquals("a9edited", t1.getCard("q9").getAnswer());

        t1.editCardAnswer("q5", "a5edited");
        assertEquals(10, t1.cardCount());
        assertEquals("a5edited", t1.getCard("q5").getAnswer());

        t1.editCardQuestion("q1", "q1edited");
        assertEquals(10, t1.cardCount());
        assertEquals("q1edited", t1.getCard("q1edited").getQuestion());

        t1.editCardQuestion("q5", "q5edited");
        assertEquals(10, t1.cardCount());
        assertEquals("q5edited", t1.getCard("q5edited").getQuestion());
    }

    @Test
    void testCardSubset() {
        Map<String, Card> cardMap2Subset = new HashMap<>();
        Map<String, Card> cardMap2Subset2 = new HashMap<>();
        cardMap2Subset.put("q0", cardMap2.get("q0"));
        cardMap2Subset.put("q1", cardMap2.get("q1"));
        cardMap2Subset2.put("q3", cardMap2.get("q3"));

        assertEquals(cardMap2Subset, t2.getCardsAt(Confidence.LOW));
        assertEquals(cardMap2Subset2, t2.getCardsAt(Confidence.HIGH));
        assertEquals(new HashMap<>(), t2.getCardsAt(Confidence.NONE));

        cardMap2Subset.put("q2", cardMap2.get("q2"));

        assertEquals(cardMap2Subset,t2.getCardsBelow(Confidence.MEDIUM));

        Card medCard = new Card("q4", "a4");
        medCard.study(Confidence.MEDIUM);
        cardMap2Subset.put(medCard.getQuestion(), medCard);
        t2.addCard(medCard);

        assertEquals(cardMap2Subset, t2.getCardsBelow(Confidence.MEDIUM));
    }

    @Test
    void testGetSorted() {
        assertEquals(deck2, t2.getPrioritySortedCards());

        Card c1 = new Card("q4", "a4");
        c1.study(LocalDate.now().minusDays(4), Confidence.MEDIUM);
        deck2.add(2, c1);
        t2.addCard(c1);

        assertEquals(deck2, t2.getPrioritySortedCards());

        Card c2 = new Card("q5", "a5");
        c2.study(LocalDate.now().minusDays(4), Confidence.HIGH);
        deck2.add(4, c2);
        t2.addCard(c2);

        assertEquals(deck2, t2.getPrioritySortedCards());

        Card c3 = new Card("q6", "a6");
        deck2.add(0, c3);
        t2.addCard(c3);

        assertEquals(deck2, t2.getPrioritySortedCards());
    }

}