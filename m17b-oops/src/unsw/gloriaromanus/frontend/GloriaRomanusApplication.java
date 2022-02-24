package unsw.gloriaromanus.frontend;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * This changes between the scenes and starts the first scene.
 */
public class GloriaRomanusApplication extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    stage.setTitle("Gloria Romanus");
    new SceneTransitioner(stage);
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {
    // gameController.terminate();
  }

  /**
   * Opens and runs application.
   *
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {

    Application.launch(args);
  }





}