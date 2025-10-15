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
    double x1 = origin.localToScene(0, 0).getX();
    double y1 = origin.localToScene(0, 0).getY();
    double x2 = target.localToScene(0, 0).getX();
    double y2 = target.localToScene(0, 0).getY();

    TranslateTransition move = new TranslateTransition(Duration.seconds(1.5), ghostGroup);
    move.setToX(x2 - x1);
    move.setToY(y2 - y1);
    move.setInterpolator(Interpolator.EASE_BOTH);

    FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.4), ghostGroup);
    fadeOut.setFromValue(1.0);
    fadeOut.setToValue(0.0);

    PauseTransition reset = new PauseTransition(Duration.seconds(0.1));
    reset.setOnFinished(
        e -> {
          ghostGroup.setTranslateX(0);
          ghostGroup.setTranslateY(0);
        });

    FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.4), ghostGroup);
    fadeIn.setFromValue(0.0);
    fadeIn.setToValue(1.0);

    SequentialTransition sequence = new SequentialTransition(move, fadeOut, reset, fadeIn);
    sequence.setCycleCount(Animation.INDEFINITE);
    sequence.play();

    return sequence;
  }
}
