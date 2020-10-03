package model;

import java.util.Calendar;

// A flash card with a question and answer. Extends the StudyMaterial class.
public class Card extends StudyMaterial {
    private String answer;

    //effects: makes card with question and answer with whitespaces trimmed
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
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    //effects: wrapper for getName
    public String getQuestion() {
        return getName();
    }

}
