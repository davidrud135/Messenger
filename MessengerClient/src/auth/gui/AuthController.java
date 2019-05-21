package auth.gui;

import auth.AuthRespond;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import messages.User;
import messenger.MessengerController;
import shared.Communicator;

/**
 * FXML Controller class
 *
 * @author David Rudenko
 */
public class AuthController implements Initializable {
  
  @FXML
  private Accordion accordion;
  
  private static Communicator communicator;
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    communicator = new Communicator();
    this.accordion.setExpandedPane(signInPane);
    this.signUpBtn.disableProperty().bind(
      Bindings.isEmpty(signUpNameField.textProperty())
        .or(Bindings.isEmpty(signUpEmailField.textProperty()))
        .or(Bindings.isEmpty(signUpPasswordField.textProperty()))
        .or(Bindings.isEmpty(signUpRePasswordField.textProperty()))
    );
    this.signInBtn.disableProperty().bind(
      Bindings.isEmpty(signInEmailField.textProperty())
        .or(Bindings.isEmpty(signInPasswordField.textProperty()))
    );
    
    signUpEmailField.focusedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(
        ObservableValue<? extends Boolean> arg0, 
        Boolean oldPropertyValue, Boolean newPropertyValue
      ) {
        System.out.println(newPropertyValue);
      }
    });
//    this.signUpEmailField.textProperty().addListener((observable, oldValue, newValue) -> {
//      System.out.println("sign up textfield changed from " + oldValue + " to " + newValue);
//      boolean isEmailValid = newValue.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
//      if (!isEmailValid) {
//        signUpEmailField.setStyle("-fx-border-color: red;");
//      } else {
//        signUpEmailField.setStyle(null);
//      }
////      if (!isEmailValid) {
////        signUpEmailField.getStyleClass().add("invalid-email");
////      } else {
////        signUpEmailField.getStyleClass().clear();
////        signUpEmailField.getStyleClass().addAll("text-field", "text-input");
////      }
//      System.out.println(signUpEmailField.getStyleClass());
//      System.out.println(
//        String.format("Email %s is %s", newValue, isEmailValid)
//      );
//    });
  } 
  
  ///////////////  Sign Up Section  ///////////////
  @FXML
  private TextField signUpNameField;
  @FXML
  private TextField signUpEmailField;
  @FXML
  private PasswordField signUpPasswordField;
  @FXML
  private PasswordField signUpRePasswordField;
  @FXML
  private Button signUpBtn;
  
  @FXML
  private void onSignUp(ActionEvent ev) {
    String name = signUpNameField.getText().trim();
    String email = signUpEmailField.getText().trim();
    String password = signUpPasswordField.getText();
    String rePassword = signUpRePasswordField.getText();
    if (password.equals(rePassword)) {
      String hashedPassword = hashStringWithSHA256(password);
      AuthRespond signUpRespond = Communicator.signUpUser(name, email, hashedPassword);
      System.out.println(
        String.format("Sign Up respond type: %s", signUpRespond.getType())
      );
      switch(signUpRespond.getType()) {
        case SIGN_UP_SUCCESS:
          this.clearSignUpForm();
          this.accordion.setExpandedPane(signInPane);
          break;
        case SIGN_UP_EMAIL_DUPLICATE:
          Alert userEmailDuplicateAlert = new Alert(AlertType.WARNING);
          userEmailDuplicateAlert.setHeaderText(String.format("User with email '%s' already exist!", email));
          userEmailDuplicateAlert.showAndWait();
          this.signUpEmailField.requestFocus();
          break;
        case SIGN_UP_FAILURE:
          Alert failureSignUpAlert = new Alert(AlertType.WARNING);
          failureSignUpAlert.setHeaderText("Can't sign up.");
          failureSignUpAlert.showAndWait();
          break;
      }
    } else {
      Alert passwordsMismatchAlert = new Alert(AlertType.WARNING);
      passwordsMismatchAlert.setHeaderText("Passwords do not match!");
      passwordsMismatchAlert.showAndWait();
      this.signUpPasswordField.requestFocus();
    }
  }
  
  private void clearSignUpForm() {
    this.signUpNameField.clear();
    this.signUpEmailField.clear();
    this.signUpPasswordField.clear();
    this.signUpRePasswordField.clear();
  }
  
  /////////////// Sign In Section ///////////////
  
  @FXML
  private TitledPane signInPane;
  @FXML
  private TextField signInEmailField;
  @FXML 
  private PasswordField signInPasswordField;
  @FXML
  public Button signInBtn;
  
  @FXML
  public void onSignIn(ActionEvent ev) {
    String email = signInEmailField.getText().trim();
    String password = signInPasswordField.getText();
    String hashedPassword = hashStringWithSHA256(password);
    AuthRespond signInRespond = Communicator.signInUser(email, hashedPassword);
    System.out.println(
      String.format("Sign In respond type: %s", signInRespond.getType())
    );
    switch(signInRespond.getType()) {
      case SIGN_IN_WRONG_DATA:
        Alert incorrectLoginDataAlert = new Alert(AlertType.ERROR);
        incorrectLoginDataAlert.setHeaderText("Email or password is incorrect!");
        incorrectLoginDataAlert.showAndWait();
        this.signInPasswordField.requestFocus();
        break;
      case SIGN_IN_SUCCESS:
        User user = signInRespond.getSignedInUserData();
        MessengerController.userData = user;
        this.redirectToMessengerWindow();
        break;
      case SIGN_IN_FAILURE:
        Alert failureSignUpAlert = new Alert(AlertType.WARNING);
        failureSignUpAlert.setHeaderText("Can't sign in.");
        failureSignUpAlert.showAndWait();
        break;
    }
  }
  
  private void redirectToMessengerWindow() {
    try {
      Stage authStage = (Stage) this.signInBtn.getScene().getWindow();
      authStage.hide();
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/messenger/MessengerDoc.fxml"));
      Parent root = loader.load();
      
      MessengerController controller = loader.getController();
      communicator.setUserData(MessengerController.userData);
      communicator.setMessengerController(controller);
      new Thread(communicator).start();
      
      Stage stage = new Stage();
      stage.setScene(new Scene(root));
      stage.setMaximized(true);
      stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/chat-icon.png")));
      stage.show();
      stage.setOnHiding(e -> System.exit(0));
    } catch (IOException ex) {
      System.err.println("Cant redirecto to Messenger window.");
      ex.printStackTrace();
    }
  }
  
  private String hashStringWithSHA256(String str) {
    String encodedStr = "";
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
      encodedStr = Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException ex) {
      ex.printStackTrace();
    }
    return encodedStr;
  }
    
}
