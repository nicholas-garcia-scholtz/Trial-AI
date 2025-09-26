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
    slides =
        new Slides.Builder(
                "images/AIAuditorMetaFlashback.png", myImageView1, myImageView2, myImageView3, btnNextSlide)
            .addSlide("images/AIAuditorCompareFlashback.png")
            .addSlide("images/AIAuditorScanningFlashback.png")
            .addSlide("images/AIAuditorPercentageFlashback.png")
            .build();
  }

  @FXML
  private void onNextSlideClicked(ActionEvent event) {
    boolean continueSlides = slides.nextSlide();
    if (!continueSlides) {
      try {
        App.getGame().setRoot("AIAuditorMemory");
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
