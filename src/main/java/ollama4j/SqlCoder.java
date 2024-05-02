package ollama4j;

import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.models.OllamaResult;
import io.github.amithkoujalgi.ollama4j.core.types.OllamaModelType;
import io.github.amithkoujalgi.ollama4j.core.utils.OptionsBuilder;

import java.io.IOException;

public class SqlCoder {

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

        ollamaAPI.setRequestTimeoutSeconds(10);

        OllamaResult result = ollamaAPI.generate(OllamaModelType.SQLCODER, getRequest(), new OptionsBuilder().build());

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
