package model;

import java.util.HashMap;
import java.util.Map;

// a course containing many topics
public class Course extends StudyMaterial {
    private String name;
    private Map<String, Topic> topics;

    public Course(String name) {
        this.name = name;
        this.topics = new HashMap<>();
    }

    public Topic addTopic(Topic topic) {
        return topics.put(topic.getName(), topic);
    }

    //requires: topicKey must be in topics
    //modifies: this
    //effects: edits topic's name and updates key of topic
    public Topic editTopicName(String topicKey, String newName) {
        Topic editTopic = topics.remove(topicKey);
        editTopic.setName(newName);
        return topics.put(newName, editTopic);
    }

    //modifies: this
    //effects: removes topic from topics. returns null if not in topics
    public Topic removeTopic(String topicKey) {
        return topics.remove(topicKey);
    }

    //effects: returns numbers of topics in this
    public int topicCount() {
        return topics.size();
    }

    //effects: gets topic with topicKey
    public Topic getTopic(String topicKey) {
        return topics.get(topicKey);
    }

    //effects: gets all topics in this
    public Map<String, Topic> getAllTopics() {
        return topics;
    }


    public String getName() {
        return name;
    }
}
