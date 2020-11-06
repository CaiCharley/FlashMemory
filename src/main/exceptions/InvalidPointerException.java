package exceptions;

// exception when pointer is invalid in the FlashMemoryGUI
public class InvalidPointerException extends RuntimeException {
    public InvalidPointerException(String message) {
        super(message);
    }
}
