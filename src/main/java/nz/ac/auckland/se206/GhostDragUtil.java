package nz.ac.auckland.se206;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.util.Duration;

public class GhostDragUtil {
  public static SequentialTransition createGhostDragAnimation(
      Group ghostGroup, Node origin, Node target) {
    // Calculate the scene coordinates of the origin and target nodes
    double x1 = origin.localToScene(0, 0).getX();
    double y1 = origin.localToScene(0, 0).getY();
    double x2 = target.localToScene(0, 0).getX();
    double y2 = target.localToScene(0, 0).getY();

    // Create a translate transition to move the ghost group
    TranslateTransition move = new TranslateTransition(Duration.seconds(1.5), ghostGroup);
    move.setToX(x2 - x1);
    move.setToY(y2 - y1);
    move.setInterpolator(Interpolator.EASE_BOTH);

    // Create a fade-out transition
    FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.4), ghostGroup);
    fadeOut.setFromValue(1.0);
    fadeOut.setToValue(0.0);

    // Create a pause transition to reset position
    PauseTransition reset = new PauseTransition(Duration.seconds(0.1));
    reset.setOnFinished(
        e -> {
          ghostGroup.setTranslateX(0);
          ghostGroup.setTranslateY(0);
        });

    // Create a fade-in transition
    FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.4), ghostGroup);
    fadeIn.setFromValue(0.0);
    fadeIn.setToValue(1.0);

    // Combine all transitions into a sequential transition
    SequentialTransition sequence = new SequentialTransition(move, fadeOut, reset, fadeIn);
    sequence.setCycleCount(Animation.INDEFINITE);
    sequence.play();

    return sequence;
  }
}
