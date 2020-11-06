package exceptions;

import model.StudyCollection;

// exception when trying to remove a non-existent element from StudyCollection
public class NoElementException extends ModifyException {
    public NoElementException(StudyCollection<?> sc, String missingElementName) {
        super(String.format("%s %s does not contain %s", sc.getClass().getSimpleName(), sc, missingElementName));
        this.element = missingElementName;
        this.sc = sc;
    }
}
