package nz.ac.auckland.se206.controllers;


import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ChatService;
import nz.ac.auckland.se206.interfaces.Interactable;


public class AiAuditorMemoryController implements Interactable {


  @FXML private Label timerLabel;
  @FXML private Button btnBack;
  @FXML private Button btnSend;
  @FXML private TextArea chatLog;
  @FXML private TextField userTextBox;
  @FXML private ScrollBar timelineScrollBar;
  @FXML private ImageView humanTimeline;
  @FXML private ImageView humanTimelineAligned;
  @FXML private Text slideInstructionText;
  @FXML private Text timelinesAlignedText;
  @FXML private ImageView aiExclamation;
  @FXML private ImageView humanExclamation;
  @FXML private ImageView thinkingHeadshot;
  @FXML private ImageView neutralHeadshot;


  @FXML
  public void initialize() {


    if (timelineScrollBar != null && humanTimeline != null && humanTimelineAligned != null) {
      timelineScrollBar.setMin(0);
      timelineScrollBar.setMax(220);
      timelineScrollBar.setValue(0);


      humanTimelineAligned.setVisible(false); // aligned timeline hidden initially
      timelinesAlignedText.setVisible(false); // aligned message hidden initially
      aiExclamation.setVisible(false); // hide AI exclamation initially
      humanExclamation.setVisible(false); // hide Human exclamation initially


      timelineScrollBar
          .valueProperty()
          .addListener(
              (obs, oldVal, newVal) -> {
                humanTimeline.setTranslateX(newVal.doubleValue());


                if (newVal.doubleValue() >= timelineScrollBar.getMax()) {
                  humanTimeline.setVisible(false);
                  humanTimelineAligned.setVisible(true);


                  timelineScrollBar.setVisible(false); // hide the scrollbar
                  slideInstructionText.setVisible(false); // hide instruction
                  timelinesAlignedText.setVisible(true); // show "timelines aligned" text


                  aiExclamation.setVisible(true); // show AI exclamation
                  humanExclamation.setVisible(true); // show Human exclamation
                } else {
                  humanTimeline.setVisible(true);
                  humanTimelineAligned.setVisible(false);


                  timelineScrollBar.setVisible(true);
                  slideInstructionText.setVisible(true);
                  timelinesAlignedText.setVisible(false);


                  aiExclamation.setVisible(false);
                  humanExclamation.setVisible(false);
                }
              });
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
    // When the send button is clicked, send the message to the LLM
    startLoading();
    ChatService.get().addPlayerMessage(userTextBox.getText());
    appendToChat("[You] " + userTextBox.getText());
    userTextBox.setText("");
    ChatService.get()
        .generateCharacterResponse(
            ChatService.ChatCharacter.AIWITNESS,
            (String result) -> {
              appendToChat("[ARPA] " + result);
              stopLoading();
            });
  }

  private void startLoading() {
    thinkingHeadshot.setVisible(true);
    neutralHeadshot.setVisible(false);
    btnSend.setDisable(true);
    userTextBox.setDisable(true);
  }

  private void stopLoading() {
    thinkingHeadshot.setVisible(false);
    neutralHeadshot.setVisible(true);
    btnSend.setDisable(false);
    userTextBox.setDisable(false);
  }

  private void appendToChat(String message) {
    Platform.runLater(() -> chatLog.positionCaret(chatLog.getLength()));
    chatLog.setText(chatLog.getText() + "\n\n" + message);
  }

  @Override
  public void prepareScene() {
    // Set up the timer
    App.getGame().getTimer().setLabel(timerLabel);
    timerLabel.setText(App.getGame().getTimer().getTime());
  }
}



