package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.interfaces.Flashback;
import nz.ac.auckland.se206.interfaces.Interactable;

public class AiArtCriticFlashBackController implements Flashback, Interactable {
  private static String characterName = "AI Art Critic";

  public static String getName() {
    return characterName;
  }

  @FXML private Rectangle rectLantern1;
  @FXML private Rectangle rectLantern2;
  @FXML private Rectangle rectTower1;
  @FXML private Rectangle rectTower2;
  @FXML private Rectangle rectFountain1;
  @FXML private Rectangle rectFountain2;
  @FXML private Label timerLabel;
  @FXML private Button btnBack;
  @FXML private Button btnSend;
  @FXML private TextArea chatLog;
  @FXML private TextField userTextBox;

  private final Map<Rectangle, Rectangle> rectangleMap = new HashMap<>();
  private final Set<Rectangle> foundSimilarities = new HashSet<>();
  private final Set<Rectangle> originalFeatures = new HashSet<>();
  private final Set<Rectangle> aiFeatures = new HashSet<>();

  private Rectangle selected = null;
  private int numSelected = 0;

  @FXML
  private void handleSelect(MouseEvent event) {
    // Get selected rectangle
    Rectangle clickedRectangle = (Rectangle) event.getSource();

    // Already found or incorrect selecting in progress
    if (foundSimilarities.contains(clickedRectangle) || numSelected == 2) {
      return;
    }

    // Deselect rectangle
    if (clickedRectangle == selected) {
      selected.setOpacity(0);
      selected = null;
      numSelected--;
      return;
    }

    // Change selected rectangle
    if (aiFeatures.contains(clickedRectangle) && aiFeatures.contains(selected)
        || originalFeatures.contains(clickedRectangle) && originalFeatures.contains(selected)) {
      selected.setOpacity(0);
      selected = clickedRectangle;
      selected.setOpacity(0.3);
      return;
    }

    // No other objects are currently selected
    if (selected == null) {
      selected = clickedRectangle;
      selected.setOpacity(0.3);
      numSelected++;
      return;
    }

    if (clickedRectangle == rectangleMap.get(selected)) {
      // Match
      selected.setOpacity(0.5);
      clickedRectangle.setOpacity(0.5);
      foundSimilarities.add(selected);
      foundSimilarities.add(clickedRectangle);
      selected = null;
      numSelected = 0;
      return;
    }

    // Not a match
    Color colorSelected = (Color) selected.getFill();
    Color colorClicked = (Color) clickedRectangle.getFill();
    clickedRectangle.setOpacity(0.3);
    selected.setFill(Color.RED);
    clickedRectangle.setFill(Color.RED);

    Thread incorrectMatchThread =
        new Thread(
            () -> {
              numSelected = 2;
              try {
                Thread.sleep(1000);
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
              }
              Platform.runLater(
                  () -> {
                    selected.setFill(colorSelected);
                    clickedRectangle.setFill(colorClicked);
                    clickedRectangle.setOpacity(0);
                    numSelected = 1; // Reset selection state
                  });
            });
    incorrectMatchThread.start();
  }

  @FXML
  public void initialize() {
    // Set up the similarities interactable
    rectangleMap.put(rectLantern1, rectLantern2);
    rectangleMap.put(rectLantern2, rectLantern1);

    rectangleMap.put(rectTower1, rectTower2);
    rectangleMap.put(rectTower2, rectTower1);

    rectangleMap.put(rectFountain1, rectFountain2);
    rectangleMap.put(rectFountain2, rectFountain1);

    originalFeatures.add(rectLantern2);
    originalFeatures.add(rectTower2);
    originalFeatures.add(rectFountain2);

    aiFeatures.add(rectLantern1);
    aiFeatures.add(rectTower1);
    aiFeatures.add(rectFountain1);
  }

  @FXML
  private void onBtnBackClicked() {
    // Switch to courtroom
    try {
      App.getGame().setRoot("courtroom");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void onBtnSendClicked() {}

  @Override
  public void prepareScene() {
    // Set up the timer
    App.getGame().getTimer().setLabel(timerLabel);
    timerLabel.setText(App.getGame().getTimer().getTime());
  }
}
