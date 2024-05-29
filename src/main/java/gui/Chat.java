package gui;

import factory.ConnectionFactory;
import factory.Schema;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import ollama4j.NSQL;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Chat extends JFrame {

    public Chat() {
        setTitle("Java Quest");
        ImageIcon icon = new ImageIcon("src/img/logo_jq.png");
        setSize(600,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
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
            input.setText(""); // Limpa o campo de entrada de texto
            chat.setText(""); // Limpa a área de texto do chat
        });

        JButton enviarButton = getEnviarButton(input, chat);

        JPanel panel = new JPanel();
        panel.setBackground(Color.darkGray); // Adiciona a mesma cor de fundo do painel de chat
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
}
