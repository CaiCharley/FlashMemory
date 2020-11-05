package ui.gui;

import model.Semester;
import model.StudyCollection;
import persistence.JsonReader;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

public class FlashMemoryGUI extends JFrame {
    // Application Fields
    private static final String JSON_DIRECTORY = "./data/";

    private Semester semester;
    private StudyCollection<?> pointer;
    private Stack<StudyCollection<?>> breadcrumb;

    // JFrame Fields
    public static final int WIDTH = 800;
    public static final int HEIGHT = 500;
    private JButton loadSemesterBtn;
    private JLabel semesterNameLabel;
    private JTextField dfTextField;
    private JPanel mainPanel;

    //effects: makes new FlashMemoryGUI and initializes application fields.
    public FlashMemoryGUI(String title) {
        super(title);

        initializeSemester();
        pointer = semester;
        breadcrumb = new Stack<>();

        initializeJFrame();
    }

    //modifies: this
    //effects: initializes JFrame fields and configuration from form
    private void initializeJFrame() {
        loadSemesterBtn.addActionListener(e -> loadSemester());

        add(mainPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(WIDTH, HEIGHT));
        centreOnScreen();
        setVisible(true);
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
    private void initializeSemester() {
        Object[] options = {"New Semester", "Load Semester"};
        int n = JOptionPane.showOptionDialog(this,
                "Would you like to make a new semester or load an existing semester?",
                "Initialize Semester",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (n == 0) {
            makeNewSemester();
        } else {
            loadSemester();
        }
    }

    //modifies: this
    //effects: asks user for new semester name and makes new semester with name
    //adapted from https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html#button
    private void makeNewSemester() {
        String semesterName = (String) JOptionPane.showInputDialog(
                null,
                "Enter a name for your new Semester",
                "Create Semester",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "My Semester");
        semester = new Semester(semesterName);
        semesterNameLabel.setText(semester.getName());
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
                semesterNameLabel.setText(semester.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Unable to load semester:\n" + filepath);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Unable to set LookAndFeel");
        }

        SwingUtilities.invokeLater(() -> new FlashMemoryGUI("Flash Memory"));
    }
}
