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
import javafx.beans.binding.BooleanBinding;
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
  
  private String validEmailRegEx = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
  private String invalidBorderStyle = "-fx-text-box-border: #B22222;";
  private String validBorderStyle = "-fx-text-box-border: #3CB371;";
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    communicator = new Communicator();
    this.accordion.setExpandedPane(signInPane);
    this.setSignUpFieldsValidation();
    this.setSignInFieldsValidation();
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
  
  private BooleanBinding signUpNameFieldValidator;
  private BooleanBinding signUpEmailFieldValidator;
  private BooleanBinding signUpPasswordFieldValidator;
  private BooleanBinding signUpRePasswordFieldValidator;
  
  private ChangeListener<Boolean> signUpNameFieldFocusListener = new ChangeListener<Boolean>() {
    @Override
    public void changed(ObservableValue<? extends Boolean> arg0, Boolean notFocused, Boolean focused) {
      if (notFocused) {
        if (signUpNameFieldValidator.get()) {
          signUpNameField.setStyle(invalidBorderStyle);
        } else {
          signUpNameField.setStyle(validBorderStyle);
        }
      }
    }
  };
  
  private ChangeListener<Boolean> signUpEmailFieldFocusListener = new ChangeListener<Boolean>() {
    @Override
    public void changed(ObservableValue<? extends Boolean> arg0, Boolean notFocused, Boolean focused) {
      if (notFocused) {
        if (signUpEmailFieldValidator.get()) {
          signUpEmailField.setStyle(invalidBorderStyle);
        } else {
          signUpEmailField.setStyle(validBorderStyle);
        }
      }
    }
  };
  
  private ChangeListener<Boolean> signUpPasswordFieldFocusListener = new ChangeListener<Boolean>() {
    @Override
    public void changed(ObservableValue<? extends Boolean> arg0, Boolean notFocused, Boolean focused) {
      if (notFocused) {
        if (signUpPasswordFieldValidator.get()) {
          signUpPasswordField.setStyle(invalidBorderStyle);
        } else {
          signUpPasswordField.setStyle(validBorderStyle);
        }
      }
    }
  };
  
  private ChangeListener<Boolean> signUpRePasswordFieldFocusListener = new ChangeListener<Boolean>() {
    @Override
    public void changed(ObservableValue<? extends Boolean> arg0, Boolean notFocused, Boolean focused) {
      if (notFocused) {
        if (signUpRePasswordFieldValidator.get()) {
          signUpRePasswordField.setStyle(invalidBorderStyle);
        } else {
          signUpRePasswordField.setStyle(validBorderStyle);
        }
      }
    }
  };
  
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
        case SIGN_UP_DUPLICATE:
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
  
  private void setSignUpFieldsValidation() {
    signUpNameFieldValidator = Bindings.isEmpty(signUpNameField.textProperty());
    signUpEmailFieldValidator = Bindings.createBooleanBinding(() ->
      !signUpEmailField.getText().matches(validEmailRegEx),
      signUpEmailField.textProperty()
    );
    signUpPasswordFieldValidator = Bindings.isEmpty(signUpPasswordField.textProperty());
    signUpRePasswordFieldValidator = Bindings.isEmpty(signUpRePasswordField.textProperty());
    
    signUpNameField.focusedProperty().addListener(signUpNameFieldFocusListener);
    signUpEmailField.focusedProperty().addListener(signUpEmailFieldFocusListener);
    signUpPasswordField.focusedProperty().addListener(signUpPasswordFieldFocusListener);
    signUpRePasswordField.focusedProperty().addListener(signUpRePasswordFieldFocusListener);
    this.signUpBtn.disableProperty().bind(
      signUpNameFieldValidator
      .or(signUpEmailFieldValidator)
      .or(signUpPasswordFieldValidator)
      .or(signUpRePasswordFieldValidator)
    );
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
  
  private BooleanBinding signInEmailFieldValidator;
  private BooleanBinding signInPasswordFieldValidator;
  
  private ChangeListener<Boolean> signInEmailFieldFocusListener = new ChangeListener<Boolean>() {
    @Override
    public void changed(ObservableValue<? extends Boolean> arg0, Boolean notFocused, Boolean focused) {
      if (notFocused) {
        if (signInEmailFieldValidator.get()) {
          signInEmailField.setStyle(invalidBorderStyle);
        } else {
          signInEmailField.setStyle(validBorderStyle);
        }
      }
    }
  };
  
  private ChangeListener<Boolean> signInPasswordFieldFocusListener = new ChangeListener<Boolean>() {
    @Override
    public void changed(ObservableValue<? extends Boolean> arg0, Boolean notFocused, Boolean focused) {
      if (notFocused) {
        if (signInPasswordFieldValidator.get()) {
          signInPasswordField.setStyle(invalidBorderStyle);
        } else {
          signInPasswordField.setStyle(validBorderStyle);
        }
      }
    }
  };
  
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
      case SIGN_IN_DUPLICATE:
        Alert signInUserDuplicateAlert = new Alert(AlertType.WARNING);
        signInUserDuplicateAlert.setHeaderText(
          String.format("User with email '%s' already signed in!", signInRespond.getSignedInUser().toString())
        );
        signInUserDuplicateAlert.showAndWait();
        this.signInEmailField.requestFocus();
        break;
      case SIGN_IN_SUCCESS:
        User user = signInRespond.getSignedInUser();
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
  
  private void setSignInFieldsValidation() {
    signInEmailFieldValidator = Bindings.createBooleanBinding(() ->
      !signInEmailField.getText().matches(validEmailRegEx),
      signInEmailField.textProperty()
    );
    signInPasswordFieldValidator = Bindings.isEmpty(signInPasswordField.textProperty());
    
    signInEmailField.focusedProperty().addListener(signInEmailFieldFocusListener);
    signInPasswordField.focusedProperty().addListener(signInPasswordFieldFocusListener);
    this.signInBtn.disableProperty().bind(
      signInEmailFieldValidator
      .or(signInPasswordFieldValidator)
    );
  }
  
  /////////////// General methods ///////////////
  
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
