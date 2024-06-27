package gui;

import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import ollama4j.NSQL;

import javax.swing.*;

import factory.ConnectionFactory;

import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Chat extends JFrame {

    private static final String APP_TITLE = "Java Quest";
    private static final int FRAME_WIDTH = 700;
    private static final int FRAME_HEIGHT = 700;
    private static String selectedDatabase;
    private static String selectedAI;

    public Chat() {
        setTitle(APP_TITLE);
        ImageIcon icon = new ImageIcon("src/img/logo_jq.png");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(icon.getImage());

        JPanel chatPanel = new JPanel();
        chatPanel.setBackground(Color.darkGray);
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JTextField input = new JTextField(30);

        JButton enviarButton = getEnviarButton(input, chatPanel);

        JButton limparButton = new JButton("Limpar");
        limparButton.setBackground(Color.darkGray);
        limparButton.setForeground(Color.white);
        limparButton.addActionListener(e -> {
            input.setText("");
            chatPanel.removeAll();
            chatPanel.revalidate();
            chatPanel.repaint();
        });

        JButton voltarButton = new JButton("Voltar");
        voltarButton.setBackground(Color.darkGray);
        voltarButton.setForeground(Color.white);
        voltarButton.addActionListener(e -> {
            dispose();
            selectedAI = null;
            selectedDatabase = null;
            showSelectionScreen();
        });

        JPanel panel = new JPanel();
        panel.setBackground(Color.darkGray);
        panel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(Color.darkGray);
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(input, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.darkGray);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(enviarButton);
        buttonPanel.add(limparButton);
        buttonPanel.add(voltarButton);

        inputPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);

        add(panel);

        setVisible(true);
    }

    private JButton getEnviarButton(JTextField input, JPanel chatPanel) {
        JButton enviarButton = new JButton("Enviar");
        enviarButton.setBackground(Color.darkGray);
        enviarButton.setForeground(Color.white);
        enviarButton.addActionListener(e -> {
            String query = input.getText();
            input.setText("");
            addMessage(chatPanel, "Você: " + query, true);
            useSelectedAI(query, chatPanel);
        });
        return enviarButton;
    }

    private void addMessage(JPanel chatPanel, String message, boolean isUser) {
        JPanel messagePanel = new JPanel();
        messagePanel.setBackground(Color.darkGray);
        messagePanel.setLayout(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT));

        JTextPane messageText = new JTextPane();
        messageText.setText(message);
        messageText.setEditable(false);
        messageText.setFont(new Font("ROBOTO", Font.PLAIN, 16));
        messageText.setBackground(Color.darkGray);
        messageText.setForeground(Color.white);
        messageText.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        messagePanel.add(messageText);
        chatPanel.add(messagePanel);
        chatPanel.revalidate();
        chatPanel.repaint();
    }

    private void useSelectedAI(String query, JPanel chatPanel) {
        try {
            if (selectedAI == null || selectedDatabase == null) {
                addMessage(chatPanel, "Erro: IA ou banco de dados não selecionados.", false);
                return;
            }

            String sqlQuery;

            try {
                NSQL ai = new NSQL(selectedDatabase, selectedAI);
                ai.setRequest(query);
                sqlQuery = ai.aiAnswer();
            } catch (OllamaBaseException | IOException | InterruptedException | SQLException e) {
                addMessage(chatPanel, "Erro ao usar IA: " + e.getMessage(), false);
                return;
            }

            try (Connection conn = new ConnectionFactory(selectedDatabase).getConnection()) {
                if (conn != null) {
                    try (java.sql.Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(sqlQuery)) {

                        addMessage(chatPanel, "Java Quest:", false);

                        while (rs.next()) {
                            addMessage(chatPanel, rs.getString(1), false);
                        }
                    } catch (SQLException ex) {
                        addMessage(chatPanel, "Erro de SQL: " + ex.getMessage(), false);
                    }
                } else {
                    addMessage(chatPanel, "Erro: Conexão com o banco de dados não estabelecida.", false);
                }
            } catch (SQLException ex) {
                addMessage(chatPanel, "Erro de SQL ao estabelecer conexão: " + ex.getMessage(), false);
            }
        } catch (Exception e) {
            addMessage(chatPanel, "Erro geral: " + e.getMessage(), false);
        }
    }

    private static void showSplashScreen() {
        JFrame splashScreen = new JFrame(APP_TITLE);
        ImageIcon icon = new ImageIcon("src/img/logo_jq.png");
        splashScreen.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        splashScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        splashScreen.setLocationRelativeTo(null);
        splashScreen.setIconImage(icon.getImage());

        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout());
        ImageIcon frontImage = new ImageIcon("src/img/front.logo.png");
        JLabel imageLabel = new JLabel(frontImage);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        JPanel dbPanel = new JPanel();
        dbPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        dbPanel.setBackground(Color.darkGray);
        dbPanel.setForeground(Color.white);

        JComboBox<String> dbSelection = new JComboBox<>(new String[]{"Livros", "Esportes"});
        dbSelection.setSelectedIndex(-1);
        JLabel dbLabel = new JLabel("Selecione um Banco de Dados:");
        dbLabel.setForeground(Color.white);
        dbPanel.add(dbLabel);
        dbPanel.add(dbSelection);

        JComboBox<String> aiComboBox = new JComboBox<>(new String[]{"NSQL", "SQLCoder"});
        aiComboBox.setSelectedIndex(-1);
        JLabel aiLabel = new JLabel("Selecione uma IA:");
        aiLabel.setForeground(Color.white);
        dbPanel.add(aiLabel);
        dbPanel.add(aiComboBox);

        JButton startButton = new JButton("Iniciar");
        startButton.setEnabled(false);
        startButton.addActionListener(e -> {
            if (dbSelection.getSelectedIndex() != -1 && aiComboBox.getSelectedIndex() != -1) {
                selectedDatabase = (String) dbSelection.getSelectedItem();
                selectedAI = (String) aiComboBox.getSelectedItem();
                splashScreen.dispose();
                SwingUtilities.invokeLater(() -> new Chat().setVisible(true));
            }
        });

        dbSelection.addActionListener(e -> startButton.setEnabled(dbSelection.getSelectedIndex() != -1 && aiComboBox.getSelectedIndex() != -1));

        aiComboBox.addActionListener(e -> startButton.setEnabled(dbSelection.getSelectedIndex() != -1 && aiComboBox.getSelectedIndex() != -1));

        dbPanel.add(startButton);

        JPanel contentPanel = new JPanel(new GridLayout(2, 1));
        contentPanel.add(imagePanel);
        contentPanel.add(dbPanel);

        contentPanel.setBackground(Color.darkGray);
        contentPanel.setForeground(Color.white);

        splashScreen.add(contentPanel, BorderLayout.CENTER);
        splashScreen.setLocationRelativeTo(null);
        splashScreen.setVisible(true);
    }

    private static void showSelectionScreen() {
        SwingUtilities.invokeLater(Chat::showSplashScreen);
    }

    public static void main(String[] args) {
        showSplashScreen();
    }
}
