package model;

import java.util.ArrayList;
import java.util.Collection;

// A course containing multiple Topics. Extends StudyCollection
public class Course extends StudyCollection<Topic> {

    //effects: make new Course with name and map of Topic
    public Course(String name) {
        super(name);
    }

    //effects: make new Course with name and confidence with map of Topic
    public Course(String name, Confidence confidence) {
        super(name, confidence);
    }

    @Override
    //modifies: this
    //effects: adds Topic with name in material map
    public void add(String name, Confidence confidence) {
        add(new Topic(name, confidence));
    }

    @Override
    //effects: returns total number of cards in all topics
    public int countCards() {
        int cards = 0;
        for (Topic t : materialMap.values()) {
            cards += t.countCards();
        }
        return cards;
    }

    @Override
    public Collection<Card> getAllCards() {
        Collection<Card> cards = new ArrayList<>();

        for (Topic t : materialMap.values()) {
            cards.addAll(t.getAllCards());
        }

        return cards;
    }
}
