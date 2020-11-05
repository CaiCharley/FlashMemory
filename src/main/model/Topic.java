package model;

import exceptions.DuplicateElementException;

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
    //effects: changes a card's answer with cardQuestion to newAnswer in materialMap
    public Card editCardAnswer(String cardQuestion, String newAnswer) {
        Card editCard = materialMap.get(cardQuestion);
        editCard.setAnswer(newAnswer);
        return editCard;
    }

    //modifies: this
    //effects: adds Card with question and answer to materialMap.
    // Throws DuplicateElementException if card with question already in this
    public void add(String question, String answer) throws DuplicateElementException {
        add(new Card(question, answer));
    }

    @Override
    //modifies: this
    //effects: adds card with question and blank answer to materialMap.
    // Throws DuplicateElementException if card with question already in this
    public Card add(String name, Confidence confidence) throws DuplicateElementException {
        Card card = new Card(name, "", confidence);
        add(card);
        return card;
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
