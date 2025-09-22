package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ChatLogs;

public class AiProvenanceAuditorMemoryController {
  private static String characterName = "AI Artist";

  public static String getName() {
    return characterName;
  }

  @FXML private Label timerLabel;
  @FXML private Button btnBack;
  @FXML private Button btnSend;
  @FXML private TextArea chatLog;
  @FXML private TextField userTextBox;
  @FXML private ScrollBar timelineScrollBar;
  @FXML private ImageView humanTimeline;

  @FXML
  public void initialize() {

    // Set up the timer
    App.getTimer().setLabel(timerLabel);
    timerLabel.setText(App.getTimer().getTime());

    // Set up the AI
    App.getChatLogs().setChat(chatLog, userTextBox, characterName);
    List<ChatMessage> logs = ChatLogs.getChatMessages();
    for (ChatMessage log : logs) {
      if (log.getRole().equals("assistant")) {
        chatLog.appendText(characterName + ": " + log.getContent() + "\n\n");
      } else {
        chatLog.appendText(log.getRole() + ": " + log.getContent() + "\n\n");
      }
    }
    List<ChatMessage> chatLogHistory = ChatLogs.getTrialMessages();
    for (ChatMessage log : chatLogHistory) {
      App.getChatLogs().addMessageToChatCompletionRequest(log);
    }

    // Set up the scrollbar to move the Human Timeline
    if (timelineScrollBar != null && humanTimeline != null) {
      timelineScrollBar.setMin(0);
      timelineScrollBar.setMax(220); // how far you want it to move
      timelineScrollBar.setValue(0);


      timelineScrollBar
          .valueProperty()
          .addListener(
              (obs, oldVal, newVal) -> {
                humanTimeline.setTranslateX(newVal.doubleValue());
              });
    }
  }

  @FXML
  private void onBtnBackClicked() {
    try {
      App.setRoot("courtroom");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void onBtnSendClicked() {
    try {
      App.getChatLogs().onSendMessage(userTextBox.getText().trim());
    } catch (ApiProxyException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}