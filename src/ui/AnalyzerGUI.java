package ui;

import analyzer.LexicalAnalyzer;
import analyzer.SyntaxAnalyzer;
import analyzer.Token;
import enums.TokenType;
import exceptions.SyntaxException;
import util.MachineStarter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class AnalyzerGUI extends JFrame {
    private final MachineStarter starter;
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JButton checkButton;

    private static final String TOKEN_OUTPUT_FORMAT = "Токен: %s (тип: %s)";
    private static final String SUCCESS_MESSAGE = "Синтаксический анализ завершен успешно!";
    private static final String ERROR_PREFIX = "Ошибка: ";
    private static final String TITLE = "Синтаксический анализатор";
    private static final String CHECK_BUTTON_TEXT = "Проверить";
    private static final String INPUT_LABEL = "Введите текст для анализа:";
    private static final String OUTPUT_LABEL = "Результат анализа:";
    private static final String EMPTY_INPUT_MESSAGE = "Введите текст для анализа.";

    public AnalyzerGUI() {
        starter = new MachineStarter();
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.add(new JLabel(INPUT_LABEL), BorderLayout.NORTH);
        inputTextArea = new JTextArea(10, 40);
        inputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);

        checkButton = new JButton(CHECK_BUTTON_TEXT);
        checkButton.addActionListener(new CheckButtonListener());
        checkButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        checkButton.setPreferredSize(new Dimension(150, 35));
        
        KeyListener enterKeyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
                    checkButton.doClick();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
        };
        inputTextArea.addKeyListener(enterKeyListener);

        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.add(new JLabel(OUTPUT_LABEL), BorderLayout.NORTH);
        outputTextArea = new JTextArea(15, 40);
        outputTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputTextArea.setEditable(false);
        outputTextArea.setBackground(Color.WHITE);
        JScrollPane outputScrollPane = new JScrollPane(outputTextArea);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(checkButton, BorderLayout.CENTER);
        mainPanel.add(outputPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private class CheckButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = inputTextArea.getText().trim();
            
            if (text.isEmpty()) {
                outputTextArea.setText(EMPTY_INPUT_MESSAGE);
                outputTextArea.setBackground(new Color(255, 255, 224));
                return;
            }
            
            outputTextArea.setBackground(Color.WHITE);

            StringBuilder result = new StringBuilder();
            
            try {
                LexicalAnalyzer lexicalAnalyzerForTokens = starter.initAnalyzer(text);
                Token currentToken;
                
                result.append("=== Лексический анализ ===\n");
                do {
                    currentToken = lexicalAnalyzerForTokens.getNextToken();
                    result.append(String.format(TOKEN_OUTPUT_FORMAT, currentToken.value(), currentToken.type()))
                          .append("\n");
                } while (currentToken.type() != TokenType.END_OF_TEXT);
                
                result.append("\n=== Синтаксический анализ ===\n");
                LexicalAnalyzer lexicalAnalyzer = starter.initAnalyzer(text);
                SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(lexicalAnalyzer);
                syntaxAnalyzer.parse();
                
                result.append(SUCCESS_MESSAGE);
            } catch (SyntaxException ex) {
                result.append(ERROR_PREFIX).append(ex.getMessage());
            } catch (Exception ex) {
                result.append(ERROR_PREFIX).append(ex.getMessage());
            }
            
            outputTextArea.setText(result.toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            AnalyzerGUI gui = new AnalyzerGUI();
            gui.setVisible(true);
        });
    }
}

