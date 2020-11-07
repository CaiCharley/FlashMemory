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
import javax.swing.tree.TreePath;
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
    private JScrollPane studyDatePane;
    private JScrollPane semesterTreePane;
    private JPanel modifyButtonPane;
    private JPanel semesterPane;
    private JPanel pointerPane;

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
    private JPanel cardPane;
    private JTextPane questionTextPane;
    private JTextPane answerTextPane;

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
        setupButtonListeners();
        setupJTreeListeners();

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
    //effects: initializes buttons in JFrame with actionListeners and other parameters
    private void setupButtonListeners() {
        changeSemesterButton.addActionListener(e -> setSemester());
        saveSemesterButton.addActionListener(e -> saveSemester());

        addButton.addActionListener(e -> addStudyMaterial());
        removeButton.addActionListener(e -> removeStudyMaterial());
        editNameButton.addActionListener(e -> editStudyMaterial());
        studyButton.addActionListener(e -> studyStudyMaterial());
    }

    //modifies: this
    //effects: updates semesterTree and configures variables
    private void setupJTreeListeners() {
        semesterTree.addTreeSelectionListener(e -> {
            currentNode = (StudyMaterialNode) semesterTree.getLastSelectedPathComponent();
            currentNodeChanged();
        });
    }

    //modifies: this
    //effects: updates the model of semesterTree to reflect a new loaded hierarchy in semester.
    private void semesterChanged() {
        StudyMaterialNode root = new StudyMaterialNode(semester);
        addStudyMaterialsToTreeNode(semester.getSortedByPriority(), root);
        semesterModel = new DefaultTreeModel(root);
        semesterTree.setModel(semesterModel);
        currentNodeChanged();
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
    //effects: updates fields in this to match new curNode
    private void currentNodeChanged() {
        if (currentNode != null) {
            pointer = (StudyMaterial) currentNode.getUserObject();
            updatePointerPane();
            pointerPane.setVisible(true);
        } else {
            pointer = null;
            pointerPane.setVisible(false);
        }
        toggleEditButtonEnable();
    }

    //modifies: this
    //effects: updates fields in pointerPane to reflect a new pointer
    private void updatePointerPane() {
        // provides the contained cards field if pointer is a study collection
        if (pointer instanceof StudyCollection<?>) {
            cardCountLabel.setVisible(true);
            cardPane.setVisible(false);
            cardCountLabel.setText("Contained Cards: " + ((StudyCollection<?>) pointer).countCards());
            pointerLabel.setText(pointer.getName());
        } else if (pointer instanceof Card) {
            cardCountLabel.setVisible(false);
            setCardPane((Card) pointer);
            pointerLabel.setText("Card");
        }

        confidenceLabel.setText("Confidence: " + pointer.getConfidence());
        daysSinceStudyLabel.setText(String.format("Days Since Studied: %d", pointer.getDaysSinceStudied()));
        timesStudiedLabel.setText(String.format("Times Studied: %d", pointer.getTimesStudied()));
        dateList.setListData(pointer.getStudyDates().toArray(new LocalDate[0]));
    }

    private void setCardPane(Card card) {
        cardPane.setVisible(true);
        questionTextPane.setText(card.getQuestion());
        answerTextPane.setText(card.getAnswer());
    }

    //modifies: this
    //effects: enables/disables buttons depending on pointer
    private void toggleEditButtonEnable() {
        if (pointer instanceof Card) {
            setButtons(false, true, true, true, "Edit Question", "Add");
        } else if (pointer instanceof Semester) {
            setButtons(true, false, true, true,
                    "Edit Name", "Add " + ((StudyCollection<?>) pointer).subtype.getSimpleName());
        } else if (pointer instanceof StudyCollection<?>) {
            setButtons(true, true, true, true,
                    "Edit Name", "Add " + ((StudyCollection<?>) pointer).subtype.getSimpleName());
        } else {
            setButtons(false, false, false, false,
                    "Edit Name", "Add");
        }
    }

    //modifies: this
    //effects: sets buttons to modify semester
    private void setButtons(boolean add,
                            boolean remove,
                            boolean edit,
                            boolean study,
                            String editLabel,
                            String addLabel) {
        addButton.setEnabled(add);
        removeButton.setEnabled(remove);
        editNameButton.setEnabled(edit);
        studyButton.setEnabled(study);
        editNameButton.setText(editLabel);
        addButton.setText(addLabel);
    }

    //modifies: this
    //effects: adds studymaterial to pointer with name defined by user. Updates semesterModel and adds new semester
    // to current node and reloads current node
    // Throws InvalidPointerException if method invoked while pointer is not studycollection.
    private void addStudyMaterial() {
        StudyCollection<?> sc;

        if (pointer instanceof StudyCollection<?>) {
            sc = (StudyCollection<?>) pointer;
        } else {
            throw new InvalidPointerException("Pointer must be a StudyCollection");
        }

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
                int index = sc.getSortedByPriority().indexOf(newMaterial);
                semesterModel.insertNodeInto(newNode, currentNode, index);
            } catch (DuplicateElementException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    //modifies: this
    //effects: asks user to confirm, and then removes pointer from parent study collection.
    // removes node from semestermodel and invokes currentNodeChanged
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
                currentNodeChanged();
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
        int n = JOptionPane.showOptionDialog(this, "How confident are you with " + pointer, "Save Changes",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (n != 4) {
            if (currentNode.isRoot()) {
                pointer.trackStudy(Confidence.values()[n]);
                semesterModel.nodeChanged(currentNode);
            } else {
                pointer.trackStudy(Confidence.values()[n]);
                StudyMaterialNode studiedNode = new StudyMaterialNode(pointer);

                StudyMaterialNode parentNode = (StudyMaterialNode) currentNode.getParent();
                StudyCollection<?> parentSC = (StudyCollection<?>) parentNode.getUserObject();
                int index = parentSC.getSortedByPriority().indexOf(pointer);

                semesterModel.removeNodeFromParent(currentNode);
                semesterModel.insertNodeInto(studiedNode, parentNode, index);
                currentNode = studiedNode;

                TreePath path = new TreePath(currentNode.getPath());
                semesterTree.setSelectionPath(path);
            }
            currentNodeChanged();
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
            currentNodeChanged();
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
            semesterChanged();
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

    //modifies: this
    //effects:  frame is centred on desktop
    //adapted from SnakeGame
    private void centreOnScreen() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2, (screen.height - getHeight()) / 2);
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
