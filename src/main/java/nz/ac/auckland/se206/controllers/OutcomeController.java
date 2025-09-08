package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;

public class OutcomeController {
  @FXML private Text verdictText;
  @FXML private Text verdictMark;
  @FXML private Pane outcome;
  @FXML private Button btnRestartGame;

  @FXML
  private void initialize() {
    if (App.getVerdict()) {
      outcome.setStyle("-fx-background-color: #00FF00;");
      verdictText.setText("Correct Verdict");
      verdictMark.setText("✓");
    }
  }

  @FXML
  private void onBtnRestartGame(ActionEvent event) {
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    try {
      App.newGame(stage);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
