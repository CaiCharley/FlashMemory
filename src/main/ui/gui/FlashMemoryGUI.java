package ui.gui;

import exceptions.DuplicateElementException;
import exceptions.InvalidPointerException;
import exceptions.ModifyException;
import exceptions.NoElementException;
import model.*;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


// A JFrame GUI interface for FlashMemory
public class FlashMemoryGUI extends JFrame {
    // Application Fields
    private static final String JSON_DIRECTORY = "./data/";
    private static final String APP_NAME = "Flash Memory";

    private Semester semester;
    private StudyMaterial pointer;
    private StudyMaterialNode currentNode;
    private DefaultTreeModel semesterModel;

    // JFrame Fields
    private JPanel mainPanel;
    private JButton changeSemesterButton;
    private JButton saveSemesterButton;
    private JButton addButton;
    private JButton removeButton;
    private JButton editNameButton;
    private JButton studyButton;
    private JButton testButton;
    private JLabel semesterNameLabel;
    private JLabel pointerLabel;
    private JLabel confidenceLabel;
    private JLabel studyDateLabel;
    private JLabel cardCountLabel;
    private JLabel daysSinceStudyLabel;
    private JLabel timesStudiedLabel;
    private JTree semesterTree;
    private JList dateList;

    //effects: makes new FlashMemoryGUI with title and initializes semester and JFrame elements.
    // Quits if user doesn't load a semester
    public FlashMemoryGUI(String title) {
        super(title);

        setSemester();
        if (semester == null) {
            System.exit(0);
        }
        setupJFrame();

    }

