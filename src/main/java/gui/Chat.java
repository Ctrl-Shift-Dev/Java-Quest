package gui;

import factory.ConnectionFactory;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import ollama4j.NSQL;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.*;

public class Chat extends JFrame {

    public Chat(){
        setTitle("Java Quest");
        ImageIcon icon = new ImageIcon("src/img/logo_jq.png");
        setSize(500,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(icon.getImage());

        JTextArea chat = new JTextArea();
        chat.setSize(470,517);
        chat.setFont(new Font("ROBOTO", Font.PLAIN, 16));
        chat.setBackground(Color.darkGray);
        chat.setForeground(Color.white);
        chat.setEditable(false);
        chat.setLineWrap(true);

        JTextField input = new JTextField(30);
        input.setSize( 366, 36);

        JButton limparButton = new JButton("Limpar");
        limparButton.setSize(39,36);
        limparButton.setBackground(Color.darkGray);
        limparButton.setForeground(Color.white);
        limparButton.addActionListener(e -> input.setText(" "));

        JButton enviarButton = getEnviarButton(input, chat);

        JPanel panel = new JPanel();
        panel.add(input);
        panel.add(enviarButton);
        panel.add(limparButton);

        add(new JScrollPane(chat), BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

    }

    private static JButton getEnviarButton(JTextField input, JTextArea chat) {
        JButton enviarButton = new JButton("Enviar");
        enviarButton.setSize(39,36);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Chat().setVisible(true));

    }
}



