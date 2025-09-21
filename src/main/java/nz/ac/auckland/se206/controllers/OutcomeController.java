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
import nz.ac.auckland.se206.OutcomeLogs;

public class OutcomeController {
  @FXML private Text verdictText;
  @FXML private Text verdictMark;
  @FXML private Pane outcome;
  @FXML private Text txtAIRationaleText;
  @FXML private Button btnRestartGame;

  @FXML
  private void initialize() {
    boolean verdict = App.getGame().getVerdict();
    String rationale = App.getGame().getRationale();

    // Set verdict UI
    if (verdict) {
      outcome.setStyle("-fx-background-color: #00FF00;"); // green
      verdictText.setText("Correct Verdict");
      verdictMark.setText("✓");
      txtAIRationaleText.setStyle("-fx-fill: white; -fx-font-size: 16px;");
    } else {
      outcome.setStyle("-fx-background-color: #FF0000;"); // red
      verdictText.setText("Incorrect Verdict");
      verdictMark.setText("X");
      txtAIRationaleText.setStyle("-fx-fill: white; -fx-font-size: 16px;");
    }

    // Call LLM to evaluate rationale and set TextArea
    OutcomeLogs outcomeLogs = new OutcomeLogs(txtAIRationaleText);
    outcomeLogs.evaluateRationale(verdict, rationale);
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
