package ui.gui;

import model.StudyMaterial;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.Collections;
import java.util.Comparator;

// a TreeNode extending DefaultMutableTreeNode with sorting based on studymaterial
// adapted from
// https://stackoverflow.com/questions/30524936/sorting-the-jtree-alphabetically-in-java-using-hibernate-technology
public class StudyMaterialNode extends DefaultMutableTreeNode {
    public StudyMaterialNode(Object userObject) {
        super(userObject);
    }

    //modifies: this
    //effects: adds child to current node and sort the children
    @Override
    public void add(MutableTreeNode newChild) {
        super.add(newChild);
        sort();
    }

    //modifies: this
    //effects: sort children with comparator based on StudyMaterial
    public void sort() {
        Collections.sort(children, compare());
    }

    //effects: returns comparator based on study material
    private Comparator compare() {
        return new Comparator<DefaultMutableTreeNode>() {
            @Override
            public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2) {
                StudyMaterial sm1 = (StudyMaterial) o1.getUserObject();
                StudyMaterial sm2 = (StudyMaterial) o2.getUserObject();

                return sm1.compareTo(sm2);
            }
        };
    }
}
