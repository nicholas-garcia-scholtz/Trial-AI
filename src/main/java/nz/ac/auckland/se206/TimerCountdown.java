package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class TimerCountdown {
  private int numSeconds;
  private Label label;
  private String displayTime;
  private String nextScene;
  private boolean continueTimer = true;

  public TimerCountdown(int mins, int seconds, String nextScene) {
    seconds = seconds % 60;
    this.nextScene = nextScene;
    this.numSeconds = 60 * mins + seconds;
    if (mins < 10) {
      displayTime = "0" + mins;
    } else {
      displayTime = "" + mins;
    }
    if (seconds < 10) {
      displayTime = displayTime + ":0" + seconds;
    } else {
      displayTime = displayTime + ":" + seconds;
    }
  }

  public void endTimer() {
    continueTimer = false;
  }

  public void setLabel(Label label) {
    this.label = label;
  }

  public String getTime() {
    return this.displayTime;
  }

  public void count() {
    int seconds;
    int minutes;
    String out;
    for (int i = 0; i < numSeconds && continueTimer; i++) {

      seconds = (numSeconds - i) % 60;
      minutes = (numSeconds - i) / 60;

      // Set up a string for the formatting of the time
      if (minutes < 10) {
        out = "0" + minutes;
      } else {
        out = "" + minutes;
      }

      if (seconds < 10) {
        out = out + ":0" + seconds;
      } else {
        out = out + ":" + seconds;
      }

      displayTime = out;

      // Update the timer label
      Platform.runLater(
          () -> {
            label.setText(displayTime);
          });
      try {
        Thread.sleep(1000);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    // Change scene when the timer runs out
    Platform.runLater(
        () -> {
          label.setText("00:00");
          try {
            App.getGame().setRoot(nextScene);
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
  }
}
