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

  public static enum ChatCharacter {
    AIDEFENDANT("EaselMind", "aidefendant.txt"),
    AIWITNESS("ARPA", "aiwitness.txt"),
    HUMANWITNESS("Jean-Luc", "humanwitness.txt");

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

  private static ChatService chatServiceInstance;

  public static ChatService get() {
    if (chatServiceInstance == null) {
      chatServiceInstance = new ChatService();
    }
    return chatServiceInstance;
  }

  public static void reset() {
    chatServiceInstance = new ChatService();
  }

  private String transcript;

  private Map<ChatCharacter, ChatCompletionRequest> chatCompletionMap = new HashMap<>();
  private Map<ChatCharacter, Boolean> didChatMap = new HashMap<>();

  public ChatService() {
    didChatMap.put(ChatCharacter.AIDEFENDANT, false);
    didChatMap.put(ChatCharacter.AIWITNESS, false);
    didChatMap.put(ChatCharacter.HUMANWITNESS, false);
    for (ChatCharacter character : ChatCharacter.values()) {
      try {
        ApiProxyConfig config = ApiProxyConfig.readConfig();
        chatCompletionMap.put(
            character,
            new ChatCompletionRequest(config)
                .setN(1)
                .setTemperature(0.2)
                .setTopP(0.5)
                .setModel(Model.GPT_4_1_MINI)
                .setMaxTokens(300));
      } catch (ApiProxyException e) {
        System.out.println("Api proxy exception");
        e.printStackTrace();
      }
      ChatCompletionRequest chatCompletion = chatCompletionMap.get(character);
      Map<String, String> entryMap = new HashMap<>();
      entryMap.put("character", character.getDisplayName());
      Map<String, String> emptyMap = new HashMap<>();
      chatCompletion.addMessage(
          "system", PromptEngineering.getPrompt("generalcontextprefix.txt", emptyMap));
      chatCompletion.addMessage(
          "system", PromptEngineering.getPrompt(character.getCharacterPromptId(), emptyMap));
      chatCompletion.addMessage(
          "system", PromptEngineering.getPrompt("generalcontextsuffix.txt", entryMap));
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
    // Add a message to the chat transcript for a particular AI character
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
    didChatMap.put(character, true);
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
              // Remove the [something] tag if it exists  to sanitise the string
              return resultMessage.getContent().replaceFirst("^\\[[^\\]]+\\]\\s*", "");
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

  public boolean readyToMakeVerdict() {
    for (Map.Entry<ChatCharacter, Boolean> entry : didChatMap.entrySet()) {
      if (entry.getValue() == false) {
        return false;
      }
    }
    return true;
  }
}
