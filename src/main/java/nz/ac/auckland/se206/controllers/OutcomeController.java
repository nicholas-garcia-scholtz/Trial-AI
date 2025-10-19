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
  @FXML private Text txtAiRationaleText;
  @FXML private Button btnRestartGame;

  @FXML
  private void initialize() {
    boolean verdict = App.getGame().getVerdict();
    String rationale = App.getGame().getRationale();

    // Set verdict UI
    if (verdict) {
      verdictText.setText("Correct Verdict");
      verdictText.setStyle("-fx-fill: #31cf0aff; -fx-font-size: 48px;");
    } else {
      verdictText.setText("Incorrect Verdict");
      verdictText.setStyle("-fx-fill: #b71206ff; -fx-font-size: 48px;");
    }

    // Call LLM to evaluate rationale and set TextArea
    OutcomeLogs outcomeLogs = new OutcomeLogs(txtAiRationaleText);
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
