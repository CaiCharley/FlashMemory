package exceptions;

import model.StudyCollection;

public class NoElementException extends ModifyException {
    public NoElementException(StudyCollection<?> sc, String missingElementName) {
        super(String.format("%s %s does not contain %s", sc.getClass().getSimpleName(), sc, missingElementName));
        this.element = missingElementName;
        this.sc = sc;
    }
}
