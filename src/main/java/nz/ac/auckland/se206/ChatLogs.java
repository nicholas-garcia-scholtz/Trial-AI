package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest.Model;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.prompts.PromptEngineering;

public class ChatLogs {

  private static Map<String, List<ChatMessage>> msgMap = new HashMap<>();
  private static List<ChatMessage> trialMessages =
      new ArrayList<>(); // Stores all the messages of the trial
  private static List<ChatMessage> criticMessages = new ArrayList<>();
  private static List<ChatMessage> aiArtistMessages = new ArrayList<>();
  private static List<ChatMessage> artistMessages = new ArrayList<>();
  private static String currentTrialCharacter = "";

  public static List<ChatMessage> getChatMessages() {
    return msgMap.get(currentTrialCharacter);
  }

  public static List<ChatMessage> getTrialMessages() {
    return trialMessages;
  }

  private ChatCompletionRequest chatCompletionRequest;
  private TextArea txtChat;
  private TextField txtInput;

  public ChatLogs() {
    // Initialise new messages
    criticMessages = new ArrayList<>();
    aiArtistMessages = new ArrayList<>();
    artistMessages = new ArrayList<>();

    criticMessages.add(
        new ChatMessage(
            "assistant",
            "The similarity is undeniable. The question is"
                + " whether making art this close to one person’s style is ethically sound."));
    aiArtistMessages.add(
        new ChatMessage(
            "assistant",
            "I created the artwork by learning from many styles, not by copying any single piece."
                + " While the resemblance is close, my intent was to create something new, not to"
                + " replicate."));
    artistMessages.add(
        new ChatMessage(
            "assistant",
            "This piece looks almost exactly like mine — same composition, brushwork, and colours."
                + " It feels like my work was taken without respect for my effort."));

    trialMessages.add(criticMessages.get(0));
    trialMessages.add(aiArtistMessages.get(0));
    trialMessages.add(artistMessages.get(0));

    // Place the messages in the message map
    msgMap.put("AI Art Critic", criticMessages);
    msgMap.put("AI Artist", aiArtistMessages);
    msgMap.put("Artist", artistMessages);
  }

  /**
   * Sets the character for the chat context and initializes the ChatCompletionRequest.
   *
   * @param txtChat the text box
   * @param txtInput the text input
   * @param trialCharacter the trial character
   */
  public void setChat(TextArea txtChat, TextField txtInput, String trialCharacter) {
    // Set the chat and input fields along with who the current character is
    this.txtChat = txtChat;
    this.txtInput = txtInput;
    currentTrialCharacter = trialCharacter;

    // Set up GPT to inform it of its role and situation
    try {
      ApiProxyConfig config = ApiProxyConfig.readConfig();
      chatCompletionRequest =
          new ChatCompletionRequest(config)
              .setN(1)
              .setTemperature(0.2)
              .setTopP(0.5)
              .setModel(Model.GPT_4_1_MINI)
              .setMaxTokens(100);
      Thread thred =
          new Thread(
              () -> {
                try {
                  runGpt(new ChatMessage("system", getSystemPrompt()));
                } catch (ApiProxyException e) {
                  e.printStackTrace();
                }
              });
      thred.start();
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
  }

  /**
   * Adds the GPT message to the character chat log and trial log
   *
   * @param trialCharacter the character
   * @param msg the AI message
   */
  private void addToMessages(String trialCharacter, ChatMessage msg) {
    msgMap.get(trialCharacter).add(msg);
    trialMessages.add(msg);
  }

  /**
   * Generates the system prompt based on the character.
   *
   * @return the system prompt string (ie the prompt with the character subbed in)
   */
  private String getSystemPrompt() {
    Map<String, String> map = new HashMap<>();
    map.put("character", currentTrialCharacter);
    return PromptEngineering.getPrompt("chat.txt", map);
  }

  /**
   * Appends a chat message to the chat text area.
   *
   * @param msg the chat message to append
   */
  private void appendChatMessage(ChatMessage msg) {
    Platform.runLater(
        () -> {
          if (msg.getRole().equals("assistant")) {
            txtChat.appendText(currentTrialCharacter + ": " + msg.getContent() + "\n\n");
          } else {
            txtChat.appendText(msg.getRole() + ": " + msg.getContent() + "\n\n");
          }
        });
  }

  // Made so that when we initialise a controller, GPT can have a history of all the chats
  public void addMessageToChatCompletionRequest(ChatMessage msg) {
    chatCompletionRequest.addMessage(msg);
  }

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    chatCompletionRequest.addMessage(msg);
    if (msg.getRole().equals("system")) {
      return null;
    }
    try {
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      result.getChatMessage();
      chatCompletionRequest.addMessage(result.getChatMessage());
      appendChatMessage(result.getChatMessage());
      addToMessages(currentTrialCharacter, result.getChatMessage());
      return result.getChatMessage();
    } catch (ApiProxyException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Sends a message to the GPT model.
   *
   * @param message the input text
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  public void onSendMessage(String message) throws ApiProxyException, IOException {
    if (message.isEmpty()) {
      return;
    }
    Task<Void> task =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            Platform.runLater(
                () -> {
                  txtInput.clear();
                });
            ChatMessage msg = new ChatMessage("user", message);
            appendChatMessage(msg);
            addToMessages(currentTrialCharacter, msg);
            runGpt(msg);
            return null;
          }
        };

    new Thread(task).start();
  }
}
