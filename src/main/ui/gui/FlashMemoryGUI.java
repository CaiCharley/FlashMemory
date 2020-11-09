package ui.gui;

import exceptions.DuplicateElementException;
import exceptions.ModifyException;
import exceptions.NoElementException;
import model.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
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
import java.util.Collections;
import java.util.List;


// A JFrame GUI interface for FlashMemory
public class FlashMemoryGUI extends JFrame {
    // Application Fields
    private static final String JSON_DIRECTORY = "./data/";
    private static final String APP_NAME = "Flash Memory";
    private static final Color[] COLORS = {
            new Color(212, 90, 90),
            new Color(213, 207, 97),
            new Color(109, 217, 90),
            new Color(86, 226, 207)};

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
    private JPanel cardPane;

    private JButton changeSemesterButton;
    private JButton saveSemesterButton;
    private JButton addButton;
    private JButton removeButton;
    private JButton editNameButton;
    private JButton editAnswerButton;
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
    private JTextPane questionTextPane;
    private JTextPane answerTextPane;
    private JPanel pieChartPane;

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
        editAnswerButton.addActionListener(e -> editAnswer());
        studyButton.addActionListener(e -> studyStudyMaterial(currentNode));

        testButton.addActionListener(e -> testStudyMaterial(currentNode));
    }

    //modifies: this
    //effects: tests all cards under testThisNode in a random order (or test the card if node is card)
    // then records that the user has studied them and asks for confidence
    private void testStudyMaterial(StudyMaterialNode testThisNode) {
        StudyMaterial sm = (StudyMaterial) testThisNode.getUserObject();

        if (sm instanceof StudyCollection<?>) {
            List<StudyMaterialNode> subMaterialNodes = Collections.list(testThisNode.children());
            Collections.shuffle(subMaterialNodes);
            for (StudyMaterialNode sub : subMaterialNodes) {
                testStudyMaterial(sub);
            }
        } else if (sm instanceof Card) {
            StudyMaterialNode parent = (StudyMaterialNode) testThisNode.getParent();
            promptCard((Card) sm, parent.getUserObject().toString());
        }
        studyStudyMaterial(testThisNode);
    }

    //effects: shows dialog boxes with card question and answer
    private void promptCard(Card card, String title) {
        JOptionPane.showMessageDialog(this, card.getQuestion(), title, JOptionPane.QUESTION_MESSAGE);
        JOptionPane.showMessageDialog(this, card.getAnswer(), title, JOptionPane.INFORMATION_MESSAGE);
    }

    //modifies: this
    //effects: if pointer is a card, ask for new answer for card and set the card's answer.
    // Invoke current node changed to update panes
    private void editAnswer() {
        if (pointer instanceof Card) {
            Card card = ((Card) pointer);
            String answer = getStringPopup("Enter the new answer for this card",
                    "Change Answer",
                    card.getAnswer());
            card.setAnswer(answer);

            currentNodeChanged();
        } else {
            throw new InvalidPointerException("Cannot change answer of non-card object");
        }
    }

    //modifies: this
    //effects: updates semesterTree and configures variables to update currentNode and invoke currentNodeChanged
    private void setupJTreeListeners() {
        semesterTree.addTreeSelectionListener(e -> {
            currentNode = (StudyMaterialNode) semesterTree.getLastSelectedPathComponent();
            currentNodeChanged();
        });
    }

    //modifies: this
    //effects: updates the model of semesterTree to reflect a new loaded hierarchy in semester.
    // Makes new root node and adds children. sets semestertree model to new TreeModel with new root.
    // Invokes currentNodeChanged
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
    //effects: updates pointer and pointerPane in this to match new currentNode. Toggles buttons based on pointer class
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
    //effects: updates fields in pointerPane to reflect a new pointer.
    // If studycollection, disable card pane, and set info on pointer pane, invoke update piechart.
    // If pointer is card, setCardpane
    private void updatePointerPane() {
        // provides the contained cards field if pointer is a study collection
        if (pointer instanceof StudyCollection<?>) {
            cardCountLabel.setVisible(true);
            cardPane.setVisible(false);
            cardCountLabel.setText("Contained Cards: " + ((StudyCollection<?>) pointer).countCards());
            pointerLabel.setText(pointer.getName());
            updatePieChart();
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

    //modifies: this
    //effects: if currentNode has children make a pieplot based on confidence of contained cards.
    // Update font and colours of chart and update pieChartpane with new chart. Otherwise disable piechartpane
    private void updatePieChart() {
        if (!currentNode.isLeaf()) {
            DefaultPieDataset pieDataset = getConfidenceDataset((StudyCollection<?>) pointer);
            JFreeChart chart = ChartFactory.createPieChart("Confidence Pie Chart", pieDataset, true, false, false);
            PiePlot plot = (PiePlot) chart.getPlot();

            Font font = pointerLabel.getFont();
            chart.getTitle().setFont(font);
            for (int i = 0; i < pieDataset.getItemCount(); i++) {
                plot.setSectionPaint(i, COLORS[i]);
            }

            pieChartPane.removeAll();
            pieChartPane.add(new ChartPanel(chart));
            pieChartPane.validate();
            pieChartPane.setVisible(true);
        } else {
            pieChartPane.setVisible(false);
        }
    }

    //effects: returns PieDataset based on confidence of cards in sc
    private DefaultPieDataset getConfidenceDataset(StudyCollection<?> sc) {
        int[] tally = {0, 0, 0, 0};

        for (StudyMaterial sm : sc.getAllCards()) {
            int confidence = sm.getConfidence().ordinal();
            tally[confidence]++;
        }

        DefaultPieDataset pieDataset = new DefaultPieDataset();
        pieDataset.setValue("None", tally[0]);
        pieDataset.setValue("Low", tally[1]);
        pieDataset.setValue("Medium", tally[2]);
        pieDataset.setValue("High", tally[3]);

        return pieDataset;
    }

    //modifies: this
    //effects: sets up visibility and question/answer of card pane based on card parameter
    private void setCardPane(Card card) {
        cardPane.setVisible(true);
        pieChartPane.setVisible(false);
        questionTextPane.setText(card.getQuestion());
        answerTextPane.setText(card.getAnswer());
    }

    //modifies: this
    //effects: enables/disables buttons depending on pointer
    private void toggleEditButtonEnable() {
        if (pointer instanceof Card) {
            setButtons(false, true, true, true, true,
                    "Edit Question", "Add");
        } else if (pointer instanceof Semester) {
            setButtons(true, false, true, true, false,
                    "Edit Name", "Add " + ((StudyCollection<?>) pointer).subtype.getSimpleName());
        } else if (pointer instanceof StudyCollection<?>) {
            setButtons(true, true, true, true, false,
                    "Edit Name", "Add " + ((StudyCollection<?>) pointer).subtype.getSimpleName());
        } else {
            setButtons(false, false, false, false, false,
                    "Edit Name", "Add");
        }
    }

    //modifies: this
    //effects: sets buttons to modify semester based on passed in parameters
    private void setButtons(boolean addEnable,
                            boolean removeEnable,
                            boolean editEnable,
                            boolean studyEnable,
                            boolean editAnswerVisibility,
                            String editLabel,
                            String addLabel) {
        addButton.setEnabled(addEnable);
        removeButton.setEnabled(removeEnable);
        editNameButton.setEnabled(editEnable);
        studyButton.setEnabled(studyEnable);
        editAnswerButton.setVisible(editAnswerVisibility);
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
        addStudyMaterialWithName(sc, name);
    }

    //modifies: this
    //Updates semesterModel and adds new semester to current node and reloads current node. If added card, asks user for
    // card answer
    private void addStudyMaterialWithName(StudyCollection<?> sc, String name) {
        if (name != null) {
            try {
                StudyMaterial newMaterial = sc.add(name);
                StudyMaterialNode newNode = new StudyMaterialNode(newMaterial);
                int index = sc.getSortedByPriority().indexOf(newMaterial);
                semesterModel.insertNodeInto(newNode, currentNode, index);
                if (newMaterial instanceof Card) {
                    String answer = getStringPopup("Enter an answer for your new card", "Set Answer", "Answer");
                    ((Card) newMaterial).setAnswer(answer);
                }
                currentNodeChanged();
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
    private void studyStudyMaterial(StudyMaterialNode studyNode) {
        StudyMaterial sm = (StudyMaterial) studyNode.getUserObject();

        String[] options = {"None", "Low", "Medium", "High", "Cancel"};
        int n = JOptionPane.showOptionDialog(this, "How confident are you with " + sm, "Save Changes",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (n != 4) {
            if (studyNode.isRoot()) {
                sm.trackStudy(Confidence.values()[n]);
                semesterModel.nodeChanged(studyNode);
            } else {
                sm.trackStudy(Confidence.values()[n]);
                StudyMaterialNode parentNode = (StudyMaterialNode) studyNode.getParent();
                parentNode.sort();
                semesterModel.reload(parentNode);
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
