package nz.ac.auckland.se206;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.concurrent.Task;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest.Model;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.prompts.PromptEngineering;

public class ChatService {
  private static ChatService chatServiceInstance;
  private Map<ChatCharacter, ChatCompletionRequest> chatCompletionMap = new HashMap<>();
  private String transcript;

  public static enum ChatCharacter {
    AIDEFENDANT("EaselMind", "aidefendant"),
    AIWITNESS("ARPA", "aiwitness"),
    HUMANWITNESS("Jean-Luc", "humanwitness");

    private final String displayName;
    private final String characterPromptId;

    ChatCharacter(String displayName, String characterPromptId) {
      this.displayName = displayName;
      this.characterPromptId = characterPromptId;
    }

    public String getDisplayName() {
      return displayName;
    }

    public String getCharacterPromptId() {
      return characterPromptId;
    }
  }

  public static ChatService get() {
    if (chatServiceInstance == null) {
      chatServiceInstance = new ChatService();
    }
    return chatServiceInstance;
  }

  public static void reset() {
    chatServiceInstance = new ChatService();
  }

  public ChatService() {
    for (Map.Entry<ChatCharacter, ChatCompletionRequest> entry : chatCompletionMap.entrySet()) {
      try {
        ApiProxyConfig config = ApiProxyConfig.readConfig();
        chatCompletionMap.put(
            entry.getKey(),
            new ChatCompletionRequest(config)
                .setN(1)
                .setTemperature(0.2)
                .setTopP(0.5)
                .setModel(Model.GPT_4_1_MINI)
                .setMaxTokens(100));
      } catch (ApiProxyException e) {
        System.out.println("Api proxy exception");
        e.printStackTrace();
      }
      ChatCompletionRequest chatCompletion = chatCompletionMap.get(entry.getKey());
      Map<String, String> entryMap = new HashMap<>();
      entryMap.put("character", entry.getKey().getDisplayName());
      chatCompletion.addMessage(
          "system", PromptEngineering.getPrompt("generalcontextprefix", null));
      chatCompletion.addMessage(
          "system", PromptEngineering.getPrompt(entry.getKey().getCharacterPromptId(), null));
      chatCompletion.addMessage(
          "system", PromptEngineering.getPrompt("generalcontextsuffix", entryMap));
    }
  }

  public void addSystemMessage(String message) {
    transcript += "\n[System] " + message;
    for (Map.Entry<ChatCharacter, ChatCompletionRequest> entry : chatCompletionMap.entrySet()) {
      ChatMessage newMessage = new ChatMessage("user", "[System] " + message);
      entry.getValue().addMessage(newMessage);
    }
  }

  public void addPlayerMessage(String message) {
    transcript += "\n[User] " + message;
    for (Map.Entry<ChatCharacter, ChatCompletionRequest> entry : chatCompletionMap.entrySet()) {
      ChatMessage newMessage = new ChatMessage("user", "[User] " + message);
      entry.getValue().addMessage(newMessage);
    }
  }

  public void addCharacterMessage(ChatService.ChatCharacter character, String message) {
    transcript += "\n[" + character.getDisplayName() + "] " + message;
    for (Map.Entry<ChatCharacter, ChatCompletionRequest> entry : chatCompletionMap.entrySet()) {
      String openAiRole = "user";
      if (character == entry.getKey()) {
        openAiRole = "assistant";
      }
      ChatMessage newMessage =
          new ChatMessage(openAiRole, "[" + character.getDisplayName() + "] " + message);
      entry.getValue().addMessage(newMessage);
    }
  }

  public void generateCharacterResponse(
      ChatService.ChatCharacter character, Consumer<String> callback) {
    Task<String> task =
        new Task<>() {
          @Override
          protected String call() throws Exception {
            try {
              ChatCompletionRequest chatCompletion = chatCompletionMap.get(character);
              ChatCompletionResult chatCompletionResult = chatCompletion.execute();
              ChatMessage resultMessage =
                  chatCompletionResult.getChoices().iterator().next().getChatMessage();
              addCharacterMessage(character, resultMessage.getContent());
              return resultMessage.getContent();
            } catch (ApiProxyException e) {
              e.printStackTrace();
            }
            return null;
          }
        };

    task.setOnSucceeded(e -> callback.accept(task.getValue()));
    new Thread(task).start();
  }

  public String getTranscript() {
    return transcript;
  }
}
