package persistence;

import model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

// some tests are adapted from JsonSerializationDemo
class TestJsonReader {
    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        assertThrows(IOException.class, reader::read);
    }

    @Test
    void testReaderEmptyWorkRoom() {
        JsonReader reader = new JsonReader("./data/testEmptySemester.json");
        Semester semester = assertDoesNotThrow(reader::read);

        assertEquals("UBC2020W1", semester.getName());
        assertEquals(Confidence.NONE, semester.getConfidence());
        assertEquals(0, semester.size());
        assertEquals(LocalDate.of(2020, 1, 1), semester.getLastStudyDate());
    }

    @Test
    void testReaderLargeSemester() {
        JsonReader reader = new JsonReader("./data/testLargeSemester.json");
        Semester parsedSemester = assertDoesNotThrow(reader::read);

        //check semester details
        assertEquals("UBC", parsedSemester.getName());
        assertEquals(Confidence.NONE, parsedSemester.getConfidence());
        assertEquals(2, parsedSemester.size());
        assertEquals(LocalDate.of(2020, 1, 1), parsedSemester.getLastStudyDate());
        assertEquals(24, parsedSemester.countCards());

        //check courses details
        Map<String, Course> parsedCourses = parsedSemester.getAll();
        Collection<String> courseNames = new HashSet<>();
        courseNames.add("course0");
        courseNames.add("course1");

        assertEquals(2, parsedCourses.size());
        assertTrue(courseNames.containsAll(parsedCourses.keySet()));
        assertEquals(3, parsedCourses.get("course0").size());
        assertEquals(12, parsedCourses.get("course0").countCards());
        for (Course c : parsedCourses.values()) {
            assertEquals(LocalDate.now(), c.getLastStudyDate());
        }

        //check topics details of first course
        Map<String, Topic> parsedTopics = parsedCourses.get("course0").getAll();
        Collection<String> topicNames = new HashSet<>();
        topicNames.add("t0");
        topicNames.add("t1");
        topicNames.add("t2");

        assertEquals(3, parsedTopics.size());
        assertTrue(topicNames.containsAll(parsedTopics.keySet()));
        assertEquals(4, parsedTopics.get("t0").size());
        assertEquals(4, parsedTopics.get("t0").countCards());
        for (Topic t : parsedTopics.values()) {
            assertEquals(LocalDate.now(), t.getLastStudyDate());
        }

        //check cards details
        Map<String, Card> parsedCards = parsedCourses.get("course0").get("t0").getAll();
        Collection<String> cardQuestions = new HashSet<>();
        Collection<String> cardAnswers = new HashSet<>();
        cardQuestions.add("q0");
        cardQuestions.add("q1");
        cardQuestions.add("q2");
        cardQuestions.add("q3");
        cardAnswers.add("a0");
        cardAnswers.add("a1");
        cardAnswers.add("a2");
        cardAnswers.add("a3");

        assertEquals(4, parsedCards.size());
        assertTrue(cardQuestions.containsAll(parsedCards.keySet()));

        Collection<String> parsedCardAnswers = new HashSet<>();
        for (Card card : parsedCards.values()) {
            assertEquals(LocalDate.now().minusDays(1), card.getLastStudyDate());
            parsedCardAnswers.add(card.getAnswer());
        }
        assertTrue(cardAnswers.containsAll(parsedCardAnswers));
    }
}