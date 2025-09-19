package nz.ac.auckland.se206.controllers;

import java.util.HashMap;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.TimerCountdown;

public class VerdictController {
  @FXML private Label timerLabel;
  @FXML private Button btnNotGuilty;
  @FXML private Button btnGuilty;
  @FXML private Button btnSend;
  @FXML private TextField txtRationale;


  private Map<Button, String> verdictMap;
  private String selectedVerdict; // "Guilty" or "Not Guilty"
  private TimerCountdown timer;


private void chooseVerdict(ActionEvent event) {
    Button btnClicked = (Button) event.getSource();
    selectedVerdict = verdictMap.get(btnClicked); // "Guilty" or "Not Guilty"


    // Reset both buttons to default color
    btnGuilty.setStyle("-fx-background-color: #3aff47;");
    btnNotGuilty.setStyle("-fx-background-color: #3aff47;");


    // Highlight the clicked button
    btnClicked.setStyle("-fx-background-color: #207c26ff;"); // change to any highlight color
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
      App.setVerdict(selectedVerdict.equals("Not Guilty"));
      App.setRationale(txtRationale.getText());


      // Stop timer and change scene
      timer.endTimer();
      try {
        App.setRoot("outcome");
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
