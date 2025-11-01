package exceptions;

import util.SyntaxConstants;

public class SyntaxException extends Exception {
    private final int lineIndex;
    private final int internalIndex;

    public SyntaxException(String message, int lineIndex, int internalIndex) {
        super(message);
        this.lineIndex = lineIndex;
        this.internalIndex = internalIndex;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public int getInternalIndex() {
        return internalIndex;
    }

    @Override
    public String getMessage() {
        return String.format(SyntaxConstants.SYNTAX_ERROR_FORMAT, 
                lineIndex + 1, internalIndex + 1, super.getMessage());
    }
}
