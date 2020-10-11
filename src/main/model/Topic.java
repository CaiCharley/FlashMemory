package model;

import java.util.Collection;

// a topic containing many cards in a map that you can study
public class Topic extends StudyCollection<Card> {

    //effects: sets the topic name and creates empty Card map
    public Topic(String name) {
        super(name);
    }

    //effects: makes Topic with name and confidence and empty card map
    public Topic(String name, Confidence confidence) {
        super(name, confidence);
    }

    //requires: cardQuestion must be in materialMap
    //modifies: this
    //effects: wrapper for editName for Card
    public Card editCardQuestion(String cardQuestion, String newQuestion) {
        return editName(cardQuestion, newQuestion);
    }

    //requires: cardQuestion must be in materialMap
    //modifies: this
    //effects: changes a card's answer with cardQuestion to newAnswer in materialMap
    public Card editCardAnswer(String cardQuestion, String newAnswer) {
        Card editCard = materialMap.get(cardQuestion);
        editCard.setAnswer(newAnswer);
        return editCard;
    }

    //modifies: this
    //effects: adds card with question and answer to materialMap.
    public void add(String question, String answer) {
        add(new Card(question, answer));
    }

    @Override
    //effects: returns total number of cards in this
    public int countCards() {
        return size();
    }

    @Override
    //effects: returns all the cards in this topic as a collection
    public Collection<Card> getAllCards() {
        return getAll().values();
    }
}