    //modifies: this
    //effects: initializes JFrame fields and configuration from form. Implements behaviour to prompt saving before quit
    private void setupJFrame() {
        setupButtons();
        setupJTree();
        refreshPointer();

        add(mainPanel);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                terminate();
            }
        });
        pack();
        centreOnScreen();
        setVisible(true);
    }

    //modifies: this
    //effects: updates semesterTree and configures variables
    private void setupJTree() {
        changeJTree();
        semesterTree.addTreeSelectionListener(e -> {
            currentNode = (StudyMaterialNode) semesterTree.getLastSelectedPathComponent();

            refreshPointer();
        });
    }

    //modifies: this
    //effects: updates the model of semesterTree to reflect a new loaded hierarchy in semester.
    private void changeJTree() {
        StudyMaterialNode root = new StudyMaterialNode(semester);
        addStudyMaterialsToTreeNode(semester.getSortedByPriority(), root);
        semesterModel = new DefaultTreeModel(root);
        semesterTree.setModel(semesterModel);
    }

    //modifies: node
    //effects: adds studyMaterials in materials as child nodes of node.
    // calls recursively to add items within each material if material is a StudyCollection
    private void addStudyMaterialsToTreeNode(List<?> materials, StudyMaterialNode node) {
        for (Object sm : materials) {
            StudyMaterialNode newNode = new StudyMaterialNode(sm);
            if (sm instanceof StudyCollection<?>) {
                StudyCollection<?> sc = (StudyCollection<?>) sm;
                addStudyMaterialsToTreeNode(sc.getSortedByPriority(), newNode);
            }
            node.add(newNode);
        }
    }

    //modifies: this
    //effects: updates fields in this to match new pointer
    private void refreshPointer() {
        if (currentNode != null) {
            pointer = (StudyMaterial) currentNode.getUserObject();
        } else {
            pointer = null;
        }

        if (pointer != null) {
            if (pointer instanceof StudyCollection<?>) {
                cardCountLabel.setText("Contained Cards: " + ((StudyCollection<?>) pointer).countCards());
            } else {
                cardCountLabel.setText(null);
            }
            pointerLabel.setText(pointer.getName());
            confidenceLabel.setText("Confidence: " + pointer.getConfidence());
            daysSinceStudyLabel.setText(String.format("Days Since Studied: %d", pointer.getDaysSinceStudied()));
            timesStudiedLabel.setText(String.format("Times Studied: %d", pointer.getTimesStudied()));
            dateList.setListData(pointer.getStudyDates().toArray(new LocalDate[0]));
            studyDateLabel.setVisible(true);
        } else {
            pointerNull();
        }
        toggleEditButtonEnable();
    }

    //modifies: this
    //effects: clears JComponents if pointer is null
    private void pointerNull() {
        pointerLabel.setText(null);
        confidenceLabel.setText(null);
        daysSinceStudyLabel.setText(null);
        timesStudiedLabel.setText(null);
        cardCountLabel.setText(null);
        studyDateLabel.setVisible(false);
        dateList.setListData(new String[0]);
    }

    //modifies: this
    //effects: enables/disables buttons depending on pointer
    private void toggleEditButtonEnable() {
        if (pointer instanceof Card) {
            setButtonsEnable(false, true, true, true, "Edit Question");
        } else if (pointer instanceof Semester) {
            setButtonsEnable(true, false, true, true, "Edit Name");
        } else if (pointer != null) {
            setButtonsEnable(true, true, true, true, "Edit Name");
        } else {
            setButtonsEnable(false, false, false, false, "Edit Name");
        }
    }

    //modifies: this
    //effects: sets buttons to modify semester
    private void setButtonsEnable(boolean add, boolean remove, boolean edit, boolean study, String editLabel) {
        addButton.setEnabled(add);
        removeButton.setEnabled(remove);
        editNameButton.setEnabled(edit);
        studyButton.setEnabled(study);
        editNameButton.setText(editLabel);
    }

    //modifies: this
    //effects: initializes buttons in JFrame with actionListeners and other parameters
    private void setupButtons() {
        changeSemesterButton.addActionListener(e -> setSemester());
        saveSemesterButton.addActionListener(e -> saveSemester());

        addButton.addActionListener(e -> addStudyMaterial());
        removeButton.addActionListener(e -> removeStudyMaterial());
        editNameButton.addActionListener(e -> editStudyMaterial());
        studyButton.addActionListener(e -> studyStudyMaterial());
    }

    //modifies: this
    //effects: adds studymaterial to pointer with name defined by user
    // Throws InvalidPointerException if method invoked while pointer is Card
    private void addStudyMaterial() {
        if (pointer instanceof Card) {
            throw new InvalidPointerException("You cannot add items to a Card");
        }

        StudyCollection<?> sc = (StudyCollection<?>) pointer;
        String subMaterial = sc.subtype.getSimpleName();
        String message = "Enter a name for your new " + subMaterial;
        if (sc instanceof Topic) {
            message = "Enter a question for your new " + subMaterial;
        }

        String name = getStringPopup(message, "Create " + subMaterial, "New " + subMaterial);
        if (name != null) {
            try {
                StudyMaterial newMaterial = sc.add(name);
                StudyMaterialNode newNode = new StudyMaterialNode(newMaterial);
                currentNode.add(newNode);
                semesterModel.reload(currentNode);
            } catch (DuplicateElementException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    //modifies: this
    //effects: removes pointer from parent study collection
    // Throws InvalidPointerException if invoked while pointer is Semester
    private void removeStudyMaterial() {
        if (pointer instanceof Semester) {
            throw new InvalidPointerException("Cannot remove the root semester");
        }
        if (confirmRemove()) {
            try {
                StudyMaterialNode parent = (StudyMaterialNode) currentNode.getParent();
                StudyCollection<?> parentSC = (StudyCollection<?>) parent.getUserObject();
                parentSC.remove(pointer.getName());
                semesterModel.removeNodeFromParent(currentNode);
                refreshPointer();
            } catch (NoElementException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    //effects: ask the user to confirm removal of element
    private boolean confirmRemove() {
        int selection = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove " + pointer, "Confirmation",
                JOptionPane.YES_NO_OPTION);
        return selection == 0;
    }

    //modifies: this
    //effects: asks user for confidence and records studying for pointer
    private void studyStudyMaterial() {
        String[] options = {"None", "Low", "Medium", "High", "Cancel"};
        int n = JOptionPane.showOptionDialog(this,
                "How confident are you with " + pointer,
                "Save Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);


        if (n != 4) {
            pointer.trackStudy(Confidence.values()[n]);

            StudyMaterialNode parent = (StudyMaterialNode) currentNode.getParent();
            parent.sort();
            semesterModel.reload(parent);
            refreshPointer();
        }
    }

    //modifies: this
    //effects: edits the name of pointer
    private void editStudyMaterial() {
        String newName = getStringPopup(
                "Edit selected " + pointer.getClass().getSimpleName(), "Edit", pointer.getName()
        );
        if (newName != null) {
            if (pointer instanceof Semester) {
                semester.editName(newName);
            } else {
                StudyMaterialNode parent = (StudyMaterialNode) currentNode.getParent();
                StudyCollection<?> parentSC = (StudyCollection<?>) parent.getUserObject();
                try {
                    parentSC.editName(pointer.getName(), newName);
                } catch (DuplicateElementException e) {
                    JOptionPane.showMessageDialog(this, "That already exists in " + parentSC);
                } catch (ModifyException e) {
                    JOptionPane.showMessageDialog(this, "Can't find " + pointer + " in " + parentSC);
                }
            }
            semesterModel.nodeChanged(currentNode);
            refreshPointer();
        }
    }

    //modifies: this
    //effects: prompts user to save semester before exiting application. Does nothing if cancelled
    private void terminate() {
        Object[] options = {"Save", "Don't Save", "Cancel"};
        int n = JOptionPane.showOptionDialog(this,
                "Would you like to save any changes before quitting?",
                "Save Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (n == 0) {
            saveSemester();
        } else if (n == 2) {
            return;
        }
        dispose();
        System.exit(0);
    }

    //effects: saves semester to file
    // adapted from JsonSerializationDemo @ https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo.git
    private void saveSemester() {
        String filePath = JSON_DIRECTORY + semester.getName() + ".json";
        JsonWriter writer = new JsonWriter(filePath);
        try {
            writer.open();
            writer.write(semester);
            writer.close();
            JOptionPane.showMessageDialog(this, "Saved " + semester.getName());
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Unable to write to file:\n" + filePath);
        }
    }

    //modifies: this
    //effects:  frame is centred on desktop
    //adapted from SnakeGame
    private void centreOnScreen() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2, (screen.height - getHeight()) / 2);
    }

    //effects: asks user if they want to load semester from file or make new semester.
    // adapted from https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html#button
    private void setSemester() {
        Object[] options = {"Create Semester", "Load Semester"};
        int n = JOptionPane.showOptionDialog(this,
                "Would you like to create a new semester or load an existing semester?",
                "Set Semester",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (n == 0) {
            makeNewSemester();
        } else if (n == 1) {
            loadSemester();
        }

        if (semester != null) {
            setTitle(APP_NAME + " | " + semester.getName());
            semesterNameLabel.setText(semester.getName());
            changeJTree();
        }
    }

    //modifies: this
    //effects: asks user for new semester name and makes new semester with name
    //adapted from https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html#button
    private void makeNewSemester() {
        String semesterName = getStringPopup("Enter a name for your new Semester", "Create Semester", "My Semester");
        if (semesterName != null) {
            semester = new Semester(semesterName);
        }
    }

    //modifies: this
    //effects: sets semester by reading from json files in JSON_DIRECTORY, unless user picks cancel.
    // Notifies user if semester is unable to be read
    private void loadSemester() {
        // FileNameFilter lambda adapted from https://www.journaldev.com/845/java-filenamefilter-example
        File f = new File(JSON_DIRECTORY);
        String[] semesterPaths = f.list((d, s) -> s.toLowerCase().endsWith(".json"));

        String selectedSemester =
                (String) JOptionPane.showInputDialog(this, "Select the Semester you want to load.",
                        "Load Semester", JOptionPane.QUESTION_MESSAGE, null, semesterPaths, 0);

        if (selectedSemester != null) {
            String filepath = JSON_DIRECTORY + selectedSemester;
            JsonReader reader = new JsonReader(filepath);
            try {
                semester = reader.read();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Unable to load semester:\n" + filepath);
            }
        }
    }

    //effects: returns pretty string entered by user with popup with specified message, title, and default value
    private String getStringPopup(String message, String title, String defaultValue) {
        String input = (String) JOptionPane.showInputDialog(
                this,
                message,
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                defaultValue);
        if (input == null) {
            return null;
        } else {
            return makePrettyText(input);
        }
    }

    //effects: removes white space and quotation marks around s
    // adapted from FitLifeGymKiosk @ https://github.com/UBCx-Software-Construction/long-form-problem-starters.git
    private String makePrettyText(String s) {
        s = s.trim();
        s = s.replaceAll("\"|'", "");
        return s;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Unable to set LookAndFeel");
        }

        SwingUtilities.invokeLater(() -> new FlashMemoryGUI(APP_NAME));
    }
}
