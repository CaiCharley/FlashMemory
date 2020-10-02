package tests;

import model.Card;
import model.Confidence;
import model.Course;
import model.Topic;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.Map;

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
            topics.put(topicName, new Topic(topicName));
            Confidence[] confidencelist = new Confidence[]{
                    Confidence.LOW,
                    Confidence.MEDIUM,
                    Confidence.LOW,
                    Confidence.MEDIUM,
                    Confidence.HIGH,
                    Confidence.HIGH};
            for (int j = 0; j < 6; j++) {
                Card c = new Card("q" + j, "a" + j);
                c.trackStudy(confidencelist[j]);
                topics.get(topicName).add(c);
            }
        }
        return topics;
    }

    @BeforeEach
    void setUp() {
        c1 = new Course("MICB 201");
        c2 = new Course ("CPSC 210");

        topics1 = makeTestTopics(5);
        for (Topic t : topics1.values()) {
            c1.add(t);
        }
    }
}