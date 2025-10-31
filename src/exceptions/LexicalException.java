package exceptions;

public class LexicalException extends RuntimeException {
    public LexicalException(String message, int lineIndex, int symbolIndex) {
        super(message + "\nAt line: " + lineIndex + ". Position: " + symbolIndex);
    }
}
