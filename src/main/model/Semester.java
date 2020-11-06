package model;

import exceptions.DuplicateElementException;

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
    //effects: adds new Course to materialMap.
    // Throws DuplicateElementException if Course already in this
    public Course add(String name, Confidence confidence) throws DuplicateElementException {
        Course course = new Course(name, confidence);
        add(course);
        return course;
    }

    //modifies: this
    //effects: modifies the name of this Semester
    public void editName(String newName) {
        this.setName(newName);
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
