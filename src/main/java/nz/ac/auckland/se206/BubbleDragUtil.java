package nz.ac.auckland.se206;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Popup;
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
  private final int tooltipOffset = 14;

  public BubbleDragUtil(
      ImageView bubble, HumanMemoryController humanMemoryController, String tooltipText) {
    this.bubble = bubble;
    this.humanMemoryController = humanMemoryController;

    // Install a tooltip onto the bubble
    Popup popup = new Popup();
    Label tooltipLabel = new Label(tooltipText);
    tooltipLabel.setStyle(
        "-fx-background-color: black; "
            + "-fx-text-fill: #3aff47; "
            + "-fx-font-size: 12px; "
            + "-fx-padding: 6 8 6 8;");
    popup.getContent().add(tooltipLabel);

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
          humanMemoryController.hideGhostAnimation();
          bubble.setLayoutX(event.getSceneX() - mouseOriginX);
          bubble.setLayoutY(event.getSceneY() - mouseOriginY);
          popup.show(
              bubble, event.getScreenX() + tooltipOffset, event.getScreenY() + tooltipOffset);
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
            humanMemoryController.revealLayer(bubble);
            popup.hide();
          } else {
            bubble.setLayoutX(startX);
            bubble.setLayoutY(startY);
          }
        });

    // Move tooltip with cursor
    bubble.setOnMouseMoved(
        e -> {
          if (debounce) {
            return;
          }
          popup.show(bubble, e.getScreenX() + tooltipOffset, e.getScreenY() + tooltipOffset);
        });

    // When the user hovers, emphasise by increasing scale, and show tooltip
    bubble.setOnMouseEntered(
        event -> {
          Bounds b = bubble.localToScreen(bubble.getBoundsInLocal());
          if (b != null) {
            popup.show(
                bubble, event.getScreenX() + tooltipOffset, event.getScreenY() + tooltipOffset);
          }
          ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), bubble);
          scaleTransition.setToX(1.05);
          scaleTransition.setToY(1.05);
          scaleTransition.play();
        });

    // When the user stops hovering, reset emphasis, and hide tooltip
    bubble.setOnMouseExited(
        event -> {
          popup.hide();
          ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), bubble);
          scaleTransition.setToX(1.0);
          scaleTransition.setToY(1.0);
          scaleTransition.play();
        });
  }

  private boolean isBubbleOnTopOfCanvas() {
    // Check if the dragged bubble is within the bounds of the painting canvas
    ImageView canvasBoundsTarget = this.humanMemoryController.getCanvasBoundsTarget();
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
    // Fade the bubble out and make it invisible
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), bubble);
    fadeTransition.setFromValue(1);
    fadeTransition.setToValue(0);
    fadeTransition.setCycleCount(1);
    fadeTransition.setAutoReverse(false);
    fadeTransition.setOnFinished(e -> bubble.setVisible(false));
    fadeTransition.play();
  }
}
