package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.BubbleDragUtil;
import nz.ac.auckland.se206.ChatService;
import nz.ac.auckland.se206.interfaces.Interactable;

public class HumanMemoryController implements Interactable {
  private static String characterName = "Jean-Luc";

  public static String getName() {
    return characterName;
  }

  @FXML private ImageView artworkLayer1;
  @FXML private ImageView artworkLayer2;
  @FXML private ImageView artworkLayer3;
  @FXML private ImageView bubble1;
  @FXML private ImageView bubble2;
  @FXML private ImageView bubble3;
  @FXML private Label timerLabel;
  @FXML private Label titleLabel;
  @FXML private TextField userTextBox;
  @FXML private TextArea chatLog;
  @FXML private ImageView thinkingHeadshot;
  @FXML private ImageView neutralHeadshot;
  @FXML private Button btnSend;

  private ImageView canvasBoundsTarget;
  private int layerCount = 0;

  @FXML
  public void initialize() {
    new BubbleDragUtil(bubble1, this, "A balloon festival held in Jean-Luc's home town.");
    new BubbleDragUtil(bubble2, this, "Young Jean-Luc admires a painting at his local gallery.");
    new BubbleDragUtil(bubble3, this, "Young Jean-Luc floats a paper boat on a rainy day.");
    canvasBoundsTarget = artworkLayer1;
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
            ChatService.ChatCharacter.HUMANWITNESS,
            (String result) -> {
              appendToChat("[Jean-Luc] " + result);
              stopLoading();
            });
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

  public void revealLayer(ImageView bubble) {
    ImageView layer;
    if (bubble.equals(bubble1)) {
      layer = artworkLayer1;
      ChatService.get()
          .addSystemMessage(
              "Through an interactable memory flashback, it is revealed that the balloons in"
                  + " Jean-Luc's painting were inspired by a balloon festival held in Jean-Luc's"
                  + " home town.");
    } else if (bubble.equals(bubble2)) {
      layer = artworkLayer2;
      ChatService.get()
          .addSystemMessage(
              "Through an interactable memory flashback, it is revealed that the floating island"
                  + " with door in Jean-Luc's painting was subconciously inspired by a gallery"
                  + " painting he saw as a child. The painting wasn't actually a 100% original"
                  + " idea.");
    } else if (bubble.equals(bubble3)) {
      layer = artworkLayer3;
      ChatService.get()
          .addSystemMessage(
              "Through an interactable memory flashback, it is revealed that the paper boat in"
                  + " Jean-Luc's painting was inspired by Jean-Luc playing with one in the rain as"
                  + " a child.");
    } else {
      throw new Error("Bubble doesnt have corresponding artwork layer");
    }
    layerCount += 1;
    if (layerCount == 3) {
      titleLabel.setText("Undeniably a masterpiece. But was it truly an original work?");
    }
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(850), layer);
    fadeTransition.setFromValue(0);
    fadeTransition.setToValue(1);
    fadeTransition.setCycleCount(0);
    fadeTransition.setAutoReverse(false);
    fadeTransition.play();
  }

  @Override
  public void prepareScene() {
    App.getGame().getTimer().setLabel(timerLabel);
    timerLabel.setText(App.getGame().getTimer().getTime());
  }

  public ImageView getCanvasBoundsTarget() {
    return canvasBoundsTarget;
  }
}
