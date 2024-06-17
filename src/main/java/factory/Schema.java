package factory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Schema {
    private final Connection connection;
    private static String sqlSchema;

    public Schema(Connection connection) {
        this.connection = connection;
    }

    public void generateDatabaseSchema() throws SQLException {
        StringBuilder schemaBuilder = new StringBuilder();
        DatabaseMetaData metaData = connection.getMetaData();

        String catalog = connection.getCatalog();
        String schemaPattern = connection.getSchema();

        ResultSet tablesResultSet = metaData.getTables(catalog, schemaPattern, "%", new String[]{"TABLE"});
        while (tablesResultSet.next()) {
            String tableName = tablesResultSet.getString("TABLE_NAME");
            schemaBuilder.append("CREATE TABLE ").append(tableName).append(" (\n");

            ResultSet columnsResultSet = metaData.getColumns(catalog, schemaPattern, tableName, "%");
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
    }

    public static String getSqlSchema() {
        return sqlSchema;
    }

}
