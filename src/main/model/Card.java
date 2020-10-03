package model;

import java.util.Calendar;

// A flash card with a question and answer. Extends the StudyMaterial class.
public class Card extends StudyMaterial {
    private String question;
    private String answer;

    //effects: makes card with question and answer with whitespaces trimmed
    public Card(String question, String answer) {
        this.question = question.trim();
        this.answer = answer.trim();
    }

    //effects: makes card with q and a and specified confidence
    public Card(String question, String answer, Confidence confidence) {
        super(confidence);
        this.question = question.trim();
        this.answer = answer.trim();
    }

    //getters and setters
    void setQuestion(String question) {
        this.question = question;
    }

    void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
