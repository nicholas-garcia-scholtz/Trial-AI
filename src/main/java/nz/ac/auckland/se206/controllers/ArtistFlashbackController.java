package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ChatLogs;

public class ArtistFlashbackController {
  private static String characterName = "Artist";

  public static String getName() {
    return characterName;
  }

  @FXML private Label timerLabel;
  @FXML private Button btnBack;
  @FXML private Button btnSend;
  @FXML private TextArea chatLog;
  @FXML private TextField userTextBox;
  private Media artistAudio = null;
  private MediaPlayer artistAudioPlayer = null;

  @FXML
  public void initialize() {
    // Give flashback
    if (!App.getDoneFlashback(characterName)) {
      try {
        artistAudio =
            new Media(App.class.getResource("/sounds/HumanArtistAudio.mp3").toURI().toString());
        artistAudioPlayer = new MediaPlayer(artistAudio);
        artistAudioPlayer.play();
      } catch (java.net.URISyntaxException e) {
        e.printStackTrace();
      }
      App.doneFlashback(characterName);
    }

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
  }

  @FXML
  private void onBtnBackClicked() {
    // Stop playing audio if user switches scenes
    if (artistAudioPlayer != null) {
      if (artistAudioPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
        artistAudioPlayer.stop();
      }
      artistAudioPlayer.dispose();
    }
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
