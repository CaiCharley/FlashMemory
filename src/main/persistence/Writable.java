package persistence;

import org.json.JSONObject;

// allows object to be written to JSON object
// adapted from JsonSerializationDemo @ https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo.git

public interface Writable {
    // EFFECTS: returns this as JSON object
    JSONObject toJson();
}
