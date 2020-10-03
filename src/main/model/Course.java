package model;

import java.util.*;

// a course containing many topics in a map that you can study
public class Course extends StudyMaterial implements StudyableMap<Topic> {
    private String name;
    private Map<String, Topic> topics;

    public Course(String name) {
        this.name = name;
        this.topics = new HashMap<>();
    }

    //requires: topicKey must be in topics
    //modifies: this
    //effects: edits topic's name and updates key of topic
    public Topic editTopicName(String topicKey, String newName) {
        Topic editTopic = topics.remove(topicKey);
        editTopic.setName(newName);
        return topics.put(newName, editTopic);
    }

    //getters and setters
    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    @Override
    //modifies: this
    //effects: adds topic to topics
    public Topic add(Topic topic) {
        return topics.put(topic.getName(), topic);
    }

    @Override
    //modifies: this
    //effects: removes topic with topicName from topics. returns null if TopicName not in topics
    public Topic remove(String topicName) {
        return topics.remove(topicName);
    }

    @Override
    //effects: returns topic with topicName
    public Topic get(String topicName) {
        return topics.get(topicName);
    }

    @Override
    //effects: returns true if topic with topicName is in topics
    public boolean contains(String topicName) {
        return topics.containsKey(topicName);
    }

    @Override
    //effects: returns true if topic is in topics
    public boolean contains(Topic topic) {
        return topics.containsValue(topic);
    }

    @Override
    //effects: returns topics
    public Map<String, Topic> getAll() {
        return topics;
    }

    @Override
    //effects: returns map of topics at confidence level
    public Map<String, Topic> getAtConfidence(Confidence confidence) {
        Map<String, Topic> selectedTopics = new HashMap<>();
        for (Topic t : topics.values()) {
            if (t.getConfidence().compareTo(confidence) == 0) {
                selectedTopics.put(t.getName(), t);
            }
        }
        return selectedTopics;
    }

    @Override
    //effects: returns map of topics at or below confidence level
    public Map<String, Topic> getBelowConfidence(Confidence confidence) {
        Map<String, Topic> selectedTopics = new HashMap<>();
        for (Topic t : topics.values()) {
            if (t.getConfidence().compareTo(confidence) <= 0) {
                selectedTopics.put(t.getName(), t);
            }
        }
        return selectedTopics;
    }

    @Override
    //effects: returns sorted list of topic of priority by compareTo
    public List<Topic> getSortedByPriority() {
        List<Topic> sortedTopics = new ArrayList<>(topics.values());
        Collections.sort(sortedTopics);
        return sortedTopics;
    }

    @Override
    //effects: returns number of topics in this
    public int size() {
        return topics.size();
    }

    @Override
    //effects: returns total number of cards in all topics
    public int countCards() {
        int cards = 0;
        for (Topic t : topics.values()) {
            cards += t.countCards();
        }
        return cards;
    }
}
