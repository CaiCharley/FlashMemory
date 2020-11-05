package exceptions;

import model.StudyCollection;

public abstract class ModifyException extends Exception {
    String element;
    StudyCollection<?> sc;

    public ModifyException(String message) {
        super(message);
    }
}
