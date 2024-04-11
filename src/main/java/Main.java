import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

public class Main {

    public static void main(String[] args){

        ChatLanguageModel model =  OllamaChatModel.builder()
                .baseUrl("http://localhost:11434/")
                .modelName("sqlcoder")
                .build();
        String answer =  model.generate("Create a SQL that creates a data base.");
        System.out.println(answer);

    }
}