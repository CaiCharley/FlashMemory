package model;

import java.time.LocalDate;
import java.util.ArrayList;

import static java.time.temporal.ChronoUnit.DAYS;

// Represents some material that you need to study. Records how many times you studied it and when.
public abstract class StudyMaterial implements Comparable<StudyMaterial> {
    private ArrayList<LocalDate> studyDates;
    private Confidence confidence;
    private String name;

    //effects: creates new material with id that hasn't been reviewed yet with no confidence
    public StudyMaterial(String name) {
        this.studyDates = new ArrayList<>();
        this.studyDates.add(LocalDate.now());
        this.confidence = Confidence.NONE;
        this.name = name;
    }

    //effects: creates new material with id that hasn't been review yet with specified confidence
    public StudyMaterial(String name, Confidence confidence) {
        this.studyDates = new ArrayList<>();
        this.studyDates.add(LocalDate.now());
        this.confidence = confidence;
        this.name = name;
    }

    //modifies: this
    //effects: adds today as last date studied to end of studyDates and updates confidence
    public void trackStudy(Confidence confidence) {
        this.studyDates.add(LocalDate.now());
        this.confidence = confidence;
    }

    //modifies: this
    //effects: studies this material and adds date studied to end of studyDates and updates confidence
    public void trackStudy(LocalDate date, Confidence confidence) {
        this.studyDates.add(date);
        this.confidence = confidence;
    }


    //effects: returns the confidence of this
    public Confidence getConfidence() {
        return confidence;
    }

    //effects: returns number of times this has been studied (excluding creation date)
    public int getTimesStudied() {
        return studyDates.size() - 1;
    }

    //effects: returns days since the last time studied to the current date
    public int getDaysSinceStudied() {
        return ((int) DAYS.between(getLastStudyDate(), LocalDate.now()));
    }

    //effects: returns last date this was studied
    public LocalDate getLastStudyDate() {
        int lastIndex = studyDates.size() - 1;
        return studyDates.get(lastIndex);
    }

    //getters and setters
    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    @Override
    //effects: returns how much more confident you are at this compared to mat using Confidence, then time since last
    //  studied. Positive int means you know this better than mat.
    public int compareTo(StudyMaterial mat) {
        if (confidence.compareTo(mat.confidence) != 0) {
            return confidence.compareTo(mat.confidence);
        } else {
            return getLastStudyDate().compareTo(mat.getLastStudyDate());
        }
    }

    @Override
    //effects: returns name
    public String toString() {
        return name;
    }
}
