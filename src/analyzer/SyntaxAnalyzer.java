package analyzer;

import enums.TokenType;
import exceptions.SyntaxException;

public class SyntaxAnalyzer {
    private final LexicalAnalyzer lexicalAnalyzer;
    private Token currentToken;

    public SyntaxAnalyzer(LexicalAnalyzer lexicalAnalyzer) {
        this.lexicalAnalyzer = lexicalAnalyzer;
    }

    /**
     * Основной метод анализа. Анализирует весь исходный текст.
     * S -> P S'
     */
    public void parse() throws SyntaxException {
        try {
            currentToken = lexicalAnalyzer.getNextToken();
            parseS();
            if (currentToken.type() != TokenType.END_OF_TEXT) {
                throw new SyntaxException("Ожидался конец текста", 
                        currentToken.lineIndex(), currentToken.internalIndex());
            }
        } catch (Exception e) {
            if (e instanceof SyntaxException) {
                throw e;
            }
            throw new SyntaxException("Лексическая ошибка: " + e.getMessage(), 
                    currentToken != null ? currentToken.lineIndex() : 0, 
                    currentToken != null ? currentToken.internalIndex() : 0);
        }
    }

    /**
     * S -> P S'
     */
    private void parseS() throws SyntaxException {
        parseP();
        parseSPrime();
    }

    /**
     * S' -> ; P S' | ε
     */
    private void parseSPrime() throws SyntaxException {
        if (currentToken.type() == TokenType.SEMICOLON) {
            match(TokenType.SEMICOLON);
            parseP();
            parseSPrime();
        }
        // ε - пустой переход, ничего не делаем
    }

    /**
     * P -> A = A
     */
    private void parseP() throws SyntaxException {
        parseA();
        match(TokenType.EQUALS);
        parseA();
    }

    /**
     * A -> M A'
     */
    private void parseA() throws SyntaxException {
        parseM();
        parseAPrime();
    }

    /**
     * A' -> + M A' | ε
     */
    private void parseAPrime() throws SyntaxException {
        if (currentToken.type() == TokenType.PLUS) {
            match(TokenType.PLUS);
            parseM();
            parseAPrime();
        }
        // ε - пустой переход
    }

    /**
     * M -> T M'
     */
    private void parseM() throws SyntaxException {
        parseT();
        parseMPrime();
    }

    /**
     * M' -> * T M' | ε
     */
    private void parseMPrime() throws SyntaxException {
        if (currentToken.type() == TokenType.MULTIPLY) {
            match(TokenType.MULTIPLY);
            parseT();
            parseMPrime();
        }
        // ε - пустой переход
    }

    /**
     * T -> NUMBER | IDENTIFIER | ( A )
     */
    private void parseT() throws SyntaxException {
        if (currentToken.type() == TokenType.NUMBER) {
            match(TokenType.NUMBER);
        } else if (currentToken.type() == TokenType.IDENTIFIER) {
            match(TokenType.IDENTIFIER);
        } else if (currentToken.type() == TokenType.LEFT_PAREN) {
            match(TokenType.LEFT_PAREN);
            parseA();
            match(TokenType.RIGHT_PAREN);
        } else {
            throw new SyntaxException("Ожидалось NUMBER, IDENTIFIER или '('", 
                    currentToken.lineIndex(), currentToken.internalIndex());
        }
    }

    /**
     * Проверяет соответствие текущего токена ожидаемому типу и переходит к следующему токену
     */
    private void match(TokenType expectedType) throws SyntaxException {
        if (currentToken.type() != expectedType) {
            throw new SyntaxException(
                    String.format("Ожидался токен %s, но получен %s", expectedType, currentToken.type()),
                    currentToken.lineIndex(), currentToken.internalIndex());
        }
        try {
            currentToken = lexicalAnalyzer.getNextToken();
        } catch (Exception e) {
            throw new SyntaxException("Ошибка при получении следующего токена: " + e.getMessage(), 
                    currentToken.lineIndex(), currentToken.internalIndex());
        }
    }
}

