package model;

import java.util.*;

// a topic containing many cards in a map with unique question as key
public class Topic extends StudyMaterial {
    private String name;
    private Map<String, Card> cards;

    //effects: sets the topic name and creates empty Card Arraylist
    public Topic(String name) {
        this.name = name;
        this.cards = new HashMap<>();
    }

    //modifies: this
    //effects: adds card to cards
    public Card addCard(Card card) {
        return cards.put(card.getQuestion(), card);
    }

    //modifies: this
    //effects: adds card with q and a to cards
    public Card addCard(String question, String answer) {
        return cards.put(question, new Card(question, answer));
    }

    //requires: card must be in cards
    //modifies: this
    //effects: edits card's question in cards and updates key of card
    public Card editCardQuestion(String cardKey, String newQuestion) {
        Card editCard = cards.remove(cardKey);
        editCard.setQuestion(newQuestion);
        return cards.put(newQuestion, editCard);
    }

    //requires: card must be in cards
    //modifies:this
    //effects: edits card's answer in cards
    public Card editCardAnswer(String cardKey, String newAnswer) {
        Card editCard = cards.remove(cardKey);
        editCard.setAnswer(newAnswer);
        return cards.put(editCard.getQuestion(), editCard);
    }

    //modifies: this
    //effects: removes card with question from cards. returns null if card not in cards
    public Card removeCard(String question) {
        return cards.remove(question);
    }

    //effects: returns number of cards in cards
    public int cardCount() {
        return cards.size();
    }

    //effects: gets card in cards with key
    public Card getCard(String cardKey) {
        return cards.get(cardKey);
    }

    //effects: returns all cards in this
    public Map<String, Card> getAllCards() {
        return cards;
    }

    //effects: returns all cards in this with certain confidence
    public Map<String, Card> getCardsAt(Confidence confidence) {
        Map<String, Card> selectedCards = new HashMap<>();
        for (String key : cards.keySet()) {
            if (cards.get(key).getConfidence().compareTo(confidence) == 0) {
                selectedCards.put(key, cards.get(key));
            }
        }
        return selectedCards;
    }

    //effect: returns cards <= con
    public Map<String, Card> getCardsBelow(Confidence confidence) {
        Map<String, Card> selectedCards = new HashMap<>();
        for (String key : cards.keySet()) {
            if (cards.get(key).getConfidence().compareTo(confidence) <= 0) {
                selectedCards.put(key, cards.get(key));
            }
        }
        return selectedCards;
    }

    //effect: returns an Arraylist of cards sorted by compareTo
    public List<Card> getPrioritySortedCards() {
        List<Card> sortedCards = new ArrayList<>(cards.values());
        Collections.sort(sortedCards);
        return sortedCards;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
