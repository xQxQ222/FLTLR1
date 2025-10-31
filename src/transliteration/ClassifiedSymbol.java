package transliteration;

import enums.SymbolType;

import java.util.HashSet;
import java.util.Set;

public class ClassifiedSymbol {
    private static final char commentStart = '<';
    private static final char commentEnd = '>';

    private final Set<Character> validLetters;
    private final Set<Character> validNumbers;
    private final Character value;
    private final SymbolType type;

    public ClassifiedSymbol(Character value) {
        validLetters = new HashSet<>(Set.of('a', 'b', 'c', 'd'));
        validNumbers = new HashSet<>(Set.of('0', '1'));

        this.value = value;
        this.type = defineSymbolType(value);
    }

    public Character getValue() {
        return value;
    }

    public SymbolType getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClassifiedSymbol symbol) {
            return type == symbol.type && value.equals(symbol.getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * type.hashCode() + value.hashCode();
    }


    private SymbolType defineSymbolType(Character value) {
        if (validLetters.contains(value)) {
            return SymbolType.LETTER;
        } else if (validNumbers.contains(value)) {
            return SymbolType.DIGIT;
        }

        return switch (value) {
            case null -> SymbolType.END_OF_TEXT;
            case commentStart -> SymbolType.COMMENT_START;
            case commentEnd -> SymbolType.COMMENT_END;
            case '\n' -> SymbolType.END_OF_LINE;
            case ' ' -> SymbolType.SPACE;
            default -> SymbolType.OTHER;
        };
    }
}
