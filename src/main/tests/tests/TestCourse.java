package tests;

import model.Card;
import model.Confidence;
import model.Course;
import model.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TestCourse {
    Course c1;
    Course c2;
    Map<String, Topic> topics1;
    Map<String, Topic> topics2;

    public Map<String, Topic> makeTestTopics(int t) {
        Map<String, Topic> topics = new HashMap<>();
        for (int i = 0; i < t; i++) {
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

            for (int j = 0; j < 6; j++) {
                Card c = new Card("q" + j, "a" + j);
                c.trackStudy(confidenceList[j]);
                topics.get(topicName).add(c);
            }
        }
        return topics;
    }

    @BeforeEach
    void setUp() {
        c1 = new Course("MICB 201");
        c2 = new Course("CPSC 210");

        topics1 = makeTestTopics(5);
        for (Topic t : topics1.values()) {
            c1.add(t);
        }
        topics2 = makeTestTopics(3);
        for (Topic t : topics2.values()) {
            c2.add(t);
        }
    }

    @Test
    void testConstructor() {
        assertEquals("MICB 201", c1.getName());
        assertEquals("CPSC 210", c2.getName());

        assertEquals(topics1, c1.getAll());
        assertEquals(topics2, c2.getAll());
    }

    @Test
    void testEditTopicName() {
        assertTrue(c1.contains("t1"));
        assertEquals(6, c1.get("t1").countCards());
        c1.editName("t1", "t1edited");
        Topic editedTopic = c1.get("t1edited");
        editedTopic.add("newq", "newa");

        assertFalse(c1.contains("t1"));
        assertTrue(c1.contains("t1edited"));
        assertEquals(5, c1.size());
        assertEquals("t1edited", c1.get("t1edited").getName());
        assertEquals(7, c1.get("t1edited").countCards());
        assertTrue(c1.get("t1edited").contains("newq"));
    }

    @Test
    void testCountCards() {
        assertEquals(30, c1.countCards());
        assertEquals(18, c2.countCards());

        Topic t = new Topic("new topic");
        t.add("new q", "new a");

        c1.add(t);
        c2.add(t);

        assertTrue(c1.contains(t));
        assertTrue(c2.contains(t));
        assertEquals(31, c1.countCards());
        assertEquals(19, c2.countCards());

        t.add("new q2", "new a 2");

        assertEquals(32, c1.countCards());
        assertEquals(20, c2.countCards());

        c1.remove("t1");
        c2.remove("t1");

        assertFalse(c1.contains("t1"));
        assertFalse(c2.contains("t1"));
        assertEquals(26, c1.countCards());
        assertEquals(14, c2.countCards());
    }

    @Test
    void testGetAtConfidence() {
        Map<String, Topic> c1Low = new HashMap<>();
        c1Low.put("t0", topics1.get("t0"));
        c1Low.put("t2", topics1.get("t2"));

        Map<String, Topic> c1Med = new HashMap<>();
        c1Med.put("t1", topics1.get("t1"));
        c1Med.put("t3", topics1.get("t3"));

        Map<String, Topic> c1High = new HashMap<>();
        c1High.put("t4", topics1.get("t4"));

        assertEquals(c1Low, c1.getAtConfidence(Confidence.LOW));
        assertEquals(c1Med, c1.getAtConfidence(Confidence.MEDIUM));
        assertEquals(c1High, c1.getAtConfidence(Confidence.HIGH));

        Topic topic = new Topic("t5");
        topic.trackStudy(Confidence.HIGH);
        c1High.put(topic.getName(), topic);
        c1.add(topic);

        assertEquals(c1High, c1.getAtConfidence(Confidence.HIGH));
    }

    @Test
    void testGetBelowConfidence() {
        Map<String, Topic> belowLow = new HashMap<>();
        belowLow.put("t0", topics1.get("t0"));
        belowLow.put("t2", topics1.get("t2"));

        Map<String, Topic> belowMed = new HashMap<>();
        belowMed.put("t1", topics1.get("t1"));
        belowMed.put("t3", topics1.get("t3"));
        belowMed.put("t0", topics1.get("t0"));
        belowMed.put("t2", topics1.get("t2"));

        Map<String, Topic> belowHigh = new HashMap<>();
        belowHigh.put("t4", topics1.get("t4"));
        belowHigh.put("t1", topics1.get("t1"));
        belowHigh.put("t3", topics1.get("t3"));
        belowHigh.put("t0", topics1.get("t0"));
        belowHigh.put("t2", topics1.get("t2"));

        assertEquals(belowLow, c1.getBelowConfidence(Confidence.LOW));
        assertEquals(belowMed, c1.getBelowConfidence(Confidence.MEDIUM));
        assertEquals(belowHigh, c1.getBelowConfidence(Confidence.HIGH));

        Topic topic = new Topic("t5");
        Map<String, Topic> belowNone = new HashMap<>();
        belowNone.put(topic.getName(), topic);
        belowLow.put(topic.getName(), topic);
        belowMed.put(topic.getName(), topic);
        belowHigh.put(topic.getName(), topic);
        c1.add(topic);

        assertEquals(belowNone, c1.getBelowConfidence(Confidence.NONE));
        assertEquals(belowLow, c1.getBelowConfidence(Confidence.LOW));
        assertEquals(belowMed, c1.getBelowConfidence(Confidence.MEDIUM));
        assertEquals(belowHigh, c1.getBelowConfidence(Confidence.HIGH));
    }

    @Test
    void testPrioritySorting() {
        List<Topic> sorted1 = new ArrayList<>(topics1.values());
        Collections.sort(sorted1);

        assertEquals(sorted1, c1.getSortedByPriority());

        Topic highPriority = new Topic("high");
        highPriority.trackStudy(LocalDate.now().minusDays(1), Confidence.NONE);
        Topic lowPriority = new Topic("low");
        lowPriority.trackStudy(LocalDate.now().plusDays(1), Confidence.HIGH);
        sorted1.add(0, highPriority);
        sorted1.add(lowPriority);

        c1.add(highPriority);
        c1.add(lowPriority);

        assertEquals(sorted1, c1.getSortedByPriority());
    }
}