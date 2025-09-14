package nz.ac.auckland.se206.controllers;

import java.util.HashMap;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.TimerCountdown;

public class VerdictController {
  @FXML private Label timerLabel;
  @FXML private Button btnNotGuilty;
  @FXML private Button btnGuilty;
  private Map<Button, String> verdictMap;
  private TimerCountdown timer;

  private void selectedVerdict(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
    // Set if the user has declared the AI artist as guilt
    App.setVerdict(verdictMap.get(btnClicked).equals("Not Guilty"));
    timer.endTimer();
  }

  @FXML
  private void onBtnNotGuiltyClicked(ActionEvent event) {
    selectedVerdict(event);
  }

  @FXML
  private void onBtnGuiltyClicked(ActionEvent event) {
    selectedVerdict(event);
  }

  @FXML
  public void initialize() {
    verdictMap = new HashMap<>();
    verdictMap.put(btnGuilty, "Guilty");
    verdictMap.put(btnNotGuilty, "Not Guilty");

    // Set up the timer
    timer = new TimerCountdown(0, 10, "outcome");
    timer.setLabel(timerLabel);
    timerLabel.setText(timer.getTime());
    Thread timerThread =
        new Thread(
            () -> {
              timer.count();
            });
    timerThread.start();
  }
}
