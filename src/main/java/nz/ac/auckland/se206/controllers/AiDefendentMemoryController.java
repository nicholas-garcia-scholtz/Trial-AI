package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.ChatService;
import nz.ac.auckland.se206.GhostDragUtil;
import nz.ac.auckland.se206.TextAreaSubmitUtil;
import nz.ac.auckland.se206.interfaces.Interactable;

public class AiDefendentMemoryController implements Interactable {
  private static String characterName = "AI Defendent";

  public static String getName() {
    return characterName;
  }

  @FXML private ImageView trainingData1;
  @FXML private ImageView trainingData2;
  @FXML private ImageView trainingData3;
  @FXML private ImageView trainingData4;
  @FXML private ImageView publicDomain1;
  @FXML private ImageView publicDomain2;
  @FXML private Rectangle rectDropZone;
  @FXML private Label timerLabel;
  @FXML private Label titleLabel;
  @FXML private Label notTrainingDataLabel;
  @FXML private TextArea userTextBox;
  @FXML private TextArea chatLog;
  @FXML private ImageView thinkingHeadshot;
  @FXML private ImageView neutralHeadshot;
  @FXML private ImageView loadingSpinner;
  @FXML private Button btnSend;
  @FXML private ProgressBar progressBar;
  @FXML private Group ghostGroup;
  @FXML private Rectangle ghostTargetPoint;

  private int droppedCount = 0;
  private int totalTrainingImages = 4;
  private Map<ImageView, Point2D> originalPositions = new HashMap<>();
  private SequentialTransition ghostTransition;
  private boolean loadingDebounce = false;

  @FXML
  private void onBtnSendClicked() {
    if (loadingDebounce) {
      return;
    }
    // Send the message to the chat service and display the response
    startLoading();
    ChatService.get().addPlayerMessage(userTextBox.getText());
    appendToChat("[You] " + userTextBox.getText());

    userTextBox.setText("");

    ChatService.get()
        .generateCharacterResponse(
            ChatService.ChatCharacter.AIDEFENDANT,
            (String result) -> {
              appendToChat("[EaselMind] " + result);
              stopLoading();
            });
  }

  private void stopLoading() {
    loadingDebounce = false;
    thinkingHeadshot.setVisible(false);
    loadingSpinner.setVisible(false);
    neutralHeadshot.setVisible(true);
    btnSend.setDisable(false);
    userTextBox.setDisable(false);
  }

