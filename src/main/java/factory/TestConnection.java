package factory;
import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {

    public static void main(String[] args) throws SQLException {

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
