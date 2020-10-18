package model;

import org.json.JSONObject;

// A flash card with a question and answer
public class Card extends StudyMaterial {
    //invariant: name of this is the question
    private String answer;

    //effects: makes card with question and answer
    public Card(String question, String answer) {
        super(question);
        this.answer = answer;
    }

    //effects: makes card with q and a and specified confidence
    public Card(String question, String answer, Confidence confidence) {
        super(question, confidence);
        this.answer = answer;
    }

    //getters and setters
    protected void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    //effects: wrapper for getName
    public String getQuestion() {
        return getName();
    }

    @Override
    //effects: returns JSONObject of super (StudyMaterial) and adds answer
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("answer", answer);
        return json;
    }
}
