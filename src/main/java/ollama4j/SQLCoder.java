package ollama4j;

import factory.ConnectionFactory;
import factory.Schema;
import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.models.OllamaResult;
import io.github.amithkoujalgi.ollama4j.core.utils.OptionsBuilder;
import io.github.amithkoujalgi.ollama4j.core.utils.PromptBuilder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLCoder {

    private String request;
    private final String databaseType;
    private String aiType;

    public SQLCoder(String databaseType, String aiType) {
        this.databaseType = databaseType;
        this.aiType = aiType;
    }

    public SQLCoder(String databaseType) {
        this.databaseType = databaseType;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getRequest() {
        return request;
    }

    public String aiAnswer() throws OllamaBaseException, IOException, InterruptedException, SQLException {
        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);
        ollamaAPI.setRequestTimeoutSeconds(100000);

        try (Connection connection = new ConnectionFactory(databaseType).getConnection()) {
            Schema schema = new Schema(connection);
            schema.generateDatabaseSchema();
            String sqlSchema = Schema.getSqlSchema();

            // Convert aiType String to corresponding model name
            String modelName = getModelNameFromString(aiType);

            PromptBuilder promptBuilder = new PromptBuilder()
            .addLine("### Instructions:")
            .addLine("Your task is to convert a question into a SQL query, given a Postgres database schema.")
            .addLine("Adhere to these rules:")
            .addLine("- **Deliberately go through the question and database schema word by word** to appropriately answer the question")
            .addLine("- **Use Table Aliases** to prevent ambiguity. For example, `SELECT table1.col1, table2.col1 FROM table1 JOIN table2 ON table1.id = table2.id`.")
            .addLine("- When creating a ratio, always cast the numerator as float")
            .addLine("")
            .addLine("### Input:")
            .addLine("Generate a SQL query that answers the question {" + getRequest() + "}.")
            .addLine("This query will run on a database whose schema is represented in this string:")
            .addLine(sqlSchema)
            .addLine("")
            .addLine("### Response:")
            .addLine("Based on your instructions, here is the SQL query I have generated to answer the question {" + getRequest() + "}:")
            .addLine("```sql")
            .addLine("```");        

            OllamaResult result = ollamaAPI.generate(modelName, promptBuilder.build(), new OptionsBuilder().build());

            return result.getResponse();
        }
    }

    private String getModelNameFromString(String aiType) {
        return switch (aiType.toUpperCase()) {
            case "NSQL" -> "duckdb-nsql";
            case "SQLCODER" -> "sqlcoder";
            default -> throw new IllegalArgumentException("Tipo de IA não reconhecido: " + aiType);
        };
    }

    @Override
    public String toString() {
        try {
            return aiAnswer();
        } catch (OllamaBaseException | InterruptedException | IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
