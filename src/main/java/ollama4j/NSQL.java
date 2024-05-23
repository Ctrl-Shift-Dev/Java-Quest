package ollama4j;

import factory.ConnectionFactory;
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
                .addLine(" CREATE TABLE `city`")
                .addLine(" `ID` int(11) NOT NULL AUTO_INCREMENT")
                .addLine(" `Name` char(35) NOT NULL DEFAULT")
                .addLine(" `CountryCode` char(3) NOT NULL DEFAULT")
                .addLine(" `District` char(20) NOT NULL DEFAULT")
                .addLine(" `Population` int(11) NOT NULL DEFAULT 0")
                .addLine("  PRIMARY KEY (`ID`)")
                .addLine(" CREATE TABLE `country`")
                .addLine("  `Code` char(3) NOT NULL DEFAULT")
                .addLine("  `Name` char(52) NOT NULL DEFAULT")
                .addLine("  `Continent` enum('Asia','Europe','North America','Africa','Oceania','Antarctica','South America') NOT NULL DEFAULT 'Asia'")
                .addLine("  `Region` char(26) NOT NULL DEFAULT")
                .addLine("  `SurfaceArea` float(10,2) NOT NULL DEFAULT 0.00")
                .addLine("  `IndepYear` smallint(6) DEFAULT NULL")
                .addLine("  `Population` int(11) NOT NULL DEFAULT 0")
                .addLine("  `LifeExpectancy` float(3,1) DEFAULT NULL")
                .addLine("  `GNP` float(10,2) DEFAULT NULL")
                .addLine("  `GNPOld` float(10,2) DEFAULT NULL")
                .addLine("  `LocalName` char(45) NOT NULL DEFAULT")
                .addLine("  `GovernmentForm` char(45) NOT NULL DEFAULT")
                .addLine("  `HeadOfState` char(60) DEFAULT NULL")
                .addLine("  `Capital` int(11) DEFAULT NULL")
                .addLine("  `Code2` char(2) NOT NULL DEFAULT")
                .addLine("  PRIMARY KEY (`Code`)")
                .addLine(" CREATE TABLE `countrylanguage`")
                .addLine("  `CountryCode` char(3) NOT NULL DEFAULT")
                .addLine("  `Language` char(30) NOT NULL DEFAULT")
                .addLine("  `IsOfficial` enum('T','F') NOT NULL DEFAULT 'F'")
                .addLine("  `Percentage` float(4,1) NOT NULL DEFAULT 0.0")
                .addLine("  PRIMARY KEY (`CountryCode`,`Language`)")
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
