package ui;

import model.*;

import java.util.*;

public class FlashMemoryApp {

    private static final String LIST_POSITION_CMD = "ls";
    private static final String GET_POSITION_CMD = ".";
    private static final String BACK_CMD = "..";
    private static final String CHECKOUT_CMD = "cd";
    private static final String ADD_CMD = "add";
    private static final String REMOVE_CMD = "remove";
    private static final String EDIT_CMD = "edit";
    private static final String STUDY_CMD = "study";
    private static final String TEST_CMD = "test";
    private static final String HELP_CMD = "help";
    private static final String QUIT_CMD = "quit";

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

        commands.put(LIST_POSITION_CMD, this::listPosition);
        commands.put(GET_POSITION_CMD, this::getPosition);
        commands.put(BACK_CMD, this::back);
        commands.put(CHECKOUT_CMD, this::checkout);
        commands.put(ADD_CMD, this::handleAddStudyMaterial);
        commands.put(REMOVE_CMD, this::removeMaterial);
        commands.put(EDIT_CMD, this::editName);
        commands.put(STUDY_CMD, this::study);
        commands.put(TEST_CMD, this::test);
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
        String str = makePrettyText(input.nextLine());
        semester = new Semester(str);
        breadcrumb = new Stack<>();
        pointer = semester;
        System.out.printf("Your new semester called \"%s\" has been created.\n\n", str);
    }

    //effects: lists available commands and acts as main program loop for each user input
    //accreditation: modified from FitLifeGymKiosk
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
    //effects: changes name of sub-material in pointer if it is in pointer and the newName doesn't already exist
    private void editName() {
        if (pointer.size() > 0) {
            System.out.println("Enter the name of the thing you would like to change.");
            String oldName = makePrettyText(input.nextLine());

            if (pointer.contains(oldName)) {
                System.out.println("Enter the new name you would like to set");
                String newName = makePrettyText(input.nextLine());

                if (!pointer.contains(newName)) {
                    pointer.editName(oldName, newName);
                    System.out.printf("Changed the name from %s to %s\n", oldName, newName);
                } else {
                    System.out.println("That name already exists. Try again");
                }
            } else {
                System.out.println(pointer + " does not contain " + oldName + ". Try again");
            }

        } else {
            System.out.println("There is nothing to edit under " + pointer);
        }
    }

    //effects: studies a selected material by updating it's confidence and adds today to study dates
    private void study() {
        if (pointer.size() > 0) {
            System.out.println("What would you like to document studying?");
            String material = makePrettyText(input.nextLine());

            if (pointer.contains(material)) {
                parseStudyConfidence(pointer.get(material));
            } else {
                System.out.println(pointer + " does not contain " + material + ". Try again");
            }

        } else {
            System.out.println("There is nothing to study under " + pointer);
        }

    }

    //modifies: this
    //effects: gets [0,3] value from user and studies m with confidence mapped from user. Loops until
    //         correct value is entered
    private void parseStudyConfidence(StudyMaterial m) {
        System.out.println("How confident are you with " + m
                + "?\nEnter a number: 0-None, 1-Low, 2-Medium, or 3-High");
        while (true) {
            try {
                int confidence = Integer.parseInt(input.next());
                if (!(confidence >= 0 && confidence < 4)) {
                    throw new Exception();
                }
                m.trackStudy(Confidence.values()[confidence]);

                System.out.println(m + " has been studied");
                break;
            } catch (Exception e) {
                System.out.println("Invalid input. You must enter number from 0-3.");
            }
        }
    }

    //effects: prints the number of cards under the pointer and asks user if he wants to study them. Studies cards
    //         and calls study on pointer after cards are studied
    private void test() {
        if (pointer.countCards() > 0) {
            System.out.printf("%s has %d cards. Would you like to test yourself? (y/n)\n",
                    pointer, pointer.countCards());
            String str = makePrettyCommand(input.nextLine());

            if (str.equals("y") || str.equals("yes")) {
                testCards(pointer.getAllCards());
                parseStudyConfidence(pointer);
            }
        } else {
            System.out.println("There are no cards to study in " + pointer);
        }
    }

    //requires: allCards not be empty
    //effects: shuffles cards in all cards and presents them to user, then ask for his confidence. User can break by
    //         enter quit
    private void testCards(Collection<Card> allCards) {
        System.out.println("Press enter anything to be shown the answer, enter \"q\" to leave");
        List<Card> cards = new ArrayList<>(allCards);
        Collections.shuffle(cards);
        for (Card c : cards) {
            System.out.println(c.getQuestion());
            try {
                int str;
                str = System.in.read();
                if (str == 113) { // q = 113
                    System.out.println("Testing Stopped");
                    break;
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.println(c.getAnswer());
            System.out.println("Your previous confidence was " + c.getConfidence());
            parseStudyConfidence(c);
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
        String name = makePrettyText(input.nextLine());

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
        System.out.printf("You are looking at a %s called \"%s\" with %d thing(s) to study.\n",
                pointer.getClass().getSimpleName(), pointer.getName(), pointer.size());
    }

    //effects: if pointer is at Semester or Course, make a Course or Topic with user selected name respectively.
    //         if pointer is a Topic, makes a new card under Topic with user defined question and answer
    private void handleAddStudyMaterial() {
        if (pointer instanceof Topic) {
            addCard();
        } else {
            addStudyMaterial();
        }
    }

    //requires: pointer cannot be Topic
    //modifies: this
    //effects: adds the correct Study material under pointer with user specified name
    private void addStudyMaterial() {
        System.out.printf("Please enter the name of your new %s you want to add to \"%s\".\n",
                pointer.subtype.getSimpleName(),pointer.getName());
        String name = makePrettyText(input.nextLine());
        if (!pointer.contains(name)) {
            pointer.add(name);

            System.out.printf("A new %s called \"%s\" has been added to \"%s\".\n",
                    pointer.subtype.getSimpleName(), name, pointer.getName());
        } else {
            System.out.printf("That %s already exists in %s\n", pointer.subtype.getSimpleName() + pointer);
        }
    }

    //requires: pointer must be a Topic
    //modifies: this
    //effects: makes a new Card under current Topic with user specified question and answer
    private void addCard() {
        System.out.println("Please enter the question you want on your card.");
        String name = makePrettyText(input.nextLine());

        if (!pointer.contains(name)) {
            System.out.println("Please enter the answer to your question you want on your card.");
            String answer = makePrettyText(input.nextLine());

            ((Topic) pointer).add(name, answer);
            System.out.printf("A new card with \"%s\" and \"%s\" has been added to \"%s\".\n",
                    name, answer, pointer.getName());
        } else {
            System.out.println("A card already exists with that question");
        }

    }

    //modifies: this
    //effects: removes user specified material from pointer
    private void removeMaterial() {
        String material;

        if (pointer.size() > 0) {
            System.out.println("What would you like to remove from " + pointer);
            material = makePrettyText(input.nextLine());
            if (pointer.contains(material)) {
                pointer.remove(material);
                System.out.println(material + " has been removed");
            } else {
                System.out.println(pointer + " does not contain " + material);
            }
        } else {
            System.out.println("There is nothing to remove from " + pointer);
        }
    }

    //effects: lists all study material with last study date and confidence under the pointer
    private void listPosition() {
        if (pointer.getSortedByPriority().isEmpty()) {
            System.out.printf("There is nothing under \"%s\".\n", pointer.getName());
        } else {
            for (Object m : pointer.getSortedByPriority()) {
                StudyMaterial sm = ((StudyMaterial) m);
                System.out.printf("%s: Studied %d time(s), last modified on %s at %s\n",
                        sm, sm.getTimesStudied(), sm.getLastStudyDate(), sm.getConfidence());
            }
        }
    }

    //effects: prints out help menu with commands
    private void printCommands() {
        System.out.printf("Enter \"%s\" to list things to study.\n", LIST_POSITION_CMD);
        System.out.printf("Enter \"%s\" to see what you are currently looking at.\n", GET_POSITION_CMD);
        System.out.printf("Enter \"%s\" to go back.\n", BACK_CMD);
        System.out.printf("Enter \"%s\" to look at sub-item.\n", CHECKOUT_CMD);
        System.out.printf("Enter \"%s\" to add an element to what you are looking at.\n", ADD_CMD);
        System.out.printf("Enter \"%s\" to remove an element from you are looking at.\n", REMOVE_CMD);
        System.out.printf("Enter \"%s\" to edit the name of an element.\n", EDIT_CMD);
        System.out.printf("Enter \"%s\" to record that you studied something.\n", STUDY_CMD);
        System.out.printf("Enter \"%s\" to test yourself on all the cards in what you are looking at.\n", STUDY_CMD);
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
