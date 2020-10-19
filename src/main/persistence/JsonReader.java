package persistence;

import model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// Represents a reader that reads workroom from JSON data stored in file
// adapted from JsonSerializationDemo

public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads Semester from file and returns it;
    // throws IOException if an error occurs reading data from file
    public Semester read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseSemester(jsonObject);
    }


    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses Semester from JSON object and returns it
    private Semester parseSemester(JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        Semester semester = new Semester(name);
        addStudyDates(semester, jsonObject);

        JSONArray jsonCourses = jsonObject.getJSONArray("materialMap");
        addStudyMaterials(semester, jsonCourses);
        return semester;
    }


    // MODIFIES: sc
    // EFFECTS: parses StudyMaterials from JsonObject and adds them to sc
    private void addStudyMaterials(StudyCollection sc, JSONArray jsonMaterialMap) {
        for (Object json : jsonMaterialMap) {
            JSONObject nextJsonSM = (JSONObject) json;
            addStudyMaterial(sc, nextJsonSM);
        }
    }

    // MODIFIES: sc
    // EFFECTS: parses StudyMaterial from JsonObject and adds them to sc
    private void addStudyMaterial(StudyCollection sc, JSONObject jsonStudyMaterial) {
        String name = jsonStudyMaterial.getString("name");
        Confidence confidence = Confidence.valueOf(jsonStudyMaterial.getString("confidence"));
        sc.add(name, confidence);
        StudyMaterial newStudyMaterial = sc.get(name);
        addStudyDates(newStudyMaterial, jsonStudyMaterial);

        if (jsonStudyMaterial.has("materialMap")) {
            // if it has materialMap, it must be also another StudyCollection
            JSONArray jsonStudyMaterials = jsonStudyMaterial.getJSONArray("materialMap");
            addStudyMaterials((StudyCollection) newStudyMaterial, jsonStudyMaterials);
        } else {
            //base case. If it doesn't have materialMap, Study Material is a card. StudyCollection is a Topic
            String answer = jsonStudyMaterial.getString("answer");

            Card newCard = (Card) newStudyMaterial;
            newCard.setAnswer(answer);
        }
    }

    // MODIFIES: sm
    // EFFECTS: parses StudyMaterial Fields from JsonObject and adds them to sm
    private void addStudyDates(StudyMaterial sm, JSONObject jsonObject) {
        List<LocalDate> studyDates = new ArrayList<>();
        JSONArray jsonDates = jsonObject.getJSONArray("studyDates");
        for (Object jsonDate : jsonDates) {
            JSONObject nextJsonDate = (JSONObject) jsonDate;
            int year = nextJsonDate.getInt("year");
            int month = nextJsonDate.getInt("month");
            int day = nextJsonDate.getInt("day");

            studyDates.add(LocalDate.of(year, month, day));
        }

        sm.setStudyDates(studyDates);
    }
}
