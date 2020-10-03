package model;

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
}
