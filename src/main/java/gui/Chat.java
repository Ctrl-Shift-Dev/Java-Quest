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
    private static final int FRAME_WIDTH = 500;
    private static final int FRAME_HEIGHT = 600;

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

        JPanel panel = new JPanel();
        panel.setBackground(Color.darkGray);
        panel.setLayout(new FlowLayout());
        panel.add(input);
        panel.add(enviarButton);
        panel.add(limparButton);

        add(new JScrollPane(chat), BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
    }

    private static JButton getEnviarButton(JTextField input, JTextArea chat) {
        JButton enviarButton = new JButton("Enviar");
        enviarButton.setBackground(Color.darkGray);
        enviarButton.setForeground(Color.white);
        enviarButton.addActionListener(e -> {
            String inputText = input.getText();
            input.setText("");
            chat.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            chat.append("User: " + inputText + "\n");
    
            try {
                NSQL prompt = new NSQL();
                prompt.setRequest(inputText);
    
                String sqlQuery = prompt.aiAnswer();
    
                // Log da consulta SQL gerada no JTextArea
                chat.append("Consulta SQL gerada: " + sqlQuery + "\n");
    
                try (Connection conn = new ConnectionFactory().getConnection()) {
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

        JComboBox<String> dbSelection = new JComboBox<>(new String[]{"Database 1", "Database 2"});
        dbSelection.setSelectedIndex(-1);
        dbPanel.add(new JLabel("Database:"));
        dbPanel.add(dbSelection);

        JButton startButton = new JButton("Iniciar");
        startButton.setEnabled(false);
        startButton.addActionListener(e -> {
            splashScreen.dispose();
            SwingUtilities.invokeLater(() -> new Chat().setVisible(true));
        });

        dbSelection.addActionListener(e -> {
            if (dbSelection.getSelectedIndex() != -1) {
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

    public static void main(String[] args) {
        showSplashScreen();
    }
}
