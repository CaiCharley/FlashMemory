package persistence;

import model.Semester;
import model.StudyCollection;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        //TODO: Implement parseSemester
        return null;
    }

    // MODIFIES: sm
    // EFFECTS: parses StudyMaterials from JsonObject and adds them to sc
    private void addStudyMaterials(StudyCollection sc, JSONObject jsonObject) {
        //TODO: Implement addStudyMaterials
    }

    // MODIFIES: sm
    // EFFECTS: parses StudyMaterial from JsonObject and adds them to sc
    private void addStudyMaterial(StudyCollection sc, JSONObject jsonObject) {
        //TODO: Implement addStudyMaterial
    }
}
