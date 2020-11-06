package exceptions;

import model.StudyCollection;

// Exception when method to modify StudyCollection is invoked with invalid parameters
public abstract class ModifyException extends Exception {
    String element;
    StudyCollection<?> sc;

    public ModifyException(String message) {
        super(message);
    }
}
