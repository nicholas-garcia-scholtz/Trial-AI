package nz.ac.auckland.se206;

import javafx.animation.FadeTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import nz.ac.auckland.se206.controllers.HumanMemoryController;

public class BubbleDragUtil {
  private double mouseOriginX;
  private double mouseOriginY;
  private double startX;
  private double startY;
  private ImageView bubble;
  private HumanMemoryController humanMemoryController;
  private boolean debounce = false;

  public BubbleDragUtil(ImageView bubble, HumanMemoryController humanMemoryController) {
    this.bubble = bubble;

    bubble.setOnMousePressed(
        event -> {
          if (debounce) {
            return;
          }
          mouseOriginX = event.getX();
          mouseOriginY = event.getY();
          startX = bubble.getLayoutX();
          startY = bubble.getLayoutY();
          bubble.toFront();
        });

    bubble.setOnMouseDragged(
        event -> {
          if (debounce) {
            return;
          }
          bubble.setLayoutX(event.getSceneX() - mouseOriginX);
          bubble.setLayoutY(event.getSceneY() - mouseOriginY);
        });

    bubble.setOnMouseReleased(
        event -> {
          if (debounce) {
            return;
          }
          if (true) {
            fadeOutAndHide();
            debounce = true;
          } else {
            bubble.setLayoutX(startX);
            bubble.setLayoutY(startY);
          }
        });
  }

  private void fadeOutAndHide() {
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), bubble);
    fadeTransition.setFromValue(1);
    fadeTransition.setToValue(0);
    fadeTransition.setCycleCount(1);
    fadeTransition.setAutoReverse(false);
    fadeTransition.setOnFinished(e -> bubble.setVisible(false));
    fadeTransition.play();
  }
}
