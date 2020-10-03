package model;

import java.util.Collection;
import java.util.Map;
import java.util.List;

public interface StudyableMap<E> {
    //modifies: this
    //effects: adds element to this
    E add(E element);

    //modifies: this
    //effects: adds all E in collection to this
    void addAll(Collection<E> collection);

    //modifies: this
    //effects: removes element with key from this
    E remove(String key);

    //effects: gets element in this with key
    E get(String key);

    //effects: returns if this map contains entry with key
    boolean contains(String key);

    //effects: returns if this map contains entry with value
    boolean contains(E value);

    //effects: returns map with all elements in this
    Map<String, E> getAll();

    //effects: returns all elements in this with confidence level
    Map<String, E> getAtConfidence(Confidence confidence);

    //effects: returns all elements in this at or below confidence level
    Map<String, E> getBelowConfidence(Confidence confidence);

    //effects: returns all elements in this sorted by priority to study with compareTo
    List<E> getSortedByPriority();

    //effects: returns number of elements in this
    int size();

    //effects: returns total number of cards in this
    int countCards();
}
