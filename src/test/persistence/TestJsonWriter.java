package persistence;

import model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

// some tests are adapted from JsonSerializationDemo
import static org.junit.jupiter.api.Assertions.*;

class TestJsonWriter {

    @Test
    void testWriterInvalidFile() {
        JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
        assertThrows(IOException.class, writer::open);
    }

    @Test
    void testWriterEmptySemester() {
        Semester semester = new Semester("UBC2020W1");
        List<LocalDate> creationDate = new ArrayList<>();
        creationDate.add(LocalDate.of(2020, 1, 1));
        semester.setStudyDates(creationDate);

        JsonWriter writer = new JsonWriter("./data/testEmptySemester.json");
        assertDoesNotThrow(writer::open);
        writer.write(semester);
        writer.close();

        JsonReader reader = new JsonReader("./data/testEmptySemester.json");
        semester = assertDoesNotThrow(reader::read);
        assertEquals("UBC2020W1", semester.getName());
        assertEquals(Confidence.NONE, semester.getConfidence());
        assertEquals(0, semester.size());
        assertEquals(creationDate.get(0), semester.getLastStudyDate());
    }

    @Test
    void testWriterLargeSemester() {
        Collection<Course> courses =
                TestSemester.makeTestCourses(2, 3, 4, 1).values();
        Semester semester = new Semester("UBC");
        List<LocalDate> creationDate = new ArrayList<>();
        creationDate.add(LocalDate.of(2020, 1, 1));
        semester.setStudyDates(creationDate);
        semester.addAll(courses);

        JsonWriter writer = new JsonWriter("./data/testLargeSemester.json");
        assertDoesNotThrow(writer::open);
        writer.write(semester);
        writer.close();

        JsonReader reader = new JsonReader("./data/testLargeSemester.json");
        Semester parsedSemester = assertDoesNotThrow(reader::read);

        //check semester details
        assertEquals("UBC", parsedSemester.getName());
        assertEquals(Confidence.NONE, parsedSemester.getConfidence());
        assertEquals(2, parsedSemester.size());
        assertEquals(creationDate.get(0), parsedSemester.getLastStudyDate());
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