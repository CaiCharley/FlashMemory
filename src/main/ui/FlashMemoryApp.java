package ui;

import model.*;


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

    private Scanner input;
    private Semester semester;
    private boolean runApp;
    private StudyCollection pointer;
    private Stack<StudyCollection> breadcrumb;

    public static void main(String[] args) {
        FlashMemoryApp app = new FlashMemoryApp();

        app.run();
        app.end();

        System.out.println("Goodbye!");
    }

    public FlashMemoryApp() {
        input = new Scanner(System.in);
        runApp = true;

        System.out.println("Please enter the name of your semester.");
        String str = input.nextLine().trim();
        semester = new Semester(str);
        breadcrumb = new Stack<>();
        pointer = semester;
        System.out.printf("Your new semester called \"%s\" has been created.\n\n", str);
    }

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

    private void parseCommand(String command) {
        if (command.length() > 0) {
            switch (command) {
                case ADD_CMD:
                    addSM();
                    break;
                case LIST_POSITION_CMD:
                    listPosition();
                    break;
                case GET_POSITION_CMD:
                    getPosition();
                    break;
                case CHECKOUT_CMD:
                    checkout();
                    break;
                case BACK_CMD:
                    back();
                    break;
                case HELP_CMD:
                    printCommands();
                    break;
                case QUIT_CMD:
                    runApp = false;
                    break;
                default:
                    System.out.println("Invalid input. Please try again.");
                    break;
            }
        }
    }

    private void back() {
        if (!breadcrumb.empty()) {
            pointer = breadcrumb.pop();
            getPosition();
        } else {
            System.out.println("You are at the top.");
        }
    }

    private void checkout() {
        if (pointer.size() == 0) {
            System.out.printf("There is nothing under \"%s\".\n", pointer.getName());
            return;
        }

        System.out.println("Enter the name of what you want to see.");
        String name = input.nextLine();

        if (pointer.contains(name)) {
            if (pointer instanceof Topic) {
                String question;
                String answer;
                Card c = (Card) pointer.get(name);

                question = c.getQuestion();
                answer = c.getAnswer();

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

    private void getPosition() {
        String clazz = pointer.getClass().getSimpleName();
        System.out.printf("You are looking at a %s called \"%s\" with %d thing(s) to study.\n",
                clazz, pointer.getName(), pointer.size());
    }

    private void addSM() {
        String name;
        StudyMaterial m;
        if (pointer instanceof Semester) {
            System.out.printf("Please enter the name of your new course you want to add to \"%s\".\n",
                    pointer.getName());
            name = makePrettyText(input.nextLine());
            m = new Course(name);

            pointer.add(m);
            System.out.printf("A new course called \"%s\" has been added to \"%s\".\n", m.getName(),
                    pointer.getName());
        } else if (pointer instanceof Course) {
            System.out.printf("Please enter the name of your new Topic you want to add to \"%s\".\n",
                    pointer.getName());
            name = makePrettyText(input.nextLine());
            m = new Topic(name);

            pointer.add(m);
            System.out.printf("A new topic called \"%s\" has been added to \"%s\".\n",
                    m.getName(), pointer.getName());
        } else if (pointer instanceof Topic) {
            System.out.println("Please enter the question you want on your card.");
            name = makePrettyText(input.nextLine());

            System.out.println("Please enter the answer to your question you want on your card.");
            String answer = input.nextLine();
            m = new Card(name, answer);

            pointer.add(m);
            System.out.printf("A new card with \"%s\" and \"%s\" has been added to \"%s\".\n",
                    m.getName(), answer, pointer.getName());
        } else {
            System.out.println("Invalid Pointer");
        }
    }

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

    private void printCommands() {
        System.out.printf("Enter \"%s\" to add an element to what you are looking at.\n", ADD_CMD);
        System.out.printf("Enter \"%s\" to see what you are currently looking at.\n", GET_POSITION_CMD);
        System.out.printf("Enter \"%s\" to list things to study.\n", LIST_POSITION_CMD);
        System.out.printf("Enter \"%s\" to look at subitem.\n", CHECKOUT_CMD);
        System.out.printf("Enter \"%s\" to go back.\n", BACK_CMD);
        System.out.printf("Enter \"%s\" to see commands.\n", HELP_CMD);
        System.out.printf("Enter \"%s\" to quit.\n", QUIT_CMD);
    }

    //effects: removes white space and quotation marks around s, lower case
    private String makePrettyCommand(String s) {
        s = s.toLowerCase();
        s = s.trim();
        s = s.replaceAll("\"|\'", "");
        return s;
    }

    //effects: removes white space and quotation marks around s
    private String makePrettyText(String s) {
        s = s.trim();
        s = s.replaceAll("\"|\'", "");
        return s;
    }

    //effects: stops receiving user input
    public void end() {
        System.out.println("Quitting...");
        input.close();
    }
}
