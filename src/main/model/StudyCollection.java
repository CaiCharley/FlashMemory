package model;

import org.json.JSONObject;

import java.util.*;

// A Map of StudyMaterial, which itself is a StudyMaterial. Contains methods to add/edit/remove elements from Map
public abstract class StudyCollection<M extends StudyMaterial> extends StudyMaterial {
    //invariant: key of value will always be the name of the StudyMaterial
    protected Map<String, M> materialMap;

    //effects: makes StudyCollection with name
    public StudyCollection(String name) {
        super(name);
        this.materialMap = new HashMap<>();
    }

    //effects: makes StudyCollection with name and confidence
    public StudyCollection(String name, Confidence confidence) {
        super(name, confidence);
        this.materialMap = new HashMap<>();
    }

    //requires: material must not already be in map
    //modifies: this
    //effects: adds material to materialMap with name as key
    public void add(M material) {
        materialMap.put(material.getName(), material);
    }

    //modifies: this
    //effects: add materials to materialMap
    public void addAll(Collection<M> materials) {
        for (M m : materials) {
            add(m);
        }
    }

    //requires: name must be in materialMap's keys and new name must not already exist
    //modifies: this
    //effects: edits material's name in materialMap and updates key of material. returns edited material
    public M editName(String name, String newName) {
        M editedMaterial = materialMap.remove(name);
        editedMaterial.setName(newName);
        materialMap.put(newName, editedMaterial);
        return editedMaterial;
    }

    //modifies: this
    //effects: removes material with name from materialMap. returns null if material not in materialMap
    public M remove(String name) {
        return materialMap.remove(name);
    }

    //modifies: this
    //effects: removes material from materialMap. Returns material removed, null if not in materialMap
    public M remove(M material) {
        return remove(material.getName());
    }

    //effects: gets material in materialMap with name as key. Returns null if name not in materialMap.
    public M get(String name) {
        return materialMap.get(name);
    }

    //effects: returns materialMap
    public Map<String, M> getAll() {
        return materialMap;
    }

    //effects: return true if name is a key of materialMap
    public boolean contains(String name) {
        return materialMap.containsKey((name));
    }

    //effects: return true if material is in materialMap
    public boolean contains(M material) {
        return materialMap.containsValue((material));
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

    //effect: returns a List of materials sorted by studying priority
    public List<M> getSortedByPriority() {
        List<M> sortedCards = new ArrayList<>(materialMap.values());
        Collections.sort(sortedCards);
        return sortedCards;
    }

    //effects: returns number of materials in materialMap
    public int size() {
        return materialMap.size();
    }

    //effects: returns number of cards in all sub-materials in materialMap
    public abstract int countCards();

    //effects: returns all the cards under in all submaterials in materialMap
    public abstract Collection<Card> getAllCards();

    @Override
    public JSONObject toJson() {
        return super.toJson();
    }
}
