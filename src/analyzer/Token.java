package analyzer;

import enums.TokenType;

public record Token(String value, TokenType type, int lineIndex, int internalIndex) {
}
