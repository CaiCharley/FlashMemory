package ui.gui;

import model.Semester;
import model.StudyCollection;
import persistence.JsonReader;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

public class FlashMemoryGUI extends JFrame implements ActionListener {
    // Application Fields
    private static final String JSON_DIRECTORY = "./data/";

    private Semester semester;
    private StudyCollection<?> pointer;
    private Stack<StudyCollection<?>> breadcrumb;

    // JFrame Fields
    public static final int WIDTH = 800;
    public static final int HEIGHT = 500;

    //effects: makes new FlashMemoryGUI and initializes application fields.
    public FlashMemoryGUI(String title) {
        super(title);
        try {
            semester = initializeSemester();
            pointer = semester;
            breadcrumb = new Stack<>();
        } catch (IOException e) {
            System.exit(0);
        }

        initializeJFrame();
    }

    //modifies: this
    //effects: initializes JFrame fields and configuration
    private void initializeJFrame() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    //effects: asks user if they want to load semester from file or make new semester. Returns semester
    private static Semester initializeSemester() throws IOException {
        // adapted from https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html#button
        Object[] options = {"New Semester", "Load Semester"};
        int n = JOptionPane.showOptionDialog(null,
                "Would you like to make a new semester or load an existing semester?",
                "Initialize Semester",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (n == 0) {
            String semesterName = (String) JOptionPane.showInputDialog(
                    null,
                    "Enter a name for your new Semester",
                    "Make New Semester",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "My Semester");
            return new Semester(semesterName);
        } else {
            return loadSemester();
        }
    }

    //effects: returns semester by reading from json files in JSON_DIRECTORY.
    // Throws IOException if unable to read Semester or if user presses cancel
    private static Semester loadSemester() throws IOException {
        // FileNameFilter lambda adapted from https://www.journaldev.com/845/java-filenamefilter-example
        File f = new File(JSON_DIRECTORY);
        String[] semesterPaths = f.list((d, s) -> s.toLowerCase().endsWith(".json"));

        // Make JOptionPane to get user selection
        String selectedSemester =
                (String) JOptionPane.showInputDialog(null, "Select the Semester you want to load.",
                        "Open Semester", JOptionPane.QUESTION_MESSAGE, null, semesterPaths, 0);

        String filepath = JSON_DIRECTORY + selectedSemester;
        JsonReader reader = new JsonReader(filepath);

        return reader.read();
    }

    public static void main(String[] args) {
        new FlashMemoryGUI("Flash Memory");
    }

}
