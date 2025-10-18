package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ChatService;
import nz.ac.auckland.se206.TextAreaSubmitUtil;
import nz.ac.auckland.se206.interfaces.Interactable;

public class AiAuditorMemoryController implements Interactable {
  private static String characterName = "AI Provenance Auditor";

  public static String getName() {
    return characterName;
  }

  @FXML private Label timerLabel;
  @FXML private Button btnBack;
  @FXML private Button btnSend;
  @FXML private TextArea chatLog;
  @FXML private TextArea userTextBox;
  @FXML private ScrollBar timelineScrollBar;
  @FXML private ImageView humanTimeline;
  @FXML private ImageView humanTimelineAligned;
  @FXML private Text slideInstructionText;
  @FXML private Text timelinesAlignedText;
  @FXML private ImageView aiExclamation;
  @FXML private ImageView humanExclamation;
  @FXML private ImageView thinkingHeadshot;
  @FXML private ImageView neutralHeadshot;
  private boolean finishedDebounce = false;

  @FXML
  public void initialize() {

    TextAreaSubmitUtil.bindEnterSubmit(userTextBox, () -> onBtnSendClicked());

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
                if (finishedDebounce) {
                  return;
                }
                humanTimeline.setTranslateX(newVal.doubleValue());

                if (newVal.doubleValue() >= timelineScrollBar.getMax()) {
                  if (!finishedDebounce) {
                    finishedDebounce = true;
                    humanTimeline.setVisible(false);
                    humanTimelineAligned.setVisible(true);

                    timelineScrollBar.setVisible(false); // hide the scrollbar
                    slideInstructionText.setVisible(false); // hide instruction
                    timelinesAlignedText.setVisible(true); // show "timelines aligned" text

                    aiExclamation.setVisible(true); // show AI exclamation
                    humanExclamation.setVisible(true); // show Human exclamation

                    ChatService.get()
                        .addSystemMessage(
                            "Through ARPA's interactable memory flashback, the user aligns a"
                                + " timeline of EaselMind and Jean-Luc's actions. AI Provenance"
                                + " Auditor timeline shows dataset preparation in July, AI"
                                + " ingesting training data in August (important moment), AI"
                                + " generating test images in September, and the AI version"
                                + " releasing in October. The Human Artist timeline shows artwork"
                                + " completed in the studio in September, scanned for digital"
                                + " format in October, and first published online in November"
                                + " (important moment). The AI training and data ingestion happened"
                                + " before the human artist's work was publicly posted.");
                    String message =
                        "Through my analysis it is clear that EaselMind ingested its training data"
                            + " before Jean-Luc first published his painting online. EaselMind"
                            + " plagiarising Jean-Luc is unlikely.";
                    ChatService.get()
                        .addCharacterMessage(ChatService.ChatCharacter.AIWITNESS, message);
                    appendToChat("[ARPA] " + message);
                  }
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
    TextAreaSubmitUtil.clearTextAreaEvents(chatLog);
  }

  @FXML
  private void onBtnBackClicked() {
    try {
      App.getGame().setRoot("courtroom");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void startLoading() {
    neutralHeadshot.setVisible(false);
    thinkingHeadshot.setVisible(true);

    userTextBox.setDisable(true);
    btnSend.setDisable(true);
  }

  @FXML
  private void onBtnSendClicked() {
    // When the send button is clicked, send the message to the LLM
    startLoading();
    appendToChat("[You] " + userTextBox.getText());
    ChatService.get().addPlayerMessage(userTextBox.getText());

    userTextBox.setText("");
    ChatService.get()
        .generateCharacterResponse(
            ChatService.ChatCharacter.AIWITNESS,
            (String result) -> {
              appendToChat("[ARPA] " + result);
              stopLoading();
            });
  }

  private void appendToChat(String message) {
    Platform.runLater(() -> chatLog.positionCaret(chatLog.getLength()));

    chatLog.setText(chatLog.getText() + "\n\n" + message);
  }

  private void stopLoading() {
    neutralHeadshot.setVisible(true);
    thinkingHeadshot.setVisible(false);

    userTextBox.setDisable(false);
    btnSend.setDisable(false);
  }

  @Override
  public void prepareScene() {
    // Set up the timer!!!
    App.getGame().getTimer().setLabel(timerLabel);
    timerLabel.setText(App.getGame().getTimer().getTime());
  }
}
