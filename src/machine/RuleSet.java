package machine;

import transliteration.ClassifiedSymbol;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RuleSet {

    private final Map<State, Map<ClassifiedSymbol, State>> rules;

    private static RuleSet instance;

    private RuleSet() {
        rules = new HashMap<>();
    }

    public static RuleSet getInstance() {
        if (instance == null) {
            instance = new RuleSet();
        }
        return instance;
    }

    public void addRule(State keyState, ClassifiedSymbol symbol, State newRuleState) {
        Map<ClassifiedSymbol, State> stateRules = rules.
                computeIfAbsent(keyState, k -> new HashMap<>());
        stateRules.put(symbol, newRuleState);
    }

    public Optional<State> tryGetState(State keyState, ClassifiedSymbol symbol) {
        if (symbol.getValue() == null) {
            return Optional.empty();
        }
        Map<ClassifiedSymbol, State> stateMap = rules.getOrDefault(keyState, null);
        if (stateMap == null) {
            return Optional.empty();
        }
        if (!stateMap.containsKey(symbol)) {
            return Optional.empty();
        }
        return Optional.of(stateMap.get(symbol));
    }
}
