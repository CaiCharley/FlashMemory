package exceptions;

import model.StudyCollection;

public class DuplicateElementException extends ModifyException {
    public DuplicateElementException(StudyCollection<?> sc, String duplicateName) {
        super(String.format("%s %s already contains %s", sc.getClass().getSimpleName(), sc, duplicateName));
        this.element = duplicateName;
        this.sc = sc;
    }
}
