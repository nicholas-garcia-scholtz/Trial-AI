package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;

public class CourtroomController {
  private static boolean isInitialised;

  public static void newGame() {
    isInitialised = false;
  }

  private Media contextAudio = null;
  private MediaPlayer contextAudioPlayer = null;
  @FXML private Button btnMakeDecision;
  @FXML private Label timerLabel;
  @FXML private Rectangle rectArtist;
  @FXML private Rectangle rectDefendant;
  @FXML private Rectangle rectCritic;
  private final Map<Rectangle, String> rectangleSceneMap = new HashMap<>();

  // Handling navigation between scenes
  @FXML
  private void handleRectangleClick(MouseEvent event) {
    // Stop playing audio if user switches scenes
    if (contextAudioPlayer != null) {
      if (contextAudioPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
        contextAudioPlayer.stop();
      }
      contextAudioPlayer.dispose();
    }
    Rectangle clickedRectangle = (Rectangle) event.getSource();
    String sceneName = rectangleSceneMap.get(clickedRectangle);

    if (sceneName != null) {
      try {
        App.setRoot(sceneName);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      System.out.println("No scene mapped for this rectangle.");
    }
  }

  @FXML
  private void onMakeDecision(ActionEvent event) {
    // Stop playing audio if still running
    if (contextAudioPlayer != null) {
      if (contextAudioPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
        contextAudioPlayer.stop();
      }
      contextAudioPlayer.dispose();
    }
    App.getTimer().endTimer();
  }

  @FXML
  public void initialize() {
    App.getTimer().setLabel(timerLabel);

    // Play the game context audio when the game has just started
    if (!App.getPlayedGameContext()) {
      try {
        contextAudio =
            new Media(App.class.getResource("/sounds/GameContext.mp3").toURI().toString());
        contextAudioPlayer = new MediaPlayer(contextAudio);
        contextAudioPlayer.play();
      } catch (java.net.URISyntaxException e) {
        e.printStackTrace();
      }
      App.setPlayedGameContext();
    }

    // Update the timer label
    timerLabel.setText(App.getTimer().getTime());
    if (!isInitialised) {
      Thread timerThread = new Thread(() -> App.getTimer().count());
      isInitialised = true;
      timerThread.setDaemon(true);
      timerThread.start();
    }

    // Link the rectangles with the scenes
    rectangleSceneMap.put(rectCritic, "AIArtCriticFlashBack");
    rectangleSceneMap.put(rectArtist, "ArtistFlashback");
    rectangleSceneMap.put(rectDefendant, "AIArtistFlashback");
  }
}
