package nz.ac.auckland.se206.controllerhelpers;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Slides {
  public static class Builder {
    private List<Image> slides = new ArrayList<>();
    private ImageView myImageView1;
    private ImageView myImageView2;
    private ImageView myImageView3;
    private Button btnNextSlide;

    public Builder(
        String path,
        ImageView myImageView1,
        ImageView myImageView2,
        ImageView myImageView3,
        Button btnNextSlide) {
      this.slides.add(new Image(path));
      this.myImageView1 = myImageView1;
      this.myImageView2 = myImageView2;
      this.myImageView3 = myImageView3;
      this.btnNextSlide = btnNextSlide;
    }

    public Builder addSlide(String path) {
      this.slides.add(new Image(path));
      return this;
    }

    public Slides build() {
      return new Slides(this, myImageView1, myImageView2, myImageView3, btnNextSlide);
    }
  }

  private ImageView myImageView1;
  private ImageView myImageView2;
  private ImageView myImageView3;
  private Button btnNextSlide;
  private int index = 0;

  private double layoutX1 = -1600.0;
  private double layoutX2 = 60.0;
  private double layoutX3 = 1600.0;

  private double layoutY1 = 72.0;

  private List<Image> slides;

  private Slides(
      Builder builder,
      ImageView myImageView1,
      ImageView myImageView2,
      ImageView myImageView3,
      Button btnNextSlide) {
    this.slides = builder.slides;
    this.myImageView1 = myImageView1;
    this.myImageView2 = myImageView2;
    this.myImageView3 = myImageView3;
    this.btnNextSlide = btnNextSlide;

    myImageView1.setOpacity(0);
    myImageView2.setOpacity(1);
    myImageView3.setOpacity(0);
    myImageView2.setImage(slides.get(0));

    myImageView1.setLayoutX(layoutX1);
    myImageView1.setLayoutY(layoutY1);

    myImageView2.setLayoutX(layoutX2);
    myImageView2.setLayoutY(layoutY1);

    myImageView3.setLayoutX(layoutX3);
    myImageView3.setLayoutY(layoutY1);
  }

  private Timeline animateSlide(ImageView imageView, double currentX) {

    double firstOpacity;
    double secondOpacity;
    double nextLayoutX;

    // Fade out
    if (currentX == layoutX2) {
      nextLayoutX = layoutX1;
      firstOpacity = 1.0;
      secondOpacity = 0.0;
    } else if (currentX == layoutX3) {
      nextLayoutX = layoutX2;
      firstOpacity = 0.0;
      secondOpacity = 1.0;
    } else {
      nextLayoutX = layoutX3;
      firstOpacity = 0.0;
      secondOpacity = 0.0;
    }

    // Animate layoutX and opacity over 1.5 seconds
    return new Timeline(
        new KeyFrame(
            Duration.seconds(0),
            new KeyValue(imageView.layoutXProperty(), currentX),
            new KeyValue(imageView.opacityProperty(), firstOpacity)),
        new KeyFrame(
            Duration.seconds(2),
            new KeyValue(imageView.layoutXProperty(), nextLayoutX, Interpolator.EASE_OUT),
            new KeyValue(imageView.opacityProperty(), secondOpacity, Interpolator.EASE_OUT)));
  }

  public boolean nextSlide() {
    if (slides.size() - 1 == index) {
      return false;
    }

    // Update the image in the ImageView that is moving off screen
    index = (index + 1);
    if (index % 3 == 0) {
      myImageView2.setImage(slides.get(index));
    } else if (index % 3 == 1) {
      myImageView3.setImage(slides.get(index));
    } else {
      myImageView1.setImage(slides.get(index));
    }

    // Animate all three ImageViews
    Timeline t1 = animateSlide(myImageView1, myImageView1.getLayoutX());
    Timeline t2 = animateSlide(myImageView2, myImageView2.getLayoutX());
    Timeline t3 = animateSlide(myImageView3, myImageView3.getLayoutX());

    // Combine all animations
    ParallelTransition parallel = new ParallelTransition(t1, t2, t3);
    btnNextSlide.setDisable(true);

    // Play the animations
    parallel.play();

    // Enable the button after the animation is done
    parallel.setOnFinished(event -> btnNextSlide.setDisable(false));

    return true;
  }
}
