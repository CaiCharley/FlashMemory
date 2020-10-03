package model;

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

    //requires: cardQuestion must be in cards
    //modifies: this
    //effects: wrapper for editName for Card
    public Card editCardQuestion(String cardQuestion, String newQuestion) {
        return editName(cardQuestion, newQuestion);
    }

    //requires: cardQuestion must be in cards
    //modifies: this
    //effects: edits card's answer in cards
    public Card editCardAnswer(String cardQuestion, String newAnswer) {
        Card editCard = materialMap.get(cardQuestion);
        editCard.setAnswer(newAnswer);
        return editCard;
    }

    //modifies: this
    //effects: adds card with q and a to cards
    public Card add(String question, String answer) {
        Card c = new Card(question, answer);
        return add(c);
    }

    @Override
    //effects: returns total number of cards in this
    public int countCards() {
        return size();
    }
}
