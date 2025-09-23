package nz.ac.auckland.se206.characters;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.interfaces.Flashback;
import nz.ac.auckland.se206.interfaces.Interactable;

public class Person {
  protected boolean doneFlashback = false;

  protected Parent flashbackFxml;
  protected Parent interactableFxml;

  protected Flashback flashbackController;
  protected Interactable interactableController;

  public Person(String flashbackName, String interactableName) {
    FXMLLoader flashbackLoader =
        new FXMLLoader(App.class.getResource("/fxml/" + flashbackName + ".fxml"));
    FXMLLoader interactableLoader =
        new FXMLLoader(App.class.getResource("/fxml/" + interactableName + ".fxml"));

    try {
      this.flashbackFxml = flashbackLoader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      this.interactableFxml = interactableLoader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }

    flashbackController = flashbackLoader.getController();
    interactableController = interactableLoader.getController();
  }

  public Parent getFxml() {
    if (!doneFlashback) {
      doneFlashback = true;
      flashbackController.prepareScene();
      return flashbackFxml;
    }
    interactableController.prepareScene();
    return interactableFxml;
  }
}
