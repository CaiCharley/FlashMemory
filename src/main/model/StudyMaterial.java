package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

// Represents some material with a name that you need to study. Records how many times you studied it and when.
public abstract class StudyMaterial implements Comparable<StudyMaterial>, Writable {
    private List<LocalDate> studyDates;
    private Confidence confidence;
    private String name;

    //effects: creates new material with name and adds today as the first study date with NONE confidence
    public StudyMaterial(String name) {
        this.studyDates = new ArrayList<>();
        this.studyDates.add(LocalDate.now());
        this.confidence = Confidence.NONE;
        this.name = name;
    }

    //effects: creates new material with name and adds today as the first study date with specified confidence
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
    //effects: adds date studied to end of studyDates and updates confidence
    public void trackStudy(LocalDate date, Confidence confidence) {
        this.studyDates.add(date);
        this.confidence = confidence;
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
    public Confidence getConfidence() {
        return confidence;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    @Override
    //effects: returns how much more confident you are at this compared to mat using Confidence, then time since last
    //         studied. Positive int means you know this better than mat.
    public int compareTo(StudyMaterial mat) {
        if (confidence.compareTo(mat.confidence) != 0) {
            return confidence.compareTo(mat.confidence);
        } else {
            return getLastStudyDate().compareTo(mat.getLastStudyDate());
        }
    }

    @Override
    //effects: returns name as String
    public String toString() {
        return name;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("confidence", confidence);
        json.put("studyDates", studyDatesToJson());

        return json;
    }

    //effects: returns jsonArray with studyDates in JsonArray
    private JSONArray studyDatesToJson() {
        JSONArray jsonArray = new JSONArray();

        for (LocalDate date : studyDates) {
            JSONObject jsonDate = new JSONObject();
            jsonDate.put("year", date.getYear());
            jsonDate.put("month", date.getMonthValue());
            jsonDate.put("day", date.getDayOfMonth());

            jsonArray.put(jsonDate);
        }

        return jsonArray;
    }
}
