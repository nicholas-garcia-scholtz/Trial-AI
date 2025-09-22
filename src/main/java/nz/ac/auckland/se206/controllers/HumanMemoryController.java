package nz.ac.auckland.se206.controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import nz.ac.auckland.se206.BubbleDragUtil;
import nz.ac.auckland.se206.interfaces.Interactable;

public class HumanMemoryController implements Interactable {
  @FXML private ImageView artworkLayer1;
  @FXML private ImageView artworkLayer2;
  @FXML private ImageView artworkLayer3;
  @FXML private ImageView bubble1;
  @FXML private ImageView bubble2;
  @FXML private ImageView bubble3;

  public ImageView canvasBoundsTarget;

  @FXML
  public void initialize() {
    BubbleDragUtil bubble1DragUtil = new BubbleDragUtil(bubble1, this);
    BubbleDragUtil bubble2DragUtil = new BubbleDragUtil(bubble2, this);
    BubbleDragUtil bubble3DragUtil = new BubbleDragUtil(bubble3, this);
    canvasBoundsTarget = artworkLayer1;
  }

  @FXML
  void onBtnSendClicked() {}

  @FXML
  void onBtnBackClicked() {}

  public void prepareScene() {}

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
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(850), layer);
    fadeTransition.setFromValue(0);
    fadeTransition.setToValue(1);
    fadeTransition.setCycleCount(0);
    fadeTransition.setAutoReverse(false);
    fadeTransition.play();
  }
}
