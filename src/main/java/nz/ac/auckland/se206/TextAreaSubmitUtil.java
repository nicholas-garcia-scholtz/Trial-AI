package nz.ac.auckland.se206;

import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class TextAreaSubmitUtil {

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
}
