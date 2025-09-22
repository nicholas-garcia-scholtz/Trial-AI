package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
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
}
