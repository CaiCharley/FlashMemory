package model;

// A flash card with a question and answer. Extends the StudyMaterial class.
public class Card extends StudyMaterial {
    private String question;
    private String answer;

    //effects: makes card with question and answer with whitespaces trimmed
    public Card(String q, String a) {
        this.question = q.trim();
        this.answer = a.trim();
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
