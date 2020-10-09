package ui;

import model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class FlashMemoryApp {

    private static final String QUIT_CMD = "quit";
    private static final String LIST_POSITION_CMD = "ls";
    private static final String GET_POSITION_CMD = ".";
    private static final String BACK_CMD = "..";
    private static final String CHECKOUT_CMD = "cd";
    private static final String EDIT_CMD = "edit";
    private static final String STUDY_CMD = "study";
    private static final String TEST_CMD = "test";
    private static final String ADD_CMD = "add";
    private static final String HELP_CMD = "help";

    private static Map<String, Runnable> commands;

    private Scanner input;
    private Semester semester;
    private boolean runApp;
    private StudyCollection pointer;
    private Stack<StudyCollection> breadcrumb;

    //modifies: this
    //effects: fills commands with command:function entries
    private void makeCommandMap() {
        commands = new HashMap<>();

        commands.put(ADD_CMD, this::handleAddStudyMaterial);
        commands.put(LIST_POSITION_CMD, this::listPosition);
        commands.put(GET_POSITION_CMD, this::getPosition);
        commands.put(CHECKOUT_CMD, this::checkout);
        commands.put(BACK_CMD, this::back);
        commands.put(HELP_CMD, this::printCommands);
        commands.put(QUIT_CMD, this::quit);
    }

    public static void main(String[] args) {
        FlashMemoryApp app = new FlashMemoryApp();

        app.run();
        app.end();

        System.out.println("Goodbye!");
    }

    //modifies: this
    //effects: makes new FlashMemoryApp by starting input, runApp and makes commandMap. Asks user for what their new
    //         semester is called. Instantiates pointer and breadcrumb and sets pointer to new semester.
    public FlashMemoryApp() {
        input = new Scanner(System.in);
        runApp = true;
        makeCommandMap();

        System.out.println("Please enter the name of your semester.");
        String str = input.nextLine().trim();
        semester = new Semester(str);
        breadcrumb = new Stack<>();
        pointer = semester;
        System.out.printf("Your new semester called \"%s\" has been created.\n\n", str);
    }

    //effects: lists available commands and acts as main program loop for each user input
    //accreditation: modified fromFitLifeGymKiosk
    private void run() {
        System.out.println("What would you like to do?");
        printCommands();
        String str;

        while (runApp) {
            if (input.hasNext()) {
                str = input.nextLine();
                str = makePrettyCommand(str);
                parseCommand(str);
            }
        }
    }

    //effects:  checks input > 0 and the command exists. Calls the function mapped to the command in commands.
    //          otherwise prints invalid input.
    private void parseCommand(String command) {
        if (command.length() > 0 && commands.containsKey(command)) {
            commands.get(command).run();
        } else {
            System.out.println("Invalid input. Please try again.");
        }
    }

    //modifies: this
    //effects: set runApp to false
    private void quit() {
        runApp = false;
    }

    //modifies: this
    //effects: Pops last position of breadcrumb if it exists and sets pointer to that. Prints the new pointer.
    private void back() {
        if (!breadcrumb.empty()) {
            pointer = breadcrumb.pop();
            getPosition();
        } else {
            System.out.println("You are at the top.");
        }
    }

    //modifies: this
    //effects: if pointer has study materials within it, asks user which one they want to see. If pointer is topic,
    //         just print out card question and answer. Otherwise, set pointer to the object user wants to see. Print
    //         invalid if user has entered something that does not exist in current pointer.
    private void checkout() {
        if (pointer.size() == 0) {
            System.out.printf("There is nothing under \"%s\".\n", pointer.getName());
            return;
        }

        System.out.println("Enter the name of what you want to see.");
        String name = input.nextLine();

        if (pointer.contains(name)) {
            if (pointer instanceof Topic) {
                Card c = (Card) pointer.get(name);

                String question = c.getQuestion();
                String answer = c.getAnswer();

                System.out.printf("Question:\n%s\nAnswer:\n%s\n", question, answer);
            } else {
                breadcrumb.push(pointer);
                pointer = (StudyCollection) pointer.get(name);
                getPosition();
            }
        } else {
            System.out.printf("Invalid entry. Type \"%s\" and try again.\n", CHECKOUT_CMD);
        }
    }

    //effects: tells user what the pointer is at
    private void getPosition() {
        String clazz = pointer.getClass().getSimpleName();
        System.out.printf("You are looking at a %s called \"%s\" with %d thing(s) to study.\n",
                clazz, pointer.getName(), pointer.size());
    }

    //modifies: this
    //effects: if pointer is at Semester or Course, make a Course or Topic with user selected name respectively.
    //         if pointer is a Topic, makes a new card under Topic with user defined question and answer
    private void handleAddStudyMaterial() {
        if (pointer instanceof Semester) {
            System.out.printf("Please enter the name of your new course you want to add to \"%s\".\n",
                    pointer.getName());

            String name = makePrettyText(input.nextLine());
            pointer.add(new Course(name));

            System.out.printf("A new course called \"%s\" has been added to \"%s\".\n", name,
                    pointer.getName());
        } else if (pointer instanceof Course) {
            System.out.printf("Please enter the name of your new Topic you want to add to \"%s\".\n",
                    pointer.getName());

            String name = makePrettyText(input.nextLine());
            pointer.add(new Topic(name));

            System.out.printf("A new topic called \"%s\" has been added to \"%s\".\n",
                    name, pointer.getName());
        } else if (pointer instanceof Topic) {
            System.out.println("Please enter the question you want on your card.");
            String name = makePrettyText(input.nextLine());

            System.out.println("Please enter the answer to your question you want on your card.");
            String answer = makePrettyText(input.nextLine());

            pointer.add(new Card(name, answer));
            System.out.printf("A new card with \"%s\" and \"%s\" has been added to \"%s\".\n",
                    name, answer, pointer.getName());
        }
    }

    //effects: lists all study material with last study date and confidence under the pointer
    private void listPosition() {
        if (pointer.getSortedByPriority().isEmpty()) {
            System.out.printf("There is nothing under \"%s\".\n", pointer.getName());
        } else {
            for (Object m : pointer.getSortedByPriority()) {
                StudyMaterial sm = ((StudyMaterial) m);
                System.out.println(m + ": Last Studied on " + sm.getLastStudyDate() + " at " + sm.getConfidence());
            }
        }
    }

    //effects: prints out help menu with commands
    private void printCommands() {
        System.out.printf("Enter \"%s\" to add an element to what you are looking at.\n", ADD_CMD);
        System.out.printf("Enter \"%s\" to see what you are currently looking at.\n", GET_POSITION_CMD);
        System.out.printf("Enter \"%s\" to list things to study.\n", LIST_POSITION_CMD);
        System.out.printf("Enter \"%s\" to look at sub-item.\n", CHECKOUT_CMD);
        System.out.printf("Enter \"%s\" to go back.\n", BACK_CMD);
        System.out.printf("Enter \"%s\" to see commands.\n", HELP_CMD);
        System.out.printf("Enter \"%s\" to quit.\n", QUIT_CMD);
    }

    //effects: removes white space and quotation marks around s, lower case
    //accreditation: FitLifeGymKiosk
    private String makePrettyCommand(String s) {
        s = s.toLowerCase();
        s = s.trim();
        s = s.replaceAll("\"|\'", "");
        return s;
    }

    //effects: removes white space and quotation marks around s
    //accreditation: modified from FitLifeGymKiosk
    private String makePrettyText(String s) {
        s = s.trim();
        s = s.replaceAll("\"|\'", "");
        return s;
    }

    //effects: stops receiving user input
    //accreditation: FitLifeGymKiosk
    public void end() {
        System.out.println("Quitting...");
        input.close();
    }
}
