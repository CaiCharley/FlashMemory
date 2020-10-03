package model;

import java.util.*;

public abstract class StudyCollection<M extends StudyMaterial> extends StudyMaterial {
    protected Map<String, M> materialMap;

    //effects: makes StudyCollection with name as id
    public StudyCollection(String name) {
        super(name);
        this.materialMap = new HashMap<>();
    }

    //effects: makes StudyCollection with name as id and confidence
    public StudyCollection(String name, Confidence confidence) {
        super(name, confidence);
        this.materialMap = new HashMap<>();
    }

    //modifies: this
    //effects: adds material to materialMap
    public M add(M material) {
        return materialMap.put(material.getName(), material);
    }

    //modifies: this
    //effects: add materials to materialMap
    public void addAll(Collection<M> materials) {
        for (M m : materials) {
            add(m);
        }
    }

    //requires: name must be in materialMap's keys
    //modifies: this
    //effects: edits material's name in materialMap and updates key of material
    public M editName(String name, String newName) {
        M editedMaterial = materialMap.remove(name);
        editedMaterial.setName(newName);
        return materialMap.put(newName, editedMaterial);
    }

    //modifies: this
    //effects: removes material with name from materialMap. returns null if material not in materialMap
    public M remove(String name) {
        return materialMap.remove(name);
    }

    //effects: gets material in materialMap with name as key
    public M get(String name) {
        return materialMap.get(name);
    }

    //effects: return true if name is a key of materialMap
    public boolean contains(String name) {
        return materialMap.containsKey((name));
    }

    //effects: return true if material is in materialMap
    public boolean contains(M material) {
        return materialMap.containsValue((material));
    }

    //effects: returns materialMap
    public Map<String, M> getAll() {
        return materialMap;
    }

    //effects: returns all materials in materialMap with certain confidence
    public Map<String, M> getAtConfidence(Confidence confidence) {
        Map<String, M> selectedMaterials = new HashMap<>();
        for (M m : materialMap.values()) {
            if (m.getConfidence().compareTo(confidence) == 0) {
                selectedMaterials.put(m.getName(), m);
            }
        }
        return selectedMaterials;
    }

    //effect: returns all materials at or below confidence in materialMap
    public Map<String, M> getBelowConfidence(Confidence confidence) {
        Map<String, M> selectedMaterials = new HashMap<>();
        for (M m : materialMap.values()) {
            if (m.getConfidence().compareTo(confidence) <= 0) {
                selectedMaterials.put(m.getName(), m);
            }
        }
        return selectedMaterials;
    }

    //effect: returns an Arraylist of materials sorted by studying priority
    public List<M> getSortedByPriority() {
        List<M> sortedCards = new ArrayList<>(materialMap.values());
        Collections.sort(sortedCards);
        return sortedCards;
    }

    //effects: returns number of materials in materialMap
    public int size() {
        return materialMap.size();
    }

    //effects: returns number of cards in this
    public abstract int countCards();
}
