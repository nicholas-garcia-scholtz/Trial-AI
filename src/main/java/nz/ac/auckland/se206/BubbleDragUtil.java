package nz.ac.auckland.se206;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Bounds;
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
    this.humanMemoryController = humanMemoryController;

    // When a drag is initiated:
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

    // As the bubble is dragged, update position:
    bubble.setOnMouseDragged(
        event -> {
          if (debounce) {
            return;
          }
          bubble.setLayoutX(event.getSceneX() - mouseOriginX);
          bubble.setLayoutY(event.getSceneY() - mouseOriginY);
        });

    // Handle drag ending:
    bubble.setOnMouseReleased(
        event -> {
          if (debounce) {
            return;
          }
          if (isBubbleOnTopOfCanvas()) {
            fadeOutAndHide();
            debounce = true;
          } else {
            bubble.setLayoutX(startX);
            bubble.setLayoutY(startY);
          }
        });

    // When the user hovers, emphasise by increasing scale
    bubble.setOnMouseEntered(
        e -> {
          ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), bubble);
          scaleTransition.setToX(1.05);
          scaleTransition.setToY(1.05);
          scaleTransition.play();
        });

    // When the user stops hovering, reset emphasis
    bubble.setOnMouseExited(
        e -> {
          ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), bubble);
          scaleTransition.setToX(1.0);
          scaleTransition.setToY(1.0);
          scaleTransition.play();
        });
  }

  private boolean isBubbleOnTopOfCanvas() {
    ImageView canvasBoundsTarget = this.humanMemoryController.canvasBoundsTarget;
    Bounds targetBounds = canvasBoundsTarget.localToScene(canvasBoundsTarget.getBoundsInLocal());
    Bounds bubbleBounds = bubble.localToScene(bubble.getBoundsInLocal());

    double centerX = bubbleBounds.getMinX() + bubbleBounds.getWidth() / 2.0;
    double centerY = bubbleBounds.getMinY() + bubbleBounds.getHeight() / 2.0;

    return (centerX >= targetBounds.getMinX()
        && centerX <= targetBounds.getMaxX()
        && centerY >= targetBounds.getMinY()
        && centerY <= targetBounds.getMaxY());
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
