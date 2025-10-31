package transliteration;

import enums.SymbolType;

import java.util.HashSet;
import java.util.Set;

public class ClassifiedSymbol {
    private static final char COMMENT_START = '<';
    private static final char COMMENT_END = '>';
    
    private static final Set<Character> VALID_LETTERS = Set.of('a', 'b', 'c', 'd');
    private static final Set<Character> VALID_NUMBERS = Set.of('0', '1');

    private final Set<Character> validLetters;
    private final Set<Character> validNumbers;
    private final Character value;
    private final SymbolType type;

    public ClassifiedSymbol(Character value) {
        validLetters = new HashSet<>(VALID_LETTERS);
        validNumbers = new HashSet<>(VALID_NUMBERS);

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
            if (value == null && symbol.value == null) {
                return type == symbol.type;
            }
            if (value == null || symbol.value == null) {
                return false;
            }
            return type == symbol.type && value.equals(symbol.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (value == null) {
            return type.hashCode();
        }
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
            case COMMENT_START -> SymbolType.COMMENT_START;
            case COMMENT_END -> SymbolType.COMMENT_END;
            case '\n' -> SymbolType.END_OF_LINE;
            case ' ' -> SymbolType.SPACE;
            default -> SymbolType.OTHER;
        };
    }
}
