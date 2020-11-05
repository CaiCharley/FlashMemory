package ui.gui;

import exceptions.DuplicateElementException;
import model.*;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


// A JFrame GUI interface for FlashMemory
public class FlashMemoryGUI extends JFrame {
    // Application Fields
    private static final String JSON_DIRECTORY = "./data/";
    private static final String APP_NAME = "Flash Memory";

    private Semester semester;
    private StudyMaterial pointer;
    private DefaultMutableTreeNode currentNode;
    private DefaultTreeModel semesterModel;

    // JFrame Fields
    private JPanel mainPanel;
    private JButton changeSemesterButton;
    private JButton saveSemesterButton;
    private JButton addButton;
    private JButton removeButton;
    private JButton editNameButton;
    private JButton studyButton;
    private JLabel semesterNameLabel;
    private JLabel pointerLabel;
    private JTextArea textArea1;
    private JTree semesterTree;

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
            currentNode = (DefaultMutableTreeNode) semesterTree.getLastSelectedPathComponent();
            if (currentNode != null) {
                pointer = (StudyMaterial) currentNode.getUserObject();
            } else {
                pointer = null;
            }
            refreshPointer();
        });
    }

    //modifies: this
    //effects: updates the model of semesterTree to reflect a new loaded hierarchy in semester.
    private void changeJTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(semester);
        addStudyMaterialsToTreeNode(semester.getSortedByPriority(), root);
        semesterModel = new DefaultTreeModel(root);
        semesterTree.setModel(semesterModel);
    }

    //modifies: node
    //effects: adds studyMaterials in materials as child nodes of node.
    // calls recursively to add items within each material if material is a StudyCollection
    private void addStudyMaterialsToTreeNode(List<?> materials, DefaultMutableTreeNode node) {
        for (Object sm : materials) {
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(sm);
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
        if (pointer != null) {
            pointerLabel.setText(pointer.getName());
        } else {
            pointerLabel.setText(null);
        }
        toggleEditButtonEnable();
    }

    //modifies: this
    //effects: enables/disables buttons depending on pointer
    private void toggleEditButtonEnable() {
        if (pointer instanceof Card) {
            addButton.setEnabled(false);
            editNameButton.setText("Edit Question");
        } else if (pointer instanceof Semester) {
            addButton.setEnabled(true);
            removeButton.setEnabled(false);
            editNameButton.setEnabled(true);
            studyButton.setEnabled(true);
            editNameButton.setText("Edit Name");
        } else if (pointer != null) {
            addButton.setEnabled(true);
            removeButton.setEnabled(true);
            editNameButton.setEnabled(true);
            studyButton.setEnabled(true);
            editNameButton.setText("Edit Name");
        } else {
            addButton.setEnabled(false);
            removeButton.setEnabled(false);
            editNameButton.setEnabled(false);
            studyButton.setEnabled(false);
        }
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

    private void addStudyMaterial() {
        StudyCollection<?> sc = (StudyCollection<?>) pointer;
        String subMaterial = sc.subtype.getSimpleName();
        String message = "Enter a name for your new " + subMaterial;
        if (sc instanceof Topic) {
            message = "Enter a question for your new " + subMaterial;
        }

        String name = getStringPopup(message, "Create " + subMaterial, "New " + subMaterial);
        if (name != null) {
            try {
                StudyMaterial newMaterial = sc.add(makePrettyText(name));
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newMaterial);
                currentNode.insert(newNode, 0);
                semesterModel.reload();
            } catch (DuplicateElementException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }


    private void removeStudyMaterial() {
    }

    private void studyStudyMaterial() {

    }

    private void editStudyMaterial() {

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
            semester = new Semester(makePrettyText(semesterName));
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

    //effects: returns string entered by user with popup with specified message, title, and default value
    private String getStringPopup(String message, String title, String defaultValue) {
        return (String) JOptionPane.showInputDialog(
                this,
                message,
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                defaultValue);
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
