package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllerhelpers.Slides;
import nz.ac.auckland.se206.interfaces.Flashback;

public class AiDefendentFlashbackController implements Flashback {
  @FXML private Button btnNextSlide;
  @FXML private ImageView myImageView1;
  @FXML private ImageView myImageView2;
  private Slides slides;
  @FXML private ImageView myImageView3;
  @FXML private Label timerLabel;

  // Constructor
  @FXML
  public void initialize() {
    // Initialize the slides with the images
    slides =
        new Slides.Builder(
                "images/AiDefendentFlashback1.png",
                myImageView1, // ImageView component
                myImageView2,
                myImageView3,
                btnNextSlide) // First slide passed as an argument
            .addSlide("images/AiDefendentFlashback2.png")
            .addSlide("images/AiDefendentFlashback3.png")
            .addSlide("images/AiDefendentFlashback4.png")
            .addSlide("images/AiDefendentFlashback5.png")
            .build(); // builder design pattern
  }

  @FXML
  private void onNextSlideClicked(ActionEvent event) {
    // transition to next slide when the button is clicked
    boolean continueSlides = slides.nextSlide();

    // If there are no more slides, go to the next scene
    if (continueSlides) {
      return;
    }
    try {
      App.getGame().setRoot(AiDefendentMemoryController.getName());
    } catch (IOException e) {

      e.printStackTrace();
      // Graceful error handling
    }
  }

  @Override
  public void prepareScene() {
    // Set up the timer
    App.getGame().getTimer().setLabel(this.timerLabel);
    timerLabel.setText(App.getGame().getTimer().getTime());
  }
}
