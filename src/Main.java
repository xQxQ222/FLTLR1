import analyzer.LexicalAnalyzer;
import analyzer.SyntaxAnalyzer;
import analyzer.Token;
import enums.TokenType;
import util.MachineStarter;
import exceptions.SyntaxException;

public class Main {
    private static final MachineStarter starter = new MachineStarter();
    private static final String DEFAULT_TEXT = "abc = 101 + (dbb * 011101110) ; dcc = 101";
    private static final String TOKEN_OUTPUT_FORMAT = "Токен: %s (тип: %s)";
    private static final String SUCCESS_MESSAGE = "Синтаксический анализ завершен успешно!";
    private static final String ERROR_PREFIX = "Ошибка: ";

    public static void main(String[] args) {
        String text = args.length > 0 ? String.join(" ", args) : DEFAULT_TEXT;
        
        try {
            LexicalAnalyzer lexicalAnalyzerForTokens = starter.initAnalyzer(text);
            Token currentToken;
            do {
                currentToken = lexicalAnalyzerForTokens.getNextToken();
                System.out.println(String.format(TOKEN_OUTPUT_FORMAT, currentToken.value(), currentToken.type()));
            } while (currentToken.type() != TokenType.END_OF_TEXT);
            
            LexicalAnalyzer lexicalAnalyzer = starter.initAnalyzer(text);
            SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(lexicalAnalyzer);
            syntaxAnalyzer.parse();
            
            System.out.println(SUCCESS_MESSAGE);
        } catch (SyntaxException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ERROR_PREFIX + ex.getMessage());
        }
    }
}