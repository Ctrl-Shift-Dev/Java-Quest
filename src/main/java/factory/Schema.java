package factory;

import java.sql.*;
import java.util.ArrayList;

public class Schema {
    private static String sqlSchema;

    public static ArrayList<String> selectSchemas(ConnectionFactory connectionFactory){

        ArrayList<String> schemas = new ArrayList<>();

        try(Connection connection = connectionFactory.getConnection()) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet resultSet = databaseMetaData.getCatalogs();

            while (resultSet.next()){
                String schemaName = resultSet.getString(1);

            }
            resultSet.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return schemas;
    }

    public void dbSchema(String dbName, ConnectionFactory connectionFactory) {
        try(Connection connection = DriverManager.getConnection(connectionFactory.getUrl() + "/" + dbName,
                connectionFactory.getUser(), connectionFactory.getPassword())) {

            StringBuilder schemaBuilder = new StringBuilder();
            DatabaseMetaData metaData = connection.getMetaData();
            String schemaPattern = connection.getSchema();

            ResultSet tablesResultSet = metaData.getTables(null, schemaPattern, "%", new String[]{"TABLE"});
            while (tablesResultSet.next()) {
                String tableName = tablesResultSet.getString("TABLE_NAME");
                schemaBuilder.append("CREATE TABLE ").append(tableName).append(" (\n");

                ResultSet columnsResultSet = metaData.getColumns(null, schemaPattern, tableName, "%");
                while (columnsResultSet.next()) {
                    String columnName = columnsResultSet.getString("COLUMN_NAME");
                    String columnType = columnsResultSet.getString("TYPE_NAME");
                    schemaBuilder.append("  ").append(columnName).append(" ").append(columnType);

                    int columnSize = columnsResultSet.getInt("COLUMN_SIZE");
                    schemaBuilder.append("(").append(columnSize).append(")");

                    String isNullable = columnsResultSet.getString("IS_NULLABLE");
                    if ("NO".equals(isNullable)) {
                        schemaBuilder.append(" NOT NULL");
                    }

                    schemaBuilder.append(",\n");
                }
                columnsResultSet.close();

                schemaBuilder.setLength(schemaBuilder.length() - 2);
                schemaBuilder.append("\n);\n");
            }
            tablesResultSet.close();

            sqlSchema = schemaBuilder.toString();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getSqlSchema() {
        return sqlSchema;
    }

//    public static void main(String[] args) {
//        ConnectionFactory connectionFactory = new ConnectionFactory();
//        try (Connection connection = connectionFactory.getConnection()) {
//            Schema schema = new Schema(connection);
//            schema.dbSchema();
//
//            // Usa a variável sqlSchema
//            String sqlSchema = schema.getSqlSchema();
//            System.out.println(sqlSchema);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

}