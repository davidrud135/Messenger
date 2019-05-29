package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main program class launcher.
 * @author David Rudenko.
 */
public class MainLauncher extends Application {

  /**
   * Start method.
   * @param primaryStage primary stage.
   */
  @Override
  public void start(Stage primaryStage) {
    try {
      Parent root = FXMLLoader.load(getClass().getResource("/auth/gui/AuthDoc.fxml"));
      Scene scene = new Scene(root);
      
      primaryStage.setTitle("Authentication");
      primaryStage.setScene(scene);
      primaryStage.setResizable(false);
      primaryStage.getIcons().add(new Image(getClass().getResource("/images/auth-icon.png").toString()));
      primaryStage.show();
      primaryStage.setOnCloseRequest(e -> Platform.exit());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Launches program.
   * @param args 
   */
  public static void main(String[] args) {
    launch(args);
  }
}