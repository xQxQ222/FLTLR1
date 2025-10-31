package util;

import analyzer.LexicalAnalyzer;
import enums.TokenType;
import machine.RuleSet;
import machine.State;
import machine.StateMachine;
import transliteration.ClassifiedSymbol;

import java.util.Set;

public class MachineStarter {

    private final RuleSet ruleSet;

    public MachineStarter() {
        ruleSet = RuleSet.getInstance();
    }

    public LexicalAnalyzer initAnalyzer(String text) {
        LexicalAnalyzer analyzer = new LexicalAnalyzer(text);
        analyzer.addMachine(initNumberMachine());
        analyzer.addMachine(initIdentifierMachine());
        analyzer.addMachine(initCommentMachine());
        return analyzer;
    }

    private StateMachine initNumberMachine() {
        State startState = new State("Num S", false);
        State stateA = new State("Num A", false);
        State stateB = new State("Num B", false);
        State stateC = new State("Num C", false);
        State stateD = new State("Num D", false);
        State stateEFin = new State("Num E Fin", true);
        State stateF = new State("Num F", false);
        State stateG = new State("Num G", false);

        ruleSet.addRule(startState, new ClassifiedSymbol('0'), stateA);
        ruleSet.addRule(startState, new ClassifiedSymbol('1'), stateC);
        ruleSet.addRule(stateA, new ClassifiedSymbol('1'), stateB);
        ruleSet.addRule(stateB, new ClassifiedSymbol('1'), startState);
        ruleSet.addRule(stateC, new ClassifiedSymbol('0'), stateD);
        ruleSet.addRule(stateD, new ClassifiedSymbol('1'), stateEFin);
        ruleSet.addRule(stateEFin, new ClassifiedSymbol('1'), stateF);
        ruleSet.addRule(stateF, new ClassifiedSymbol('1'), stateG);
        ruleSet.addRule(stateG, new ClassifiedSymbol('0'), stateEFin);

        return new StateMachine(startState, TokenType.NUMBER, Set.of(new ClassifiedSymbol('0'),
                new ClassifiedSymbol('1')));
    }

    private StateMachine initIdentifierMachine() {
        State startState = new State("Ident S", false);
        State stateAFin = new State("Ident A Fin", true);
        State stateBFin = new State("Ident B Fin", true);

        ruleSet.addRule(startState, new ClassifiedSymbol('a'), stateBFin);
        ruleSet.addRule(startState, new ClassifiedSymbol('b'), stateBFin);
        ruleSet.addRule(startState, new ClassifiedSymbol('c'), stateBFin);
        ruleSet.addRule(startState, new ClassifiedSymbol('d'), stateAFin);

        ruleSet.addRule(stateAFin, new ClassifiedSymbol('b'), stateAFin);
        ruleSet.addRule(stateAFin, new ClassifiedSymbol('c'), stateAFin);
        ruleSet.addRule(stateAFin, new ClassifiedSymbol('d'), stateAFin);

        ruleSet.addRule(stateBFin, new ClassifiedSymbol('a'), stateBFin);
        ruleSet.addRule(stateBFin, new ClassifiedSymbol('b'), stateBFin);
        ruleSet.addRule(stateBFin, new ClassifiedSymbol('c'), stateBFin);
        ruleSet.addRule(stateBFin, new ClassifiedSymbol('d'), stateBFin);

        return new StateMachine(startState, TokenType.IDENTIFIER, Set.of(
                new ClassifiedSymbol('a'),
                new ClassifiedSymbol('b'),
                new ClassifiedSymbol('c'),
                new ClassifiedSymbol('d')
        ));
    }

    private void setCommentRule() {

    }

    private StateMachine initCommentMachine() {
        State startState = new State("Comment S", false);
        State stateA = new State("Comment A", false);
        State stateB = new State("Comment B", false);
        State stateC = new State("Comment C", false);
        State stateD = new State("Comment D", false);
        State stateE = new State("Comment E", false);
        State stateF = new State("Comment F", false);
        State stateFin = new State("Comment Fin", true);

        ruleSet.addRule(startState, new ClassifiedSymbol('<'), stateA);
        ruleSet.addRule(stateA, new ClassifiedSymbol('!'), stateB);
        ruleSet.addRule(stateB, new ClassifiedSymbol('-'), stateC);
        ruleSet.addRule(stateC, new ClassifiedSymbol('-'), stateD);

        ruleSet.addRule(stateD, new ClassifiedSymbol('-'), stateE);
        ruleSet.addRule(stateD, new ClassifiedSymbol('a'), stateD);
        ruleSet.addRule(stateD, new ClassifiedSymbol('b'), stateD);
        ruleSet.addRule(stateD, new ClassifiedSymbol('c'), stateD);
        ruleSet.addRule(stateD, new ClassifiedSymbol('d'), stateD);

        ruleSet.addRule(stateE, new ClassifiedSymbol('-'), stateF);
        ruleSet.addRule(stateE, new ClassifiedSymbol('a'), stateD);
        ruleSet.addRule(stateE, new ClassifiedSymbol('b'), stateD);
        ruleSet.addRule(stateE, new ClassifiedSymbol('c'), stateD);
        ruleSet.addRule(stateE, new ClassifiedSymbol('d'), stateD);

        ruleSet.addRule(stateF, new ClassifiedSymbol('>'), stateFin);
        ruleSet.addRule(stateF, new ClassifiedSymbol('-'), stateFin);
        ruleSet.addRule(stateF, new ClassifiedSymbol('a'), stateD);
        ruleSet.addRule(stateF, new ClassifiedSymbol('b'), stateD);
        ruleSet.addRule(stateF, new ClassifiedSymbol('c'), stateD);
        ruleSet.addRule(stateF, new ClassifiedSymbol('d'), stateD);

        return new StateMachine(startState, TokenType.UNKNOW, Set.of(
                new ClassifiedSymbol('<'),
                new ClassifiedSymbol('!'),
                new ClassifiedSymbol('-'),
                new ClassifiedSymbol('>')
        ));
    }
}