  @FXML
  private void onBtnBackClicked() {
    try {
      App.getGame().setRoot("courtroom");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void appendToChat(String message) {
    chatLog.setText(chatLog.getText() + "\n\n" + message);
    Platform.runLater(() -> chatLog.positionCaret(chatLog.getLength()));
  }

  private void startLoading() {
    loadingDebounce = true;
    thinkingHeadshot.setVisible(true);
    loadingSpinner.setVisible(true);
    userTextBox.setDisable(true);
    btnSend.setDisable(true);

    neutralHeadshot.setVisible(false);
  }

  @FXML
  public void initialize() {
    storeOriginalPositions();

    TextAreaSubmitUtil.bindEnterSubmit(userTextBox, () -> onBtnSendClicked());

    progressBar.setProgress(0.0);

    // Make images draggable
    makeDraggable(trainingData1);
    makeDraggable(trainingData2);
    makeDraggable(trainingData3);
    makeDraggable(trainingData4);
    makeDraggable(publicDomain1);
    makeDraggable(publicDomain2);

    ghostTransition =
        GhostDragUtil.createGhostDragAnimation(ghostGroup, publicDomain1, ghostTargetPoint);
    TextAreaSubmitUtil.clearTextAreaEvents(chatLog);
  }

  private void stopDragGhost() {
    ghostGroup.setVisible(false);
    if (ghostTransition != null) {
      ghostTransition.stop();
    }
  }

  private void storeOriginalPositions() {
    // Populate the map with original positions
    originalPositions.put(
        trainingData1, new Point2D(trainingData1.getLayoutX(), trainingData1.getLayoutY()));
    originalPositions.put(
        trainingData2, new Point2D(trainingData2.getLayoutX(), trainingData2.getLayoutY()));
    originalPositions.put(
        trainingData3, new Point2D(trainingData3.getLayoutX(), trainingData3.getLayoutY()));
    originalPositions.put(
        trainingData4, new Point2D(trainingData4.getLayoutX(), trainingData4.getLayoutY()));
    originalPositions.put(
        publicDomain1, new Point2D(publicDomain1.getLayoutX(), publicDomain1.getLayoutY()));
    originalPositions.put(
        publicDomain2, new Point2D(publicDomain2.getLayoutX(), publicDomain2.getLayoutY()));
  }

  private void makeDraggable(ImageView imageView) {
    final double[] dragAnchorX = new double[1];
    final double[] dragAnchorY = new double[1];

    // Mouse pressed - start drag
    imageView.setOnMousePressed(
        event -> {
          stopDragGhost();
          dragAnchorX[0] = event.getX();
          dragAnchorY[0] = event.getY();
          imageView.toFront(); // Bring to front while dragging
          imageView.setOpacity(0.7); // Visual feedback

          // Highlight drop zone
          rectDropZone.setOpacity(0.3);
        });

    // Mouse dragged - move image
    imageView.setOnMouseDragged(
        event -> {
          imageView.setLayoutX(event.getSceneX() - dragAnchorX[0]);
          imageView.setLayoutY(event.getSceneY() - dragAnchorY[0]);
        });

    // Mouse released - check if dropped in valid zone
    imageView.setOnMouseReleased(
        event -> {
          imageView.setOpacity(1.0); // Restore opacity
          rectDropZone.setOpacity(0.0); // Hide drop zone highlight

          if (isInDropZone(imageView)) {
            // Successfully dropped in training zone
            handleSuccessfulDrop(imageView);
          } else {
            // Reset to original position
            resetImagePosition(imageView);
          }
        });
  }

  private boolean isInDropZone(ImageView imageView) {
    // Get bounds of both the image and drop zone
    Bounds imageBounds = imageView.getBoundsInParent();
    Bounds dropBounds = rectDropZone.getBoundsInParent();

    // Check if they intersect (overlap)
    return imageBounds.intersects(dropBounds);
  }

  private void handleSuccessfulDrop(ImageView imageView) {
    // Increment count if right training data
    if (publicDomain1.equals(imageView) || publicDomain2.equals(imageView)) {
      showNotTrainingDataMessage();
    } else {
      droppedCount++;
      updateProgressBar();
      notTrainingDataLabel.setVisible(false); // Hide error message if visible
    }
    if (trainingData1.equals(imageView)) {
      ChatService.get()
          .addSystemMessage(
              "Inside EaselMind's memory interactable, the user identifies The Moon Light by"
                  + " Aurelio Vantini as a relevant piece of training data. (public domain work.)");
      String message =
          "The dark night sky in this training datum was a considerable factor in my generation.";
      ChatService.get().addCharacterMessage(ChatService.ChatCharacter.AIDEFENDANT, message);
      appendToChat("[EaselMind] " + message);

    } else if (trainingData2.equals(imageView)) {
      ChatService.get()
          .addSystemMessage(
              "Inside EaselMind's memory interactable, the user identifies 'The Island' created by"
                  + " Mariana D'Orazio as a relevant piece of training data. This public domain"
                  + " work has a floating island with a door and a balloon that looks extremely"
                  + " similar to Jean-Luc's painting. It is made clear from this interactable that"
                  + " Jean-Luc also copied off the same source material.");
      String message = "D'Orazio's work was a major influence on my generated painting.";
      ChatService.get().addCharacterMessage(ChatService.ChatCharacter.AIDEFENDANT, message);
      appendToChat("[EaselMind] " + message);

    } else if (trainingData3.equals(imageView)) {
      ChatService.get()
          .addSystemMessage(
              "Inside EaselMind's memory interactable, the user identifies the Japanese origami"
                  + " crane as a relevant piece of training data.");
      String message =
          "The Orizuru, or Japanese origami crane was a key training datum considered when"
              + " generating my piece.";
      ChatService.get().addCharacterMessage(ChatService.ChatCharacter.AIDEFENDANT, message);
      appendToChat("[EaselMind] " + message);

    } else if (trainingData4.equals(imageView)) {
      ChatService.get()
          .addSystemMessage(
              "Inside EaselMind's memory interactable, the user identifies a public domain painting"
                  + " (Love Bear by Silvio Andretto) with a bear holding a red love heart balloon"
                  + " as a key piece of training data.");
      String message =
          "The heart-shaped balloon in Andretto's work certainly informed my generative model.";
      ChatService.get().addCharacterMessage(ChatService.ChatCharacter.AIDEFENDANT, message);
      appendToChat("[EaselMind] " + message);
    }

    // Make image disappear with fade animation
    FadeTransition fadeOut = new FadeTransition(Duration.millis(300), imageView);
    fadeOut.setFromValue(1.0);
    fadeOut.setToValue(0.0);
    fadeOut.setOnFinished(
        e -> {
          imageView.setVisible(false);
          imageView.setDisable(true); // Prevent further interaction
        });
    fadeOut.play();
  }

  private void resetImagePosition(ImageView imageView) {
    Point2D originalPos = originalPositions.get(imageView);

    // Animate back to original position
    Timeline resetAnimation =
        new Timeline(
            new KeyFrame(
                Duration.millis(300),
                new KeyValue(
                    imageView.layoutXProperty(), originalPos.getX(), Interpolator.EASE_OUT),
                new KeyValue(
                    imageView.layoutYProperty(), originalPos.getY(), Interpolator.EASE_OUT)));
    resetAnimation.play();
  }

  private void showNotTrainingDataMessage() {
    // Show the error label
    notTrainingDataLabel.setVisible(true);
    notTrainingDataLabel.setOpacity(1.0);

    // Create fade-in animation
    FadeTransition fadeIn = new FadeTransition(Duration.millis(200), notTrainingDataLabel);
    fadeIn.setFromValue(0.0);
    fadeIn.setToValue(1.0);

    // Create fade-out animation after 1.5 seconds
    FadeTransition fadeOut = new FadeTransition(Duration.millis(500), notTrainingDataLabel);
    fadeOut.setFromValue(1.0);
    fadeOut.setToValue(0.0);
    fadeOut.setOnFinished(e -> notTrainingDataLabel.setVisible(false));

    // Create timeline to wait 1.5 seconds between fade-in and fade-out
    Timeline timeline =
        new Timeline(
            new KeyFrame(Duration.ZERO, e -> fadeIn.play()),
            new KeyFrame(Duration.seconds(1.5), e -> fadeOut.play()));

    timeline.play();
  }

  private void updateProgressBar() {
    // Calculate progress (0.0 to 1.0)
    double progress = (double) droppedCount / totalTrainingImages;

    // Animate the progress bar update
    Timeline progressAnimation =
        new Timeline(
            new KeyFrame(
                Duration.millis(500), new KeyValue(progressBar.progressProperty(), progress)));
    progressAnimation.play();
  }

  @Override
  public void prepareScene() {
    // !!! Set up the timer
    App.getGame().getTimer().setLabel(timerLabel);
    timerLabel.setText(App.getGame().getTimer().getTime());
  }
}
