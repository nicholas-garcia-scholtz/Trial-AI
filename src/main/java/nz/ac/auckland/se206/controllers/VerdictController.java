package nz.ac.auckland.se206.controllers;

import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.TimerCountdown;

public class VerdictController {
  @FXML private Label timerLabel;
  @FXML private Button btnNotGuilty;
  @FXML private Button btnGuilty;
  @FXML private Button btnSend;
  @FXML private TextArea txtRationale;

  private Map<Button, String> verdictMap;
  private String selectedVerdict; // "Guilty" or "Not Guilty"
  private TimerCountdown timer;

  private void chooseVerdict(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
    selectedVerdict = verdictMap.get(btnClicked); // "Guilty" or "Not Guilty"

    // Highlight the selected one with a darker shade
    if (selectedVerdict.equals("Guilty")) {
      btnGuilty.setStyle("-fx-background-color: #0f3f03ff;"); // darker green
      btnNotGuilty.setStyle("-fx-background-color: #176104ff;"); // original color
    } else if (selectedVerdict.equals("Not Guilty")) {
      btnNotGuilty.setStyle("-fx-background-color: #0f3f03ff;"); // darker green
      btnGuilty.setStyle("-fx-background-color: #176104ff;"); // original color
    }
  }

  @FXML
  private void onBtnNotGuiltyClicked(ActionEvent event) {
    chooseVerdict(event);
  }

  @FXML
  private void onBtnGuiltyClicked(ActionEvent event) {
    chooseVerdict(event);
  }

  @FXML
  private void onBtnSendClicked(ActionEvent event) {
    if (selectedVerdict != null && !txtRationale.getText().isBlank()) {
      // Save the verdict and rationale into App
      App.getGame().setVerdict(selectedVerdict.equals("Not Guilty"));
      App.getGame().setRationale(txtRationale.getText());

      // Stop timer and change scene
      timer.endTimer();
      try {
        App.getGame().setRoot("outcome");
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      // Feedback if missing selection or rationale
      System.out.println("Please select a verdict and provide a rationale.");
    }
  }

  @FXML
  public void initialize() {
    verdictMap = new HashMap<>();
    verdictMap.put(btnGuilty, "Guilty");
    verdictMap.put(btnNotGuilty, "Not Guilty");

    // Set up the timer
    timer = new TimerCountdown(1, 0, "outcome");
    timer.setLabel(timerLabel);
    timerLabel.setText(timer.getTime());
    Thread timerThread =
        new Thread(
            () -> {
              timer.count();
              Platform.runLater(
                  () -> onBtnSendClicked(new ActionEvent())); // Auto-submit when timer ends
            });
    timerThread.start();
  }
}
