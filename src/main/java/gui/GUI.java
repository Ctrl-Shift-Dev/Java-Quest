package gui;

import ollama4j.SqlCoder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GUI extends JFrame {
//    private final JTextArea chatArea;
//    private final JTextField inputField;

    public GUI() {
//        setTitle("Java Quest");
//        setSize(400, 400);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        setLayout(new BorderLayout());
//
//        chatArea = new JTextArea();
//        chatArea.setEditable(false);
//        JScrollPane scrollPane = new JScrollPane(chatArea);
//        add(scrollPane, BorderLayout.CENTER);
//
//        inputField = new JTextField();
//        inputField.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String input = inputField.getText();
//                SqlCoder prompt = new SqlCoder();
//                prompt.setRequest(input);
//                inputField.setText("");
//                chatArea.setText(String.valueOf(prompt));
//            }
//        });
//        add(inputField, BorderLayout.SOUTH);
//
//        setVisible(true);
//    }

                SwingUtilities.invokeLater(() -> {
                    JFrame frame = new JFrame("Chat Interface");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(400, 600);

                    JPanel panel = new JPanel(new BorderLayout());
                    frame.add(panel);

                    JTextArea chatArea = new JTextArea();
                    chatArea.setEditable(false);
                    chatArea.setFont(new Font("Arial", Font.PLAIN, 20));
                    JScrollPane scrollPane = new JScrollPane(chatArea);
                    panel.add(scrollPane, BorderLayout.CENTER);

                    JPanel bottomPanel = new JPanel();
                    bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
                    panel.add(bottomPanel, BorderLayout.SOUTH);

                    JTextField inputField = new JTextField(20);
                    inputField.setFont(new Font("Arial", Font.PLAIN, 20));
                    bottomPanel.add(inputField);

                    JPanel buttonPanel = new JPanel();
                    buttonPanel.setLayout(new FlowLayout());
                    bottomPanel.add(buttonPanel);

                    JButton sendButton = new JButton("Enviar");
                    sendButton.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String input = inputField.getText();
                            chatArea.append("Voce\t \n\n" + input + "\n\n");
                            SqlCoder prompt = new SqlCoder();
                            chatArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                            prompt.setRequest(input);
                            inputField.setText("");
                            chatArea.append(String.valueOf((prompt)));
                            chatArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                        }

                    });
                    buttonPanel.add(sendButton);

                    JButton clearButton = new JButton("Limpar");
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
