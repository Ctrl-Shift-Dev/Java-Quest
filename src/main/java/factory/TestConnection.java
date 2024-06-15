package factory;
import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {

    public static void main(String[] args) throws SQLException {

        String databaseType = "Livros";

        try {
            Connection connection = new ConnectionFactory(databaseType).getConnection();
            if (connection != null) {
                System.out.println("Conexão aberta!");
                connection.close();

            } else {
                System.out.println("Falha na conexão");

            }
        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());

        }
    }
}
