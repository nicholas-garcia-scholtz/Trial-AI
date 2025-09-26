package nz.ac.auckland.se206;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.text.Text;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest.Model;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;

/**
 * Handles sending the user’s verdict + rationale to the LLM and displaying the outcome in a Text
 * node.
 */
public class OutcomeLogs {

  private final Text txtOutcome;
  private final ChatCompletionRequest chatCompletionRequest;

  public OutcomeLogs(Text txtOutcome) {
    this.txtOutcome = txtOutcome;

    ChatCompletionRequest tempRequest = null;
    try {
      ApiProxyConfig config = ApiProxyConfig.readConfig();
      tempRequest =
          new ChatCompletionRequest(config)
              .setN(1)
              .setTemperature(0.2)
              .setTopP(0.4)
              .setModel(Model.GPT_4_1_MINI)
              .setMaxTokens(100);
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
    chatCompletionRequest = tempRequest;
  }

  /**
   * Sends the user’s verdict + rationale to the LLM and updates the Text node with the response.
   *
   * @param verdict true if Not Guilty, false if Guilty
   * @param rationale user input rationale
   */
  public void evaluateRationale(boolean verdict, String rationale) {
    if (chatCompletionRequest == null) {
      Platform.runLater(() -> txtOutcome.setText("Error: LLM request not initialized."));
      return;
    }

    Task<Void> task =
        new Task<>() {
          @Override
          protected Void call() {
            try {
              String basePrompt = loadPrompt("/prompts/outcomeprompt.txt");

              String fullPrompt =
                  basePrompt
                      + "\n\nVerdict: "
                      + (verdict ? "Not Guilty" : "Guilty")
                      + "\nRationale: "
                      + rationale;

              ChatMessage userMessage = new ChatMessage("user", fullPrompt);

              chatCompletionRequest.addMessage(userMessage);

              ChatCompletionResult result = chatCompletionRequest.execute();
              Choice choice = result.getChoices().iterator().next();
              ChatMessage llmResponse = choice.getChatMessage();

              Platform.runLater(() -> txtOutcome.setText(llmResponse.getContent()));

            } catch (ApiProxyException e) {
              e.printStackTrace();
              Platform.runLater(() -> txtOutcome.setText("Error: Could not evaluate rationale."));
            }
            return null;
          }
        };

    new Thread(task).start();
  }

  /**
   * Utility to load a text file from resources.
   *
   * @param resourcePath the resource path of the prompt
   * @return the contents of the file, or empty string if not found
   */
  private String loadPrompt(String resourcePath) {
    try (InputStream input = getClass().getResourceAsStream(resourcePath)) {
      if (input == null) {
        throw new IOException("Prompt file not found: " + resourcePath);
      }
      return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
      return "";
    }
  }
}
