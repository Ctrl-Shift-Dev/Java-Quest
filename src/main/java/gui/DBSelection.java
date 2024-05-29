package gui;

import factory.ConnectionFactory;
import factory.Schema;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DBSelection extends JFrame {

public DBSelection(Schema schema, ConnectionFactory connectionFactory) {
    ImageIcon icon = new ImageIcon("src/img/logo_jq.png");
    setSize(600,500);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setVisible(true);
    setIconImage(icon.getImage());

    JPanel imagePanel = new JPanel();
    imagePanel.setLayout(new BorderLayout());
    ImageIcon frontImage = new ImageIcon("src/resources/img/front.logo.png"); // Substitua pelo caminho da sua imagem
    JLabel imageLabel = new JLabel(frontImage);
    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    imagePanel.add(imageLabel, BorderLayout.CENTER);

    // Cria painel de seleção de banco de dados
    JPanel dbPanel = new JPanel();
    dbPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    dbPanel.setBackground(Color.darkGray); // Define a cor de fundo para coincidir com o frame principal
    dbPanel.setForeground(Color.white);    // Define a cor do texto para coincidir com o frame principal

    ArrayList<String> schemaList = Schema.selectSchemas(connectionFactory);
    JComboBox<String> dbSelection = new JComboBox<>(schemaList.toArray(new String[0])); // Substitua pelos bancos de dados reais
    dbSelection.setSelectedIndex(-1); // Desselecionar por padrão
    dbPanel.add(new JLabel("Database:"));
    dbPanel.add(dbSelection);
    dbSelection.addActionListener(e -> {
        String selectedSchema = (String) dbSelection.getSelectedItem();
        schema.dbSchema(selectedSchema, connectionFactory);

    });

    JButton startButton = new JButton("Iniciar");
    startButton.setEnabled(false);
    startButton.addActionListener(e -> {
        dispose();
        SwingUtilities.invokeLater(() -> new Chat().setVisible(true));
    });

    dbSelection.addActionListener(e -> {
        startButton.setEnabled(dbSelection.getSelectedIndex() != -1);
    });

    dbPanel.add(startButton);

    JPanel contentPanel = new JPanel(new GridLayout(2, 1));
    contentPanel.add(imagePanel);
    contentPanel.add(dbPanel);

    contentPanel.setBackground(Color.darkGray);
    contentPanel.setForeground(Color.white);
    add(contentPanel, BorderLayout.CENTER);

}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(e -> {
            new DBSelection(schema);
        });
    }


}
