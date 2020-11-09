package model;

import exceptions.DuplicateElementException;
import exceptions.ModifyException;
import exceptions.NoElementException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.util.*;

// A Map of StudyMaterial, which itself is a StudyMaterial. Contains methods to add/edit/remove elements from Map
public abstract class StudyCollection<M extends StudyMaterial> extends StudyMaterial {
    //invariant: key of value will always be the name of the StudyMaterial
    protected Map<String, M> materialMap;
    public final Class<M> subtype;

    //effects: makes StudyCollection with name
    // taken from https://stackoverflow.com/questions/3403909/get-generic-type-of-class-at-runtime
    public StudyCollection(String name) {
        super(name);
        this.materialMap = new HashMap<>();
        subtype = (Class<M>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    //effects: makes StudyCollection with name and confidence
    // taken from https://stackoverflow.com/questions/3403909/get-generic-type-of-class-at-runtime
    public StudyCollection(String name, Confidence confidence) {
        super(name, confidence);
        this.materialMap = new HashMap<>();
        subtype = (Class<M>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    //modifies: this
    //effects: creates M with name and adds to material map, returns newly added material.
    // Throws DuplicateElementException if name is already in KeySet
    public M add(String name) throws DuplicateElementException {
        return add(name, Confidence.NONE);
    }

    //modifies: this
    //effects: creates M with name and specified confidence and adds to material map, returns newly added material.
    // Throws DuplicateElementException if name is already in KeySet
    public abstract M add(String name, Confidence confidence) throws DuplicateElementException;

    //modifies: this
    //effects: adds material to materialMap with name as key
    protected void add(M material) throws DuplicateElementException {
        if (materialMap.containsKey(material.getName())) {
            throw new DuplicateElementException(this, material.getName());
        }
        materialMap.put(material.getName(), material);
    }

    //modifies: this
    //effects: add unique materials to materialMap
    public void addAll(Collection<M> materials) {
        for (M m : materials) {
            try {
                add(m);
            } catch (DuplicateElementException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    //modifies: this
    //effects: edits material's name in materialMap and updates key of material. returns edited material.
    // Throws DuplicateElementException if newName already exists
    // Throws NoElementException if name to be modifies does not exist
    public M editName(String name, String newName) throws ModifyException {
        if (materialMap.containsKey(newName)) {
            throw new DuplicateElementException(this, newName);
        }

        if (!materialMap.containsKey(name)) {
            throw new NoElementException(this, name);
        }

        M editedMaterial = materialMap.remove(name);
        editedMaterial.setName(newName);
        materialMap.put(newName, editedMaterial);
        return editedMaterial;
    }

    //modifies: this
    //effects: removes material with name from materialMap. returns null if material not in materialMap
    // Throws NoElementException if material to be removed is not in this
    public M remove(String name) throws NoElementException {
        if (!materialMap.containsKey(name)) {
            throw new NoElementException(this, name);
        }
        return materialMap.remove(name);
    }

    //modifies: this
    //effects: removes material from materialMap. Returns material removed, null if not in materialMap
    // Throws NoElementException if material to be removed is not in this
    public M remove(M material) throws NoElementException {
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

    //effects: returns all the cards under in all sub-materials in materialMap
    public abstract Collection<Card> getAllCards();

    @Override
    //effects: gets jsonObject of super (StudyMaterial) and adds study materials in material map to jsonObject
    //         as jsonArray
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        JSONArray jsonMaterialMap = new JSONArray();

        for (M sm : materialMap.values()) {
            jsonMaterialMap.put(sm.toJson());
        }

        json.put("materialMap", jsonMaterialMap);

        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        StudyCollection<?> that = (StudyCollection<?>) o;
        return materialMap.equals(that.materialMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), materialMap, subtype);
    }
}
