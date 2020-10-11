package model;

import java.util.ArrayList;
import java.util.Collection;

// A semester containing multiple Courses. Extends StudyCollection
public class Semester extends StudyCollection<Course> {

    //effects: make new semester with name and map of Course
    public Semester(String name) {
        super(name);
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
