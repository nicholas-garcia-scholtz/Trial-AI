package nz.ac.auckland.se206;

import java.util.HashMap;
import java.util.Map;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;

public class ChatService {
  private static Map<ChatCharacter, ChatCompletionRequest> chatCompletionMap = new HashMap<>();
  private static ChatService chatServiceInstance;
  private static String transcript;

  public static enum ChatCharacter {
    AIDEFENDANT("EaselMind"),
    AIWITNESS("ARPA"),
    HUMANWITNESS("Jean-Luc");

    private final String displayName;

    ChatCharacter(String displayName) {
      this.displayName = displayName;
    }

    public String getDisplayName() {
      return displayName;
    }
  }

  public static ChatService get() {
    if (chatServiceInstance == null) {
      chatServiceInstance = new ChatService();
    }
    return chatServiceInstance;
  }

  public ChatService() {
    chatCompletionMap.put(ChatCharacter.AIDEFENDANT, new ChatCompletionRequest(null));
    chatCompletionMap.put(ChatCharacter.AIWITNESS, new ChatCompletionRequest(null));
    chatCompletionMap.put(ChatCharacter.HUMANWITNESS, new ChatCompletionRequest(null));
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

  public void generateCharacterResponse(ChatService.ChatCharacter character) {
    ChatCompletionRequest completionRequest = chatCompletionMap.get(character);
  }
}
