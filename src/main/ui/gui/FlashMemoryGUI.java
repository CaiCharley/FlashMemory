package ui.gui;

import model.Semester;
import model.StudyCollection;
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

    // JFrame Fields
    private JPanel mainPanel;
    private JButton changeSemesterButton;
    private JButton saveSemesterButton;
    private JLabel semesterNameLabel;
    private JTree semesterTree;
    private JTextArea textArea1;
    private JLabel pointerLabel;

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
        updateJTree();

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
    //effects: updates the root node of semesterTree to reflect hierarchy in semester.
    private void updateJTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(semester);
        addStudyMaterialsToTreeNode(semester.getSortedByPriority(), root);
        DefaultTreeModel model = new DefaultTreeModel(root);
        semesterTree.setModel(model);
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
    //effects: initializes buttons in JFrame with actionListeners and other parameters
    private void setupButtons() {
        changeSemesterButton.addActionListener(e -> setSemester());
        saveSemesterButton.addActionListener(e -> saveSemester());
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
            updateJTree();
            semesterNameLabel.setText(semester.getName());
        }
    }

    //modifies: this
    //effects: asks user for new semester name and makes new semester with name
    //adapted from https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html#button
    private void makeNewSemester() {
        String semesterName = (String) JOptionPane.showInputDialog(
                this,
                "Enter a name for your new Semester",
                "Create Semester",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "My Semester");
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
