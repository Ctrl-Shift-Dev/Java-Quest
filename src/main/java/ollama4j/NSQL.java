package ollama4j;

import factory.ConnectionFactory;
import factory.Schema;
import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.models.OllamaResult;
import io.github.amithkoujalgi.ollama4j.core.types.OllamaModelType;
import io.github.amithkoujalgi.ollama4j.core.utils.OptionsBuilder;
import io.github.amithkoujalgi.ollama4j.core.utils.PromptBuilder;

import java.io.IOException;
import java.sql.PreparedStatement;

public class NSQL {


    private String request;

    public void setRequest(String request) {
        this.request = request;
    }

    public String getRequest() {
        return request;
    }


    public String aiAnswer() throws OllamaBaseException, IOException, InterruptedException {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        ollamaAPI.setRequestTimeoutSeconds(100000);

        PromptBuilder promptBuilder = new PromptBuilder()
                .addLine("You are an expert data engineering")
                .addLine("Given a question follow this sentence:")
                .addLine("Generate a SQL query that answers the question: {" + getRequest() + "}")
                .addSeparator()
                .addLine("This query will run on a database whose schema is represented in this string:")
                .addLine("```sql")
                .addLine(Schema.getSqlSchema())
                .addLine("```")
                .addSeparator();

        OllamaResult result = ollamaAPI.generate(OllamaModelType.DUCKDB_NSQL, promptBuilder.build(), new OptionsBuilder().build());

        return result.getResponse();
    }

    @Override
    public String toString() {
        try {
            return aiAnswer();
        } catch (OllamaBaseException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
