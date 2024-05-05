package factory;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    public Connection getConnection(){

        try{
            Dotenv dotenv = Dotenv.load();
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/world","root", dotenv.get("SQL_PASSWORD"));
        }
        catch (SQLException exception){
            throw new RuntimeException(exception);
        }
    }
}
