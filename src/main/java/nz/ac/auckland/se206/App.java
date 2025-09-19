package nz.ac.auckland.se206;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nz.ac.auckland.se206.controllers.AiArtCriticFlashBackController;
import nz.ac.auckland.se206.controllers.AiArtistFlashbackController;
import nz.ac.auckland.se206.controllers.ArtistFlashbackController;
import nz.ac.auckland.se206.controllers.ChatController;
import nz.ac.auckland.se206.controllers.CourtroomController;

/**
 * This is the entry point of the JavaFX application. This class initializes and runs the JavaFX
 * application.
 */
public class App extends Application {

  private static Scene scene;
  private static TimerCountdown timer;
  private static ChatLogs chatLogs;
  private static String rationale;
  private static Map<String, Boolean> flashbackMap;
  private static boolean playedGameContext;
  private static boolean isGuilty;

  public static void newGame(Stage stage) throws IOException {
    // Reset all static fields
    flashbackMap = new HashMap<>();
    flashbackMap.put(AiArtCriticFlashBackController.getName(), false);
    flashbackMap.put(AiArtistFlashbackController.getName(), false);
    flashbackMap.put(ArtistFlashbackController.getName(), false);
    playedGameContext = false;
    isGuilty = false;
    chatLogs = new ChatLogs();
    timer = new TimerCountdown(2, 0, "verdict");

    // Change all static variables for the controller classes to indicate that a new game has
    // started
    CourtroomController.newGame();

    setRoot("welcome");
  }

  public static void setVerdict(boolean guilty) {
    isGuilty = guilty;
  }

  public static boolean getVerdict() {
    return isGuilty;
  }

  public static void setRationale(String userRationale) {
    rationale = userRationale;
  }

  public static String getRationale() {
    return rationale;
  }

  public static boolean getPlayedGameContext() {
    return playedGameContext;
  }

  public static void setPlayedGameContext() {
    playedGameContext = true;
  }

  public static TimerCountdown getTimer() {
    return timer;
  }

  public static ChatLogs getChatLogs() {
    return chatLogs;
  }

  public static void doneFlashback(String name) {
    flashbackMap.put(name, true);
  }

  public static boolean getDoneFlashback(String name) {
    return flashbackMap.get(name);
  }

  /**
   * The main method that launches the JavaFX application.
   *
   * @param args the command line arguments
   */
  public static void main(final String[] args) {
    launch();
  }

  /**
   * Sets the root of the scene to the specified FXML file.
   *
   * @param fxml the name of the FXML file (without extension)
   * @throws IOException if the FXML file is not found
   */
  public static void setRoot(String fxml) throws IOException {
    scene.setRoot(loadFxml(fxml));
  }

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

  /**
   * Opens the chat view and sets the profession in the chat controller.
   *
   * @param event the mouse event that triggered the method
   * @param profession the profession to set in the chat controller
   * @throws IOException if the FXML file is not found
   */
  public static void openChat(MouseEvent event, String profession) throws IOException {
    FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/chat.fxml"));
    Parent root = loader.load();

    ChatController chatController = loader.getController();
    chatController.setProfession(profession);

    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "welcome" scene.
   *
   * @param stage the primary stage of the application
   * @throws IOException if the "src/main/resources/fxml/welcome.fxml" file is not found
   */
  @Override
  public void start(final Stage stage) throws IOException {
    // Initialise all variables
    flashbackMap = new HashMap<>();
    flashbackMap.put(AiArtCriticFlashBackController.getName(), false);
    flashbackMap.put(AiArtistFlashbackController.getName(), false);
    flashbackMap.put(ArtistFlashbackController.getName(), false);
    playedGameContext = false;
    isGuilty = false;
    chatLogs = new ChatLogs();
    timer = new TimerCountdown(2, 0, "verdict");

    // Change all static variables for the controller classes to indicate that a new game has
    // started
    CourtroomController.newGame();

    // Load the welcome scene
    Parent root = loadFxml("welcome");
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
    root.requestFocus();
  }
}