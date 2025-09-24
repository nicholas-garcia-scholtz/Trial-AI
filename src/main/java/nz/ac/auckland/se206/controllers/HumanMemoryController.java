package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
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

  public ImageView canvasBoundsTarget;
  private int layerCount = 0;

  @FXML
  public void initialize() {
    new BubbleDragUtil(bubble1, this, "A balloon festival held in Jean-Luc's home town.");
    new BubbleDragUtil(bubble2, this, "Young Jean-Luc admires a painting at his local gallery.");
    new BubbleDragUtil(bubble3, this, "Young Jean-Luc floats a paper boat on a rainy day.");
    canvasBoundsTarget = artworkLayer1;
  }

  @FXML
  void onBtnSendClicked() {
    ChatService.get().addPlayerMessage(userTextBox.getText());
    userTextBox.setText("");
  }

  @FXML
  void onBtnBackClicked() {
    try {
      App.getGame().setRoot("courtroom");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void revealLayer(ImageView bubble) {
    ImageView layer;
    if (bubble.equals(bubble1)) {
      layer = artworkLayer1;
    } else if (bubble.equals(bubble2)) {
      layer = artworkLayer2;
    } else if (bubble.equals(bubble3)) {
      layer = artworkLayer3;
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
}
