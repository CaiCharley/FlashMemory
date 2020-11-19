# Flash Memory

### A flash card studying system that implements the Retrospective Revision Spaced Repetition Method with Active Recall

My personal project will be a flash card application that helps you study called *Flash Memory*. It will have basic functionality that allows you to have a question and answer on a flash card. It can also keep track of other details like when you last review a certain cue card and how you confident you felt after testing yourself. An assortment of cards can be collected into a card deck for a specific topic. You can also have multiple classes that can have multiple card decks.

Once cards are made, you can also use *Flash Memory* to test yourself and rate how you did for each topic. It will keep track of your confidence with the topic and also when you last studied it. This way, when you have extra time to study, you can see what your weakest topic is or which topic you haven't studied in while. I was interested in this project as I feel having more efficient studying techniques is always beneficial.

The idea is further explained in by Ali Abdall in his [YouTube Video](https://www.youtube.com/watch?v=b7o09a7t4RA) which inspired the idea for the project.

### User Stories
Phase 1
- As a user, I want to add and remove topics to study in different classes
- As a user, I want have flash cards for each topic
- As a user, I want to record how confident I feel with each topic/flash card
- As a user, I want to know when I last studied a topic
- As a user, I want to know what topics/flash cards there are in a subject/topic in the order they should be studied
- As a user, I want be able to edit the names of topics/classes/cards
- As a user, I want to be able to test myself on all the card within a topic/class

Phase 2
- As a user, I want to be able to save my Semester to a file and be prompted before quitting
- As a user, I want to be able to either make a new Semester or load different Semesters from a file
- As a user, I want my semester to be prompted to save if I quit the program

Phase 3
- As a user, I want to see the hierarchy within each semester and have them sorted in order of priority
- As a user, I want to be able to add/remove/edit different study materials to study collections
- As a user, I want to be able to select a study material in the semester and view its details
- As a user, I want to be able to load different semesters and create new semesters
- As a user, I want to be able to save my semester and be prompted to save before quitting
- As a user, I want to be able to test myself on the flash cards within a study collection and update my confidence
- As a user, I want a visual representation of how I am doing for a study collection (pie chart of confidence)

Phase 4: Task 2
- Design and test a class in your model package that is robust
    - Methods in StudyCollection have been made robust. Specifically, methods involving adding, removing, and editing StudyMaterials throws DuplicateElementExceptions and NoElementExceptions if arguments to modify the material map are not valid
- Design a type hierarchy in your code
    - StudyMaterial is the superclass of all model classes implementing conserved functionality of things that can be studied
    - Concrete StudyMaterials other than Card extend StudyCollection to have conserved functionality involving management of sub-materials
    - Concrete StudyCollections override methods to obtain the set of Card within themselves recusively and also implement robust methods to add the correct type of StudyMaterial within their material map
- Make appropriate use of the Map interface within your code
    - StudyCollections have a HashMap field to store their sub materials with the names of the sub materials as the key to simplify lookup

Phase 4: Task 3
Regarding my model classes, I believe that I have the done as much refactoring as I can to maintain cohesion and reduce coupling. Duplicated code was abstracted away with the StudyMaterial and StudyCollection abstract classes to make a pseudo-composition design pattern with each level only able to add components of specified type. One improvement I could make would be to integrate my composition to extend DefaultTreeNode to allow better integration with Swing's JTree.

One area I believe refactoring would be beneficial would be within the GUI class. I currently have several panels with functionality specific methods within the main JFrame such as PointerPane, SemesterPane, and ModifyButtonPane that could be delegated within their own classes to improve cohesion and instead have the main GUI class have them as fields. However, since I opted to use IntelliJ's form builder, it made it difficult to easily delegate the components to different tasks since the new panels would lose formatting reference to the form file. A workaround would be to make a separate form file for each of the panes to maintain formatting, and I would implement this if I had extra time.