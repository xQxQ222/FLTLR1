package analyzer;

import enums.SymbolType;
import enums.TokenType;
import exceptions.LexicalException;
import machine.RuleSet;
import machine.State;
import machine.StateMachine;
import transliteration.ClassifiedSymbol;
import transliteration.Transliterator;
import util.MachineConstants;

import java.util.*;

public class LexicalAnalyzer {
    private final Transliterator transliterator;
    private final Set<StateMachine> machines;
    private final RuleSet ruleSet;

    private ClassifiedSymbol currentSymbol;
    private boolean machineWorkSuccess = false;

    public void addMachine(StateMachine machine) {
        machines.add(machine);
    }

    public LexicalAnalyzer(String text) {
        transliterator = new Transliterator();
        machines = new HashSet<>();
        ruleSet = RuleSet.getInstance();

        if (text == null) {
            throw new IllegalArgumentException("text is null");
        }

        transliterator.setText(text);
        currentSymbol = transliterator.getNextSymbol();
    }

    public Token getNextToken() {
        skipUnrelatedSymbols();
        boolean newToken = true;

        if (currentSymbol.getType() == SymbolType.END_OF_TEXT) {
            return new Token("", TokenType.END_OF_TEXT,
                    transliterator.getLineIndex(),
                    transliterator.getInternalIndex());
        }

        while (currentSymbol.getType() == SymbolType.COMMENT_START || newToken) {
            newToken = false;
            for (StateMachine machine : machines) {
                if (machine.checkApplicability(currentSymbol)) {
                    int tokenLineIndex = transliterator.getLineIndex();
                    int tokenInternalIndex = transliterator.getInternalIndex();

                    String result = runStateMachine(machine);
                    if (machineWorkSuccess) {
                        if (machine.getTokenType() == TokenType.COMMENT) {
                            skipUnrelatedSymbols();
                            if (currentSymbol.getType() == SymbolType.END_OF_TEXT) {
                                return new Token("", TokenType.END_OF_TEXT,
                                        transliterator.getLineIndex(),
                                        transliterator.getInternalIndex());
                            }
                            newToken = true;
                            break;
                        }
                        return new Token(result, machine.getTokenType(),
                                tokenLineIndex, tokenInternalIndex);
                    }
                }
            }
            
            Token singleCharToken = tryGetSingleCharToken();
            if (singleCharToken != null) {
                return singleCharToken;
            }
        }

        String symbolValue = currentSymbol.getValue() != null ? currentSymbol.getValue().toString() : "null";
        throw new LexicalException(MachineConstants.ERROR_INVALID_SYMBOL + symbolValue,
                transliterator.getLineIndex(), transliterator.getInternalIndex());
    }

    private String runStateMachine(StateMachine machine) {
        if (machine.getInitialState() == null) {
            throw new NullPointerException("Initial state is null");
        }
        StringBuilder sb = new StringBuilder();
        State currentState = machine.getInitialState();

        while (true) {
            if (currentSymbol.getType() == SymbolType.END_OF_TEXT) {
                if (machine.getTokenType() == TokenType.COMMENT && !currentState.isAccepting()) {
                    throw new LexicalException(MachineConstants.ERROR_UNCLOSED_COMMENT,
                            transliterator.getLineIndex(),
                            transliterator.getInternalIndex());
                }
                machineWorkSuccess = currentState.isAccepting();
                return sb.toString();
            }

            Optional<State> optionalState = ruleSet.tryGetState(currentState, currentSymbol);
            if (optionalState.isEmpty()) {
                if (machine.getTokenType() == TokenType.COMMENT) {
                    String stateName = currentState.name();
                    if (stateName.equals(MachineConstants.STATE_COMMENT_BODY) ||
                        stateName.equals(MachineConstants.STATE_COMMENT_AFTER_FIRST_DASH) ||
                        stateName.equals(MachineConstants.STATE_COMMENT_AFTER_DOUBLE_DASH)) {
                        if (currentSymbol.getValue() != null && currentSymbol.getValue() != '-') {
                            sb.append(currentSymbol.getValue());
                            Optional<State> commentBodyStateOpt = ruleSet.getStateByName(MachineConstants.STATE_COMMENT_BODY);
                            if (commentBodyStateOpt.isPresent()) {
                                currentState = commentBodyStateOpt.get();
                            } else {
                                currentState = new State(MachineConstants.STATE_COMMENT_BODY, false);
                            }
                            currentSymbol = transliterator.getNextSymbol();
                            continue;
                        }
                    }
                }
                
                if (currentState.name().equals(MachineConstants.STATE_IDENTIFIER_D_START) 
                        && currentSymbol.getValue() != null 
                        && currentSymbol.getValue().equals('a')) {
                    throw new LexicalException(MachineConstants.ERROR_IDENTIFIER_D_CONTAINS_A,
                            transliterator.getLineIndex(),
                            transliterator.getInternalIndex());
                }
                machineWorkSuccess = currentState.isAccepting();
                return sb.toString();
            }
            if (currentSymbol.getValue() != null) {
                sb.append(currentSymbol.getValue());
            }
            currentState = optionalState.get();

            currentSymbol = transliterator.getNextSymbol();
        }
    }

    private void skipUnrelatedSymbols() {
        while (currentSymbol != null && 
               (currentSymbol.getType() == SymbolType.SPACE ||
                currentSymbol.getType() == SymbolType.END_OF_LINE)) {
            currentSymbol = transliterator.getNextSymbol();
        }
    }

    private Token tryGetSingleCharToken() {
        if (currentSymbol == null || currentSymbol.getValue() == null) {
            return null;
        }
        
        char value = currentSymbol.getValue();
        
        switch (value) {
            case ';', '=', '+', '*', '(', ')' -> {
                int tokenLineIndex = transliterator.getLineIndex();
                int tokenInternalIndex = transliterator.getInternalIndex();
                currentSymbol = transliterator.getNextSymbol();
                return new Token(String.valueOf(value), TokenType.RESERVED, tokenLineIndex, tokenInternalIndex);
            }
            default -> {
                return null;
            }
        }
    }
}
