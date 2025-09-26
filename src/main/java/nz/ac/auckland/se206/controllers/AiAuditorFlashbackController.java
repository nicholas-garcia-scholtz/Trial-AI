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

public class AiAuditorFlashbackController implements Flashback {
  @FXML private Label timerLabel;
  @FXML private ImageView myImageView1;
  @FXML private ImageView myImageView2;
  @FXML private ImageView myImageView3;
  @FXML private Button btnNextSlide;
  private Slides slides;

  @FXML
  public void initialize() {
    // Initialise the flashback with all of the images in the slideshow
    slides =
        new Slides.Builder(
                "images/AIAuditorMetaFlashback.png",
                myImageView1,
                myImageView2,
                myImageView3,
                btnNextSlide)
            .addSlide("images/AIAuditorCompareFlashback.png")
            .addSlide("images/AIAuditorScanningFlashback.png")
            .addSlide("images/AIAuditorPercentageFlashback.png")
            .build(); // builder design pattern
  }

  @FXML
  private void onNextSlideClicked(ActionEvent event) {
    // When the next slide button is clicked, transition to the next slide
    boolean continueSlides = slides.nextSlide();
    if (!continueSlides) {
      try {
        App.getGame().setRoot(AiAuditorMemoryController.getName());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void prepareScene() {
    // Set up the timer
    App.getGame().getTimer().setLabel(timerLabel);
    timerLabel.setText(App.getGame().getTimer().getTime());
  }
}
