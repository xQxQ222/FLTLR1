package analyzer;

import enums.SymbolType;
import enums.TokenType;
import exceptions.LexicalException;
import machine.RuleSet;
import machine.State;
import machine.StateMachine;
import transliteration.ClassifiedSymbol;
import transliteration.Transliterator;

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
        boolean newTocken = true;

        if (currentSymbol.getType() == SymbolType.END_OF_TEXT) {
            return new Token("", TokenType.END_OF_TEXT,
                    transliterator.getLineIndex(),
                    transliterator.getInternalIndex());
        }

        while (currentSymbol.getType() == SymbolType.COMMENT_START || newTocken) {
            newTocken = false;
            for (StateMachine machine : machines) {
                if (machine.checkApplicability(currentSymbol)) {
                    int tokenLineIndex = transliterator.getLineIndex();
                    int tokenInternalIndex = transliterator.getInternalIndex();

                    String result = runStateMachine(machine);
                    if (machineWorkSuccess) {
                        return new Token(result, machine.getTokenType(),
                                tokenLineIndex, tokenInternalIndex);
                    }
                }
            }
        }

        throw new LexicalException("Недопустимый символ: " + currentSymbol.getValue(),
                transliterator.getLineIndex(), transliterator.getInternalIndex());
    }

    private String runStateMachine(StateMachine machine) {
        if (machine.getInitialState() == null) {
            throw new NullPointerException("Initial state is null");
        }
        StringBuilder sb = new StringBuilder();
        State currentState = machine.getInitialState();


        while (true) {
            Optional<State> optionalState = ruleSet.tryGetState(currentState, currentSymbol);
            if (optionalState.isEmpty()) {
                if (currentState.name().equals("Ident A Fin") && currentSymbol.getValue().equals('a')) {
                    throw new LexicalException("Не выполняется условие \"Если слово начинается с d, оно не должно содержать a\",",
                            transliterator.getLineIndex(),
                            transliterator.getInternalIndex());
                }
                machineWorkSuccess = currentState.isAccepting();
                //currentSymbol = transliterator.getNextSymbol();
                return sb.toString();
            }
            sb.append(currentSymbol.getValue());
            currentState = optionalState.get();

            currentSymbol = transliterator.getNextSymbol();
        }
    }

    private void skipUnrelatedSymbols() {
        while (currentSymbol.getType() == SymbolType.SPACE ||
                currentSymbol.getType() == SymbolType.END_OF_LINE) {
            currentSymbol = transliterator.getNextSymbol();
        }
    }

    private void skipCommentSymbols(){
        do{
            currentSymbol = transliterator.getNextSymbol();
        } while (!currentSymbol.getValue().equals('-'));
    }


}
