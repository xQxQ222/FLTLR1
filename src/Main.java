import analyzer.LexicalAnalyzer;
import analyzer.SyntaxAnalyzer;
import analyzer.Token;
import enums.TokenType;
import util.MachineStarter;
import exceptions.SyntaxException;

public class Main {
    private static MachineStarter starter = new MachineStarter();

    public static void main(String[] args) {
        // Пример синтаксически корректного выражения:
        // abc = 101 + (dbb * 011101110)
        String text = "abc = 101 + (dbb * 011101110) ; dcc = 101";
        
        try {
            // Сначала выводим все токены
            System.out.println("=== Лексический анализ ===");
            LexicalAnalyzer lexicalAnalyzerForTokens = starter.initAnalyzer(text);
            Token currentToken;
            do {
                currentToken = lexicalAnalyzerForTokens.getNextToken();
                System.out.println("Токен: " + currentToken.value() + " (тип: " + currentToken.type() + ")");
            } while (currentToken.type() != TokenType.END_OF_TEXT);
            
            System.out.println("\n=== Синтаксический анализ ===");
            // Создаем новый анализатор для синтаксического разбора
            LexicalAnalyzer lexicalAnalyzer = starter.initAnalyzer(text);
            SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(lexicalAnalyzer);
            syntaxAnalyzer.parse();
            
            System.out.println("Синтаксический анализ завершен успешно!");
        } catch (SyntaxException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Ошибка: " + ex.getMessage());
        }
    }
}