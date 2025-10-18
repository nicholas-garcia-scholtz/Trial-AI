package nz.ac.auckland.se206;

import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class TextAreaSubmitUtil {

  private static boolean isOnScrollbar(TextArea textArea, MouseEvent event) {
    for (Node node : textArea.lookupAll(".scroll-bar")) {
      if (node instanceof ScrollBar) {
        if (node.getBoundsInParent().contains(event.getX(), event.getY())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Binds a lambda to run when Enter is pressed in the given TextArea. After execution, the
   * TextArea loses focus.
   *
   * @param textArea The TextArea to attach the handler to
   * @param onSubmit Lambda to run when Enter is pressed
   */
  public static void bindEnterSubmit(TextArea textArea, Runnable onSubmit) {
    textArea.addEventFilter(
        KeyEvent.KEY_PRESSED,
        event -> {
          if (event.getCode() == KeyCode.ENTER) {
            event.consume(); // Prevent newline in TextArea
            onSubmit.run();
            textArea.getParent().requestFocus(); // Unfocus TextArea
          }
        });
  }

  public static void clearTextAreaEvents(TextArea textArea) {
    textArea.addEventFilter(
        MouseEvent.MOUSE_PRESSED,
        event -> {
          if (!isOnScrollbar(textArea, event)) {
            event.consume();
          }
        });
    textArea.addEventFilter(
        MouseEvent.MOUSE_DRAGGED,
        event -> {
          if (!isOnScrollbar(textArea, event)) {
            event.consume();
          }
        });
  }
}
