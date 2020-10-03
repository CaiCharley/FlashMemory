package model;

// A course containing multiple Topics. Extends StudyCollection
public class Course extends StudyCollection<Topic> {

    //effects: make new Course with name and map of Topic
    public Course(String name) {
        super(name);
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
}
