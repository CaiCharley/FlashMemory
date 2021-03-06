package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestCourse extends TestStudyCollection<Topic> {
    Course c1;
    Course c2;

    //effects: returns Map of a number of Topics with different confidence, each with a number of cards with confidence
    //  studied daysAgo
    public static Map<String, Topic> makeTestTopics(int numTopics, int numCards, int daysAgo) {
        Map<String, Topic> topics = new HashMap<>();
        for (int i = 0; i < numTopics; i++) {
            String topicName = "t" + i;
            Confidence[] confidenceList = new Confidence[]{
                    Confidence.LOW,
                    Confidence.MEDIUM,
                    Confidence.LOW,
                    Confidence.MEDIUM,
                    Confidence.HIGH,
                    Confidence.HIGH};
            Topic topic = new Topic(topicName);
            topic.trackStudy(confidenceList[i % 6]);
            topics.put(topicName, topic);

            topic.addAll(TestTopic.makeTestCards(numCards, daysAgo).values());
        }
        return topics;
    }

    @BeforeEach
    void setUp() {
        c1 = new Course("MICB 201");
        c2 = new Course("CPSC 210", Confidence.HIGH);

        //set up as generic StudyCollection
        sc1 = c1;
        sc2 = c2;

        //set up unique StudyMaterials
        highPrioritySM = new Topic("Roman History");
        lowPrioritySM = new Topic("Algebra");
        highPrioritySM.trackStudy(LocalDate.now().minusDays(7), Confidence.NONE);
        lowPrioritySM.trackStudy(Confidence.HIGH);

        map1 = makeTestTopics(5, 6, 1);
        map2 = makeTestTopics(3, 6, 1);

        c1.addAll(map1.values());
        c2.addAll(map2.values());
    }

    @Test
    void testConstructor() {
        assertEquals("MICB 201", c1.getName());
        assertEquals("CPSC 210", c2.getName());
        assertEquals("MICB 201", c1.toString());
        assertEquals("CPSC 210", c2.toString());

        assertEquals(Confidence.NONE, c1.getConfidence());
        assertEquals(Confidence.HIGH, c2.getConfidence());
        assertEquals(Topic.class, sc1.subtype);
        assertEquals(Topic.class, sc2.subtype);
    }


    @Test
    void testCountCards() {
        assertEquals(30, c1.countCards());
        assertEquals(18, c2.countCards());

        Topic t = new Topic("new topic");
        assertDoesNotThrow(() -> {
            t.add("new q", "new a");
            c1.add(t);
            c2.add(t);
        });

        assertTrue(c1.contains(t));
        assertTrue(c2.contains(t));
        assertEquals(31, c1.countCards());
        assertEquals(19, c2.countCards());

        assertDoesNotThrow(() -> t.add("new q2", "new a 2"));

        assertEquals(32, c1.countCards());
        assertEquals(20, c2.countCards());

        assertDoesNotThrow(() -> {
            c1.remove("t1");
            c2.remove("t1");
        });

        assertFalse(c1.contains("t1"));
        assertFalse(c2.contains("t1"));
        assertEquals(26, c1.countCards());
        assertEquals(14, c2.countCards());
    }

    @Test
    void testGetAllCards() {
        Collection<Card> cards1 = new ArrayList<>();
        Collection<Card> cards2 = new ArrayList<>();

        for (Topic t : map1.values()) {
            cards1.addAll(t.getAllCards());
        }
        assertTrue(cards1.containsAll(c1.getAllCards()));

        for (Topic t : map2.values()) {
            cards2.addAll(t.getAllCards());
        }
        assertTrue(cards2.containsAll(c2.getAllCards()));
    }

    @Override
    @Test
    void testEquals() {
        Course clone = new Course(sc1.getName());
        clone.addAll(sc1.getAll().values());

        testEqualsClone(clone);
    }
}