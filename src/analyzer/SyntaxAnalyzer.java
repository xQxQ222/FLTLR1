package analyzer;

import enums.TokenType;
import exceptions.SyntaxException;
import util.ReservedSymbols;
import util.SyntaxConstants;

public class SyntaxAnalyzer {
    private final LexicalAnalyzer lexicalAnalyzer;
    private Token currentToken;

    public SyntaxAnalyzer(LexicalAnalyzer lexicalAnalyzer) {
        this.lexicalAnalyzer = lexicalAnalyzer;
    }

    public void parse() throws SyntaxException {
        try {
            currentToken = lexicalAnalyzer.getNextToken();
            parseS();
            if (currentToken.type() != TokenType.END_OF_TEXT) {
                throw new SyntaxException(SyntaxConstants.ERROR_EXPECTED_END_OF_TEXT, 
                        currentToken.lineIndex(), currentToken.internalIndex());
            }
        } catch (Exception e) {
            if (e instanceof SyntaxException) {
                throw e;
            }
            throw new SyntaxException(SyntaxConstants.ERROR_LEXICAL + e.getMessage(), 
                    currentToken != null ? currentToken.lineIndex() : 0, 
                    currentToken != null ? currentToken.internalIndex() : 0);
        }
    }

    private void parseS() throws SyntaxException {
        parseP();
        parseSPrime();
    }

    private void parseSPrime() throws SyntaxException {
        if (isReserved(ReservedSymbols.SEMICOLON)) {
            matchReserved(ReservedSymbols.SEMICOLON);
            parseP();
            parseSPrime();
        }
    }

    private void parseP() throws SyntaxException {
        parseA();
        matchReserved(ReservedSymbols.EQUALS);
        parseA();
    }

    private void parseA() throws SyntaxException {
        parseM();
        parseAPrime();
    }

    private void parseAPrime() throws SyntaxException {
        if (isReserved(ReservedSymbols.PLUS)) {
            matchReserved(ReservedSymbols.PLUS);
            parseM();
            parseAPrime();
        }
    }

    private void parseM() throws SyntaxException {
        parseT();
        parseMPrime();
    }

    private void parseMPrime() throws SyntaxException {
        if (isReserved(ReservedSymbols.MULTIPLY)) {
            matchReserved(ReservedSymbols.MULTIPLY);
            parseT();
            parseMPrime();
        }
    }

    private void parseT() throws SyntaxException {
        if (currentToken.type() == TokenType.NUMBER) {
            match(TokenType.NUMBER);
        } else if (currentToken.type() == TokenType.IDENTIFIER) {
            match(TokenType.IDENTIFIER);
        } else if (isReserved(ReservedSymbols.LEFT_PAREN)) {
            matchReserved(ReservedSymbols.LEFT_PAREN);
            parseA();
            matchReserved(ReservedSymbols.RIGHT_PAREN);
        } else {
            throw new SyntaxException(SyntaxConstants.ERROR_EXPECTED_NUMBER_IDENTIFIER_OR_PAREN, 
                    currentToken.lineIndex(), currentToken.internalIndex());
        }
    }

    private boolean isReserved(String expectedValue) {
        return currentToken.type() == TokenType.RESERVED && currentToken.value().equals(expectedValue);
    }

    private void matchReserved(String expectedValue) throws SyntaxException {
        if (!isReserved(expectedValue)) {
            throw new SyntaxException(
                    String.format(SyntaxConstants.ERROR_EXPECTED_TOKEN, expectedValue, currentToken.value()),
                    currentToken.lineIndex(), currentToken.internalIndex());
        }
        try {
            currentToken = lexicalAnalyzer.getNextToken();
        } catch (Exception e) {
            throw new SyntaxException(SyntaxConstants.ERROR_GETTING_NEXT_TOKEN + e.getMessage(), 
                    currentToken.lineIndex(), currentToken.internalIndex());
        }
    }

    private void match(TokenType expectedType) throws SyntaxException {
        if (currentToken.type() != expectedType) {
            throw new SyntaxException(
                    String.format(SyntaxConstants.ERROR_EXPECTED_TOKEN, expectedType, currentToken.type()),
                    currentToken.lineIndex(), currentToken.internalIndex());
        }
        try {
            currentToken = lexicalAnalyzer.getNextToken();
        } catch (Exception e) {
            throw new SyntaxException(SyntaxConstants.ERROR_GETTING_NEXT_TOKEN + e.getMessage(), 
                    currentToken.lineIndex(), currentToken.internalIndex());
        }
    }
}

