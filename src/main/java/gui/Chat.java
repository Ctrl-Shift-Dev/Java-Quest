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

        JTextField input = new JTextField(30);

        JButton limparButton = new JButton("Limpar");
        limparButton.setBackground(Color.darkGray);
        limparButton.setForeground(Color.white);
        limparButton.addActionListener(e -> input.setText(""));

        JButton enviarButton = getEnviarButton(input, chat);

        JPanel panel = new JPanel();
        panel.setBackground(Color.darkGray); // Adiciona a mesma cor de fundo do painel de chat
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

                Connection conn = new ConnectionFactory().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sqlQuery);

                chat.append("Java Quest: \n");

                while (rs.next()) {
                    chat.append(rs.getString(1));
                    chat.append("\n");
                }

                conn.close();
                stmt.close();
                rs.close();

            } catch (SQLException | InterruptedException | IOException | OllamaBaseException ex) {
                throw new RuntimeException(ex);
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


        // Cria painel de imagem central
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout());
        ImageIcon frontImage = new ImageIcon("src/img/front.logo.png"); // Substitua pelo caminho da sua imagem
        JLabel imageLabel = new JLabel(frontImage);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // Cria painel de seleção de banco de dados
        JPanel dbPanel = new JPanel();
        dbPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        dbPanel.setBackground(Color.darkGray); // Define a cor de fundo para coincidir com o frame principal
        dbPanel.setForeground(Color.white);    // Define a cor do texto para coincidir com o frame principal


        JComboBox<String> dbSelection = new JComboBox<>(new String[]{"Database 1", "Database 2"}); // Substitua pelos bancos de dados reais
        dbSelection.setSelectedIndex(-1); // Desselecionar por padrão
        dbPanel.add(new JLabel("Database:"));
        dbPanel.add(dbSelection);

        // Cria botão de iniciar e desabilita inicialmente
        JButton startButton = new JButton("Iniciar");
        startButton.setEnabled(false);
        startButton.addActionListener(e -> {
            splashScreen.dispose(); // Fecha a tela inicial
            SwingUtilities.invokeLater(() -> new Chat().setVisible(true)); // Exibe a janela principal do chat
        });

        dbSelection.addActionListener(e -> {
            if (dbSelection.getSelectedIndex() != -1) {
                startButton.setEnabled(true); // Habilita o botão quando uma seleção for feita
            } else {
                startButton.setEnabled(false); // Desabilita o botão se nenhuma seleção for feita
            }
        });

        dbPanel.add(startButton);

        // Combina painéis de imagem e seleção
        JPanel contentPanel = new JPanel(new GridLayout(2, 1));
        contentPanel.add(imagePanel);
        contentPanel.add(dbPanel);

        // Define a mesma cor de fundo para o painel de conteúdo
        contentPanel.setBackground(Color.darkGray);
        contentPanel.setForeground(Color.white);

        splashScreen.add(contentPanel, BorderLayout.CENTER);
        splashScreen.setLocationRelativeTo(null); // Centraliza na tela
        splashScreen.setVisible(true);
    }

    public static void main(String[] args) {
        // Exibe a tela inicial
        showSplashScreen();

        // A janela principal do chat é lançada a partir do botão iniciar da tela inicial
    }
}
