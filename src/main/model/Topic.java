package model;

import java.util.*;

// a topic containing many cards in a map that you can study
public class Topic extends StudyMaterial implements StudyableMap<Card> {
    private String name;
    private Map<String, Card> cards;

    //effects: sets the topic name and creates empty Card Arraylist
    public Topic(String name) {
        this.name = name;
        this.cards = new HashMap<>();
    }

    //requires: cardQuestion must be in cards
    //modifies: this
    //effects: edits card's question in cards and updates key of card
    public Card editCardQuestion(String cardQuestion, String newQuestion) {
        Card editCard = cards.remove(cardQuestion);
        editCard.setQuestion(newQuestion);
        return cards.put(newQuestion, editCard);
    }

    //requires: cardKey must be in cards
    //modifies: this
    //effects: edits card's answer in cards
    public Card editCardAnswer(String cardQuestion, String newAnswer) {
        Card editCard = cards.remove(cardQuestion);
        editCard.setAnswer(newAnswer);
        return cards.put(editCard.getQuestion(), editCard);
    }

    //getters and setters
    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    @Override
    //modifies: this
    //effects: adds card to cards
    public Card add(Card card) {
        return cards.put(card.getQuestion(), card);
    }

    //modifies: this
    //effects: adds card with q and a to cards
    public Card add(String question, String answer) {
        return cards.put(question, new Card(question, answer));
    }

    @Override
    //modifies: this
    //effects: add cards to cards in this
    public void addAll(Collection<Card> cards) {
        for (Card c : cards) {
            add(c);
        }
    }

    @Override
    //modifies: this
    //effects: removes card with question from cards. returns null if card not in cards
    public Card remove(String question) {
        return cards.remove(question);
    }

    @Override
    //effects: gets card in cards with key
    public Card get(String cardKey) {
        return cards.get(cardKey);
    }

    @Override
    //effects: return true if cardQuestion is in cards
    public boolean contains(String cardQuestion) {
        return cards.containsKey((cardQuestion));
    }

    @Override
    //effects: return true if card is in cards
    public boolean contains(Card card) {
        return cards.containsValue((card));
    }

    @Override
    //effects: returns all cards in this
    public Map<String, Card> getAll() {
        return cards;
    }

    @Override
    //effects: returns all cards in this with certain confidence
    public Map<String, Card> getAtConfidence(Confidence confidence) {
        Map<String, Card> selectedCards = new HashMap<>();
        for (Card c : cards.values()) {
            if (c.getConfidence().compareTo(confidence) == 0) {
                selectedCards.put(c.getQuestion(), c);
            }
        }
        return selectedCards;
    }

    @Override
    //effect: returns cards at or below confidence
    public Map<String, Card> getBelowConfidence(Confidence confidence) {
        Map<String, Card> selectedCards = new HashMap<>();
        for (Card c : cards.values()) {
            if (c.getConfidence().compareTo(confidence) <= 0) {
                selectedCards.put(c.getQuestion(), c);
            }
        }
        return selectedCards;
    }

    @Override
    //effect: returns an Arraylist of cards sorted by compareTo
    public List<Card> getSortedByPriority() {
        List<Card> sortedCards = new ArrayList<>(cards.values());
        Collections.sort(sortedCards);
        return sortedCards;
    }

    @Override
    //effects: returns number of cards in cards
    public int size() {
        return cards.size();
    }

    @Override
    //effects: returns number of cards in topics
    public int countCards() {
        return cards.size();
    }
}
