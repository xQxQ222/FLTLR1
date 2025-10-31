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
        // Паттерн: (011)*101(110)*
        // S - старт (может быть началом (011)* или сразу 1)
        // A - после 0 (первый символ (011)*)
        // B - после 01 (второй символ (011)*)
        // C - после 011 (возврат к началу цикла (011)* или переход к 101)
        // D - после 1 (начало 101)
        // E - после 10 (второй символ 101)
        // F - после 101 (финальное состояние, начало (110)*)
        // G - после 110 (возврат к началу цикла (110)*)
        // H - после 11 (второй символ (110)*)
        
        State startState = new State("Num S", false);
        State stateA = new State("Num A", false);  // после 0 в (011)*
        State stateB = new State("Num B", false);  // после 01 в (011)*
        State stateC = new State("Num C", false);  // после 011 в (011)*
        State stateD = new State("Num D", false);  // после 1 перед 101
        State stateE = new State("Num E", false);  // после 10 в 101
        State stateFFin = new State("Num F Fin", true);  // после 101 (финальное)
        State stateG = new State("Num G", false);  // после 11 в (110)*
        State stateH = new State("Num H", false);  // после 110 в (110)*

        // (011)* - цикл: можно ноль или более раз
        ruleSet.addRule(startState, new ClassifiedSymbol('0'), stateA);
        ruleSet.addRule(startState, new ClassifiedSymbol('1'), stateD);  // переход к 101
        
        ruleSet.addRule(stateA, new ClassifiedSymbol('1'), stateB);
        ruleSet.addRule(stateB, new ClassifiedSymbol('1'), stateC);
        ruleSet.addRule(stateC, new ClassifiedSymbol('0'), stateA);  // возврат к началу (011)*
        ruleSet.addRule(stateC, new ClassifiedSymbol('1'), stateD);  // переход к 101
        
        // 101 - обязательно должно быть
        ruleSet.addRule(stateD, new ClassifiedSymbol('0'), stateE);
        ruleSet.addRule(stateE, new ClassifiedSymbol('1'), stateFFin);
        
        // (110)* - цикл после 101
        ruleSet.addRule(stateFFin, new ClassifiedSymbol('1'), stateG);
        ruleSet.addRule(stateG, new ClassifiedSymbol('1'), stateH);
        ruleSet.addRule(stateH, new ClassifiedSymbol('0'), stateFFin);  // возврат к началу (110)*

        return new StateMachine(startState, TokenType.NUMBER, Set.of(new ClassifiedSymbol('0'),
                new ClassifiedSymbol('1')));
    }

    private StateMachine initIdentifierMachine() {
        // Паттерн: (a|b|c|d)+
        // Если начинается с d, то не должно встретиться a
        // stateAFin - состояние для слов, начинающихся с d (не может содержать a)
        // stateBFin - состояние для слов, начинающихся с a, b или c (может содержать любые символы)
        
        State startState = new State("Ident S", false);
        State stateAFin = new State("Ident A Fin", true);  // начинается с d
        State stateBFin = new State("Ident B Fin", true);  // начинается с a, b или c

        // Первый символ определяет путь
        ruleSet.addRule(startState, new ClassifiedSymbol('a'), stateBFin);
        ruleSet.addRule(startState, new ClassifiedSymbol('b'), stateBFin);
        ruleSet.addRule(startState, new ClassifiedSymbol('c'), stateBFin);
        ruleSet.addRule(startState, new ClassifiedSymbol('d'), stateAFin);

        // Если началось с d, можно только b, c, d (нельзя a)
        ruleSet.addRule(stateAFin, new ClassifiedSymbol('b'), stateAFin);
        ruleSet.addRule(stateAFin, new ClassifiedSymbol('c'), stateAFin);
        ruleSet.addRule(stateAFin, new ClassifiedSymbol('d'), stateAFin);

        // Если началось с a, b или c, можно любые символы
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
        // Паттерн: <!-- ... --> (многострочные комментарии как в HTML)
        // Комментарии должны пропускаться и не возвращать токен
        
        State startState = new State("Comment S", false);
        State stateA = new State("Comment A", false);  // после <
        State stateB = new State("Comment B", false);  // после <!
        State stateC = new State("Comment C", false);  // после <!-
        State stateD = new State("Comment D", false);  // после <!-- (тело комментария)
        State stateE = new State("Comment E", false);  // после первого -
        State stateF = new State("Comment F", false);  // после --
        State stateFin = new State("Comment Fin", true);  // после --> (финальное)

        // Начало комментария: <!--
        ruleSet.addRule(startState, new ClassifiedSymbol('<'), stateA);
        ruleSet.addRule(stateA, new ClassifiedSymbol('!'), stateB);
        ruleSet.addRule(stateB, new ClassifiedSymbol('-'), stateC);
        ruleSet.addRule(stateC, new ClassifiedSymbol('-'), stateD);

        // Тело комментария (stateD) - может содержать любые символы, включая переводы строк
        // Если встречаем '-', переходим в состояние проверки закрытия
        ruleSet.addRule(stateD, new ClassifiedSymbol('-'), stateE);
        // Все остальные символы остаются в теле комментария
        ruleSet.addRule(stateD, new ClassifiedSymbol('a'), stateD);
        ruleSet.addRule(stateD, new ClassifiedSymbol('b'), stateD);
        ruleSet.addRule(stateD, new ClassifiedSymbol('c'), stateD);
        ruleSet.addRule(stateD, new ClassifiedSymbol('d'), stateD);
        ruleSet.addRule(stateD, new ClassifiedSymbol('0'), stateD);
        ruleSet.addRule(stateD, new ClassifiedSymbol('1'), stateD);
        ruleSet.addRule(stateD, new ClassifiedSymbol(' '), stateD);
        ruleSet.addRule(stateD, new ClassifiedSymbol('\n'), stateD);
        ruleSet.addRule(stateD, new ClassifiedSymbol('>'), stateD);  // > внутри комментария тоже допустим
        ruleSet.addRule(stateD, new ClassifiedSymbol('!'), stateD);  // ! может быть в теле комментария

        // После первого '-' возможен второй '-' или возврат в тело
        ruleSet.addRule(stateE, new ClassifiedSymbol('-'), stateF);
        ruleSet.addRule(stateE, new ClassifiedSymbol('a'), stateD);
        ruleSet.addRule(stateE, new ClassifiedSymbol('b'), stateD);
        ruleSet.addRule(stateE, new ClassifiedSymbol('c'), stateD);
        ruleSet.addRule(stateE, new ClassifiedSymbol('d'), stateD);
        ruleSet.addRule(stateE, new ClassifiedSymbol('0'), stateD);
        ruleSet.addRule(stateE, new ClassifiedSymbol('1'), stateD);
        ruleSet.addRule(stateE, new ClassifiedSymbol(' '), stateD);
        ruleSet.addRule(stateE, new ClassifiedSymbol('\n'), stateD);
        ruleSet.addRule(stateE, new ClassifiedSymbol('>'), stateD);
        ruleSet.addRule(stateE, new ClassifiedSymbol('!'), stateD);

        // После '--' ожидаем '>' (может быть несколько '-' перед '>', например --->)
        ruleSet.addRule(stateF, new ClassifiedSymbol('>'), stateFin);
        // Если после '--' идет еще один '-', остаемся в stateF (для поддержки ---> и т.д.)
        ruleSet.addRule(stateF, new ClassifiedSymbol('-'), stateF);
        ruleSet.addRule(stateF, new ClassifiedSymbol('a'), stateD);
        ruleSet.addRule(stateF, new ClassifiedSymbol('b'), stateD);
        ruleSet.addRule(stateF, new ClassifiedSymbol('c'), stateD);
        ruleSet.addRule(stateF, new ClassifiedSymbol('d'), stateD);
        ruleSet.addRule(stateF, new ClassifiedSymbol('0'), stateD);
        ruleSet.addRule(stateF, new ClassifiedSymbol('1'), stateD);
        ruleSet.addRule(stateF, new ClassifiedSymbol(' '), stateD);
        ruleSet.addRule(stateF, new ClassifiedSymbol('\n'), stateD);
        ruleSet.addRule(stateF, new ClassifiedSymbol('!'), stateD);

        return new StateMachine(startState, TokenType.COMMENT, Set.of(
                new ClassifiedSymbol('<'),
                new ClassifiedSymbol('!'),
                new ClassifiedSymbol('-'),
                new ClassifiedSymbol('>')
        ));
    }
}
