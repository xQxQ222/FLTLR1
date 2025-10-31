import analyzer.LexicalAnalyzer;
import analyzer.Token;
import enums.TokenType;
import util.MachineStarter;

public class Main {
    private static MachineStarter starter = new MachineStarter();

    public static void main(String[] args) {
        String text = "<!--abcd-->abcd dba";
        starter.initAnalyzer(text);
        Token currentToken = new Token("", TokenType.UNKNOW, 0, 0);
        try {
            LexicalAnalyzer analyzer = starter.initAnalyzer(text);
            do {
                currentToken = analyzer.getNextToken();
                System.out.println("Токен: " + currentToken.value());
            } while (currentToken.type() != TokenType.END_OF_TEXT);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}