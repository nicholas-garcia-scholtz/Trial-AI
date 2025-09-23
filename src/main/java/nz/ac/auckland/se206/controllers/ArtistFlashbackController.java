package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ChatLogs;
import nz.ac.auckland.se206.interfaces.Flashback;
import nz.ac.auckland.se206.interfaces.Interactable;

public class ArtistFlashbackController implements Flashback, Interactable {
  private static String characterName = "Artist";

  public static String getName() {
    return characterName;
  }

  @FXML private Label timerLabel;
  @FXML private Button btnBack;
  @FXML private Button btnSend;
  @FXML private TextArea chatLog;
  @FXML private TextField userTextBox;

  @FXML
  public void initialize() {
    // Set up the AI
    App.getGame().getChatLogs().setChat(chatLog, userTextBox, characterName);
    List<ChatMessage> logs = ChatLogs.getChatMessages();
    for (ChatMessage log : logs) {
      if (log.getRole().equals("assistant")) {
        chatLog.appendText(characterName + ": " + log.getContent() + "\n\n");
      } else {
        chatLog.appendText(log.getRole() + ": " + log.getContent() + "\n\n");
      }
    }
  }

  @FXML
  private void onBtnBackClicked() {
    try {
      App.getGame().setRoot("courtroom");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void onBtnSendClicked() {
    try {
      App.getGame().getChatLogs().onSendMessage(userTextBox.getText().trim());
    } catch (ApiProxyException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void prepareScene() {
    // Set up the timer
    App.getGame().getTimer().setLabel(timerLabel);
    timerLabel.setText(App.getGame().getTimer().getTime());

    // Set up the AI
    App.getGame().getChatLogs().setChat(chatLog, userTextBox, characterName);
    List<ChatMessage> chatLogHistory = ChatLogs.getTrialMessages();
    for (ChatMessage log : chatLogHistory) {
      App.getGame().getChatLogs().addMessageToChatCompletionRequest(log);
    }
  }
}
