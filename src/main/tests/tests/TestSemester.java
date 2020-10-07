package tests;

import model.Confidence;
import model.Course;
import model.Semester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


class TestSemester extends TestStudyCollection<Course> {
    Semester s1;
    Semester s2;

    //effects: returns Map of a number of Courses with different confidence, each with a number of Topics with
    // cards with varying confidence
    public static Map<String, Course> makeTestCourses(int numCourses, int numTopics, int numCards, int daysAgo) {
        Map<String, Course> courses = new HashMap<>();
        for (int i = 0; i < numTopics; i++) {
            String topicName = "course" + i;
            Confidence[] confidenceList = new Confidence[]{
                    Confidence.LOW,
                    Confidence.MEDIUM,
                    Confidence.LOW,
                    Confidence.MEDIUM,
                    Confidence.HIGH,
                    Confidence.HIGH};
            Course topic = new Course(topicName);
            topic.trackStudy(confidenceList[i % 6]);
            courses.put(topicName, topic);

            topic.addAll(TestCourse.makeTestTopics(numCourses, numCards, daysAgo).values());
        }
        return courses;
    }

    @BeforeEach
    void setUp() {
        s1 = new Semester("2020W1");
        s2 = new Semester("2019W2");

        //set up as generic StudyCollection
        sc1 = s1;
        sc2 = s2;

        //set up unique StudyMaterials
        highPrioritySM = new Course("Roman History");
        lowPrioritySM = new Course("Algebra");
        highPrioritySM.trackStudy(LocalDate.now().minusDays(7), Confidence.NONE);
        lowPrioritySM.trackStudy(Confidence.HIGH);

        map1 = makeTestCourses(4,5, 6, 1);
        map2 = makeTestCourses(4,3, 6, 1);

        s1.addAll(map1.values());
        s2.addAll(map2.values());
    }

    @Test
    void testCountCards() {
        assertEquals(120, s1.countCards());
        assertEquals(72, s2.countCards());

        s1.get("course1").get("t1").add("new q", "new a");
        s1.get("course1").get("t2").add("new q", "new a");
        s1.get("course2").get("t1").add("new q", "new a");
        s1.get("course3").get("t1").add("new q", "new a");

        assertEquals(124, s1.countCards());
    }
}