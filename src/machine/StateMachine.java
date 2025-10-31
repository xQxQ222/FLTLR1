package machine;

import enums.TokenType;
import transliteration.ClassifiedSymbol;

import java.util.Set;

public class StateMachine {

    private final Set<ClassifiedSymbol> applicableSymbols;

    private final State initialState;

    private final TokenType tokenType;

    public StateMachine(State initialState, TokenType tokenType, Set<ClassifiedSymbol> applicableSymbols) {
        this.applicableSymbols = applicableSymbols;
        this.initialState = initialState;
        this.tokenType = tokenType;
    }

    public State getInitialState() {
        return initialState;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public boolean checkApplicability(ClassifiedSymbol symbol) {
        return applicableSymbols.contains(symbol);
    }
}
