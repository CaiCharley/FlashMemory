package model;

import java.util.*;

public class Semester implements StudyableMap<Course> {
    private String name;
    private Map<String, Course> courses;

    //effects: make new semester with name and map of courses
    public Semester(String name) {
        this.name = name;
        courses = new HashMap<>();
    }

    //requires: courseKey must be in courses
    //modifies: this
    //effects: edits Course of courseKey's name and updates key of Course
    public Course editCourseName(String courseKey, String newName) {
        Course editCourse = courses.remove(courseKey);
        editCourse.setName(newName);
        return courses.put(newName, editCourse);
    }

    //getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    //modifies: this
    //effects: adds course to courses
    public Course add(Course course) {
        return courses.put(course.getName(), course);
    }

    @Override
    //modifies: this
    //effects: adds courses to courses in this
    public void addAll(Collection<Course> courses) {
        for (Course c : courses) {
            add(c);
        }
    }

    @Override
    //modifies: this
    //effects: removes course with courseName from courses
    public Course remove(String courseName) {
        return courses.remove(courseName);
    }

    @Override
    //effects: returns course with courseName
    public Course get(String courseName) {
        return  courses.get(courseName);
    }

    @Override
    //effects: returns true if courseName exists in courses keys
    public boolean contains(String courseName) {
        return courses.containsKey(courseName);
    }

    @Override
    //effects: returns true if course exists in courses
    public boolean contains(Course course) {
        return courses.containsValue(course);
    }

    @Override
    //effects: returns all courses in this as map
    public Map<String, Course> getAll() {
        return courses;
    }

    @Override
    public Map<String, Course> getAtConfidence(Confidence confidence) {
        Map<String, Course> selectedCourses = new HashMap<>();
        for (Course c : courses.values()) {
            if (c.getConfidence().compareTo(confidence) == 0) {
                selectedCourses.put(c.getName(), c);
            }
        }
        return selectedCourses;
    }

    @Override
    public Map<String, Course> getBelowConfidence(Confidence confidence) {
        Map<String, Course> selectedCourses = new HashMap<>();
        for (Course c : courses.values()) {
            if (c.getConfidence().compareTo(confidence) <= 0) {
                selectedCourses.put(c.getName(), c);
            }
        }
        return selectedCourses;
    }

    @Override
    public List<Course> getSortedByPriority() {
        List<Course> sortedCourses = new ArrayList<>(courses.values());
        Collections.sort(sortedCourses);
        return sortedCourses;
    }

    @Override
    public int size() {
        return courses.size();
    }

    @Override
    public int countCards() {
        int cards = 0;
        for (Course c : courses.values()) {
            cards += c.countCards();
        }
        return cards;
    }
}
