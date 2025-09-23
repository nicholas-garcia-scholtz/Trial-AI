package nz.ac.auckland.se206.controllerhelpers;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Slides {
  private ImageView myImageView1;
  private ImageView myImageView2;
  private ImageView myImageView3;
  private int index = 0;

  private List<Image> slides;

  private Slides(
      Builder builder, ImageView myImageView1, ImageView myImageView2, ImageView myImageView3) {
    this.slides = builder.slides;
    this.myImageView1 = myImageView1;
    this.myImageView2 = myImageView2;
    this.myImageView3 = myImageView3;

    myImageView1.setOpacity(0);
    myImageView2.setOpacity(1);
    myImageView3.setOpacity(0);
    myImageView2.setImage(slides.get(0));

    myImageView1.setLayoutX(-725.0);
    myImageView1.setLayoutY(52.0);

    myImageView2.setLayoutX(45.0);
    myImageView2.setLayoutY(52.0);

    myImageView3.setLayoutX(878.0);
    myImageView3.setLayoutY(52.0);
  }

  private Timeline animateSlide(ImageView imageView, double currentX) {

    double firstOpacity = 0.0;
    double secondOpacity = 0.0;
    double nextLayoutX = 0.0;

    // Fade out
    if (currentX == 45.0) {
      nextLayoutX = -725.0;
      firstOpacity = 1.0;
      secondOpacity = 0.0;
    } else if (currentX == 878.0) {
      nextLayoutX = 45.0;
      firstOpacity = 0.0;
      secondOpacity = 1.0;
    } else {
      nextLayoutX = 878.0;
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
            Duration.seconds(1.5),
            new KeyValue(imageView.layoutXProperty(), nextLayoutX, Interpolator.EASE_OUT),
            new KeyValue(imageView.opacityProperty(), secondOpacity, Interpolator.EASE_OUT)));
  }

  public boolean nextSlide() {
    if (slides.size() - 1 == index) {
      return false;
    }

    index = (index + 1);
    if (index % 3 == 0) {
      myImageView2.setImage(slides.get(index));
    } else if (index % 3 == 1) {
      myImageView3.setImage(slides.get(index));
    } else {
      myImageView1.setImage(slides.get(index));
    }

    Timeline t1 = animateSlide(myImageView1, myImageView1.getLayoutX());
    Timeline t2 = animateSlide(myImageView2, myImageView2.getLayoutX());
    Timeline t3 = animateSlide(myImageView3, myImageView3.getLayoutX());

    ParallelTransition parallel = new ParallelTransition(t1, t2, t3);
    parallel.play();

    return true;
  }

  public static class Builder {
    private List<Image> slides = new ArrayList<>();
    private ImageView myImageView1;
    private ImageView myImageView2;
    private ImageView myImageView3;

    public Builder(
        String path, ImageView myImageView1, ImageView myImageView2, ImageView myImageView3) {
      this.slides.add(new Image(path));
      this.myImageView1 = myImageView1;
      this.myImageView2 = myImageView2;
      this.myImageView3 = myImageView3;
    }

    public Builder addSlide(String path) {
      this.slides.add(new Image(path));
      return this;
    }

    public Slides build() {
      return new Slides(this, myImageView1, myImageView2, myImageView3);
    }
  }
}
