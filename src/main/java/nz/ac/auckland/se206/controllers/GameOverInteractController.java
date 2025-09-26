package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;

public class GameOverInteractController {

  @FXML private Button btnRestartGame;

  @FXML
  private void initialize() {}

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
