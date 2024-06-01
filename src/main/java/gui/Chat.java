package gui;

import factory.ConnectionFactory;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import ollama4j.NSQL;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

        JTextArea chat = new JTextArea();
        chat.setFont(new Font("ROBOTO", Font.PLAIN, 16));
        chat.setBackground(Color.darkGray);
        chat.setForeground(Color.white);
        chat.setEditable(false);
        chat.setLineWrap(true);
        chat.setWrapStyleWord(true);

        JTextField input = new JTextField(30);

        JButton limparButton = new JButton("Limpar");
        limparButton.setBackground(Color.darkGray);
        limparButton.setForeground(Color.white);
        limparButton.addActionListener(e -> {
            input.setText("");
            chat.setText("");
        });

        JButton enviarButton = getEnviarButton(input, chat);

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
        panel.setLayout(new FlowLayout());
        panel.add(input);
        panel.add(enviarButton);
        panel.add(limparButton);
        panel.add(voltarButton); // Adicionando o botão "Voltar" ao painel

        add(new JScrollPane(chat), BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
    }

    private JButton getEnviarButton(JTextField input, JTextArea chat) {
        JButton enviarButton = new JButton("Enviar");
        enviarButton.setBackground(Color.darkGray);
        enviarButton.setForeground(Color.white);
        enviarButton.addActionListener(e -> {
            String inputText = input.getText();
            input.setText("");
            chat.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            chat.append("User: " + inputText + "\n");

            try {
                if (selectedAI == null){
                    chat.append("Erro: Nenhuma IA foi selecionada. \n");
                    return;
                }
                if (selectedDatabase == null) {
                    chat.append("Erro: Nenhum banco de dados selecionado.\n");
                    return;
                }

                NSQL prompt = new NSQL(selectedAI);
                prompt.setRequest(inputText);
                String sqlQuery = prompt.aiAnswer();

                // Log da consulta SQL gerada no JTextArea
                chat.append("Consulta SQL gerada: " + sqlQuery + "\n");

                try (Connection conn = new ConnectionFactory(selectedDatabase).getConnection()) {
                    if (conn != null) {
                        try (Statement stmt = conn.createStatement();
                             ResultSet rs = stmt.executeQuery(sqlQuery)) {

                            chat.append("Java Quest: \n");

                            while (rs.next()) {
                                chat.append(rs.getString(1));
                                chat.append("\n");
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            chat.append("Erro de SQL: " + ex.getMessage() + "\n");
                        }
                    } else {
                        chat.append("Erro: Conexão com o banco de dados não estabelecida.\n");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    chat.append("Erro de SQL ao estabelecer conexão: " + ex.getMessage() + "\n");
                }

            } catch (InterruptedException | IOException | OllamaBaseException | SQLException ex) {
                ex.printStackTrace();
                chat.append("Erro: " + ex.getMessage() + "\n");
            }
        });
        return enviarButton;
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

        JComboBox<String> dbSelection = new JComboBox<>(new String[]{"Livros", "Esportes", "Viagens"});
        dbSelection.setSelectedIndex(-1);
        JLabel dbLabel = new JLabel("Selecione um Banco de Dados:");
        dbLabel.setForeground(Color.white);
        dbPanel.add(dbLabel);
        dbPanel.add(dbSelection);

        JComboBox<String> aiSelection = new JComboBox<>(new String[]{"NSQL", "IA-2", "IA-3"});
        aiSelection.setSelectedIndex(-1);
        JLabel aiLabel = new JLabel("Selecione uma IA:");
        aiLabel.setForeground(Color.white);
        dbPanel.add(aiLabel);
        dbPanel.add(aiSelection);

        JButton startButton = new JButton("Iniciar");
        startButton.setEnabled(false);
        startButton.addActionListener(e -> {
            if (dbSelection.getSelectedIndex() != -1 && aiSelection.getSelectedIndex() != -1) {
                selectedDatabase = (String) dbSelection.getSelectedItem();
                selectedAI = (String) aiSelection.getSelectedItem();
                splashScreen.dispose();
                SwingUtilities.invokeLater(() -> new Chat().setVisible(true));
            }
        });

        dbSelection.addActionListener(e -> {
            if (dbSelection.getSelectedIndex() != -1 && aiSelection.getSelectedIndex() != -1) {
                selectedDatabase = (String) dbSelection.getSelectedItem();
                selectedAI = (String) aiSelection.getSelectedItem();
                startButton.setEnabled(true);
            } else {
                startButton.setEnabled(false);
            }
        });

        aiSelection.addActionListener(e -> {
            if (dbSelection.getSelectedIndex() != -1 && aiSelection.getSelectedIndex() != -1) {
                selectedDatabase = (String) dbSelection.getSelectedItem();
                selectedAI = (String) aiSelection.getSelectedItem();
                startButton.setEnabled(true);
            } else {
                startButton.setEnabled(false);
            }
        });

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
