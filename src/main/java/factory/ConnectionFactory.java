package factory;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionFactory {

    public Connection getConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Dotenv dotenv = Dotenv.load();

            return DriverManager.getConnection("jdbc:mysql://localhost:3306/world", "root", dotenv.get("SQL_PASSWORD"));

        } catch (SQLException e) {
            System.out.println("Erro de SQL: " + e.getMessage());

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);

        }
        return null;
    }
}
