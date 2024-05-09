package gui;

import factory.ConnectionFactory;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import ollama4j.SqlCoder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GUI extends JFrame {

    public GUI() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Java Quest");
            ImageIcon icon = new ImageIcon("src/img/logo_jq.png");
            frame.setIconImage(icon.getImage());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 600);

            JPanel panel = new JPanel(new BorderLayout());
            frame.add(panel);

            JTextArea chatArea = new JTextArea();
            chatArea.setEditable(false);
            chatArea.setFont(new Font("Arial", Font.PLAIN, 16));
            chatArea.setBackground(Color.darkGray);
            chatArea.setForeground(Color.WHITE);
            JScrollPane scrollPane = new JScrollPane(chatArea);
            panel.add(scrollPane, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
            panel.add(bottomPanel, BorderLayout.SOUTH);

            JTextField inputField = new JTextField(20);
            inputField.setFont(new Font("Arial", Font.PLAIN, 16));
            bottomPanel.add(inputField);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            bottomPanel.add(buttonPanel);

            JButton sendButton = new JButton("Send");
            sendButton.setForeground(Color.WHITE);
            sendButton.setBackground(Color.darkGray);
            sendButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String input = inputField.getText();
                    chatArea.append("You\t \n\n" + input + "\n\n");

                    SqlCoder prompt = new SqlCoder();
                    prompt.setRequest(input);
                    String generatedSql = null;
                    try {
                        generatedSql = prompt.aiAnswer();
                    } catch (OllamaBaseException ex) {
                        throw new RuntimeException(ex);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }

                    try {
                        ConnectionFactory connector = new ConnectionFactory("jdbc:mysql://localhost:3306/world", "root", "fatec");
                        connector.connect();
                        Connection connection = connector.getConnection();
                        //Connection connection = ConnectionFactory.getConnection();
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(generatedSql);

                        chatArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                        chatArea.append("Bot:\n");
                        chatArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

                        while (resultSet.next()) {
                            chatArea.append(resultSet.getString(1));
                            chatArea.append("\n");
                        }

                        connection.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    chatArea.append("\n\n");

                }
            });
            buttonPanel.add(sendButton);

            JButton clearButton = new JButton("Clear");
            clearButton.setForeground(Color.WHITE);
            clearButton.setBackground(Color.darkGray);
            clearButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chatArea.setText("");
                }
            });
            buttonPanel.add(clearButton);

            frame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}