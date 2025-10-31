package transliteration;

public class Transliterator {
    private String text = "";
    private int generalIndex = 0;
    private int lineIndex = 0;
    private int internalIndex = -1;

    public void setText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text is null");
        }
        this.text = text;
        generalIndex = 0;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public int getInternalIndex() {
        return internalIndex;
    }

    public ClassifiedSymbol getNextSymbol() {
        if (generalIndex >= text.length()) {
            return new ClassifiedSymbol(null);
        }
        if (text.charAt(generalIndex) == '\n') {
            lineIndex++;
            internalIndex = -1;
        }
        internalIndex++;
        return new ClassifiedSymbol(text.charAt(generalIndex++));
    }
}
