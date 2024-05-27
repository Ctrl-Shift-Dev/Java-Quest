package factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionFactory {
    private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());

    public Connection getConnection() {
        String url = "jdbc:mysql://localhost:3306/livros";
        String user = "root";
        String password = "apijava";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            LOGGER.log(Level.INFO, "Conexão com o banco de dados estabelecida com sucesso.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao estabelecer conexão com o banco de dados.", e);
        }
        return conn;
    }
}
