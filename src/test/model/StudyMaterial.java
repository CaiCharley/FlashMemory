package model;

import java.time.LocalDate;
import java.util.ArrayList;

import static java.time.temporal.ChronoUnit.DAYS;

// Represents some material that you need to study. Records how many times you studied it and when.
public class StudyMaterial implements Comparable<StudyMaterial> {
    private ArrayList<LocalDate> studyDates;
    private Confidence confidence;

    //effects: creates new material that hasn't been reviewed yet with no confidence
    public StudyMaterial() {
        this.studyDates = new ArrayList<>();
        this.confidence = Confidence.NONE;
    }

    //effects: creates new material that hasn't been review yet with specified confidence
    public StudyMaterial(Confidence confidence) {
        this.studyDates = new ArrayList<>();
        this.confidence = confidence;
    }

    //modifies: this
    //effects: studies this material and adds date studied to end of studyDates and updates confidence
    public void study(LocalDate date, Confidence confidence) {
        this.studyDates.add(date);
        this.confidence = confidence;
    }

    @Override
    //effects: returns how much more confident you are at this compared to mat using ordinals of Confidence enumeration
    public int compareTo(StudyMaterial mat) {
        return this.confidence.ordinal() - mat.confidence.ordinal();
    }

    //effects: returns the confidence of this
    public Confidence getConfidence() {
        return confidence;
    }

    //effects: returns number of times this has been studied
    public int getTimesStudied() {
        return studyDates.size();
    }

    //effects: returns days since the last time studied to the current date
    public int getDaysSinceStudied() {
        return ((int) DAYS.between(getLastStudyDate(), LocalDate.now()));
    }

    //requires: this has to be studied at least once
    //effects: returns last date this was studied
    public LocalDate getLastStudyDate() {
        int lastIndex = studyDates.size() - 1;
        return studyDates.get(lastIndex);
    }

}
