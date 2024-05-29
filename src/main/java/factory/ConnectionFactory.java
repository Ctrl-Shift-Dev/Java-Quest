package factory;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class ConnectionFactory {

    private String url;
    private String user;
    private String password;

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public ConnectionFactory() {
        runProperties();

    }

    public void runProperties() {

        Properties properties = new Properties();

        try (FileInputStream file = new FileInputStream("src/main/resources/info.properties")) {
            properties.load(file);
            url = properties.getProperty("url");
            user = properties.getProperty("user");
            password = properties.getProperty("password");

        } catch (IOException e) {
            System.out.println("Erro: " + e.getMessage());

        }
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }

    public static void main(String[] args) {
        try {
            Connection connection = new ConnectionFactory().getConnection();
            if (connection != null) {
                System.out.println("Conexão aberta!");
                connection.close();

            } else {
                System.out.println("Falha na concexão");

            }
        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());

        }
    }
}
