package model;

import java.util.ArrayList;
import java.util.Collection;

// A semester containing multiple Courses. Extends StudyCollection
// invariant: studydate can only have length 1, the creation date
public class Semester extends StudyCollection<Course> {

    //effects: make new semester with name and map of Course
    public Semester(String name) {
        super(name);
    }

    @Override
    //modifies: this
    //effects: adds card with question and answer to materialMap.
    public Course add(String name, Confidence confidence) {
        Course course = new Course(name, confidence);
        add(course);
        return course;
    }

    @Override
    //effects: returns total number of cards in this
    public int countCards() {
        int cards = 0;
        for (Course c : materialMap.values()) {
            cards += c.countCards();
        }
        return cards;
    }

    @Override
    public Collection<Card> getAllCards() {
        Collection<Card> cards = new ArrayList<>();

        for (Course c : materialMap.values()) {
            cards.addAll(c.getAllCards());
        }

        return cards;
    }
}
