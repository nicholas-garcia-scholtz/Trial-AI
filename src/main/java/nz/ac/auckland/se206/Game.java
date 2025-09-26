package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import nz.ac.auckland.se206.characters.AiAuditor;
import nz.ac.auckland.se206.characters.AiDefendent;
import nz.ac.auckland.se206.characters.HumanArtist;
import nz.ac.auckland.se206.characters.Person;
import nz.ac.auckland.se206.controllers.AiDefendentMemoryController;
import nz.ac.auckland.se206.controllers.AiProvenanceAuditorMemoryController;
import nz.ac.auckland.se206.controllers.CourtroomController;
import nz.ac.auckland.se206.controllers.HumanMemoryController;

public class Game {
  /**
   * Loads the FXML file and returns the associated node. The method expects that the file is
   * located in "src/main/resources/fxml".
   *
   * @param fxml the name of the FXML file (without extension)
   * @return the root node of the FXML file
   * @throws IOException if the FXML file is not found
   */
  private static Parent loadFxml(final String fxml) throws IOException {
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
  }

  private Map<String, Person> characters = new HashMap<>();

  private Scene scene;
  private TimerCountdown timer;
  private String rationale;
  private boolean playedGameContext;
  private boolean isGuilty;

  public Game(Stage stage) throws IOException {
    // Initialise all other game variables
    playedGameContext = false;
    isGuilty = false;
    timer = new TimerCountdown(5, 0, "verdict");

    // Change all static variables for the controller classes to indicate that a new game has
    // started
    CourtroomController.newGame();

    // Load the welcome scene
    Parent root = loadFxml("welcome");
    scene = new Scene(root, 1080, 720);
    stage.setScene(scene);
    stage.setResizable(false);
    stage.setTitle("Trial AI");
    stage.getIcons().clear();
    scene.setFill(Color.BLACK);
    stage.show();
    root.requestFocus();
  }

  public void setUpGame() {
    // Initialize characters
    characters.put(
        AiProvenanceAuditorMemoryController.getName(),
        new AiAuditor("AIArtCriticFlashBack", "AIArtProvenanceMemory"));
    characters.put(
        AiDefendentMemoryController.getName(),
        new AiDefendent("AiDefendentFlashback", "AiDefendentMemory"));
    characters.put(
        HumanMemoryController.getName(), new HumanArtist("ArtistFlashback", "HumanMemory"));
  }

  /**
   * Sets the root of the scene to the specified FXML file.
   *
   * @param fxml the name of the FXML file (without extension)
   * @throws IOException if the FXML file is not found
   */
  public void setRoot(String fxml) throws IOException {
    if (characters.containsKey(fxml)) {
      // Character - keep memory
      scene.setRoot(characters.get(fxml).getFxml());
    } else {
      scene.setRoot(loadFxml(fxml));
    }
  }

  public void setVerdict(boolean guilty) {
    isGuilty = guilty;
  }

  public boolean getVerdict() {
    return isGuilty;
  }

  public void setRationale(String userRationale) {
    rationale = userRationale;
  }

  public String getRationale() {
    return rationale;
  }

  public boolean getPlayedGameContext() {
    return playedGameContext;
  }

  public void setPlayedGameContext() {
    playedGameContext = true;
  }

  public TimerCountdown getTimer() {
    return timer;
  }
}
