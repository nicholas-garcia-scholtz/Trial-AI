package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.App;

public class OutcomeController {
  @FXML private Text verdictText;
  @FXML private Text verdictMark;
  @FXML private Pane outcome;

  @FXML
  private void initialize() {
    if (App.getVerdict()) {
      outcome.setStyle("-fx-background-color: #00FF00;");
      verdictText.setText("Correct Verdict");
      verdictMark.setText("✓");
    }
  }
}
