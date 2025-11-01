package util;

public class SyntaxConstants {
    public static final String ERROR_EXPECTED_END_OF_TEXT = "Ожидался конец текста";
    public static final String ERROR_LEXICAL = "Лексическая ошибка: ";
    public static final String ERROR_EXPECTED_TOKEN = "Ожидался токен %s, но получен %s";
    public static final String ERROR_GETTING_NEXT_TOKEN = "Ошибка при получении следующего токена: ";
    public static final String ERROR_EXPECTED_NUMBER_IDENTIFIER_OR_PAREN = "Ожидалось NUMBER, IDENTIFIER или '('";
    public static final String ERROR_INCOMPLETE_ASSIGNMENT = "После идентификатора ожидается знак '='";
    public static final String SYNTAX_ERROR_FORMAT = "Синтаксическая ошибка на строке %d, позиции %d: %s";
}
