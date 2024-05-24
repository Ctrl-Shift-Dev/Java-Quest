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
                .addLine("/set system \"\"\"Here is the database schema that the SQL query will run on:")
                .addSeparator()
                .addLine(Schema.getSqlSchema())
                .addLine("```")
                .addLine("Generate a SQL query that answers the question: {" + getRequest() + "}")
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
