package auth;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Base64;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
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
import messenger.Listener;
import messenger.MessengerController;
import shared.DBConnector;

/**
 * FXML Controller class
 *
 * @author David RJ
 */
public class AuthController implements Initializable {
  
  private Connection conn = null;

  @FXML
  private Accordion accordion;
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    this.conn = DBConnector.connect();
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
  } 
  
  ///////////////  Sign Up Section  ///////////////
  @FXML
  private TitledPane signUpPane;
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
      this.signUp(name, email, hashedPassword);
    } else {
      Alert passwordsMismatchAlert = new Alert(AlertType.WARNING);
      passwordsMismatchAlert.setHeaderText("Passwords do not match!");
      passwordsMismatchAlert.show();
      this.signUpPasswordField.requestFocus();
    }
  }
     
  private void signUp(String name, String email, String password) {
    String signUpQuery =  "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
    try {
      PreparedStatement prepStatement = this.conn.prepareStatement(signUpQuery);
      prepStatement.setString(1, name);
      prepStatement.setString(2, email);
      prepStatement.setString(3, password);
      prepStatement.executeUpdate();
      this.clearSignUpForm();
      this.accordion.setExpandedPane(signInPane);
    } catch (SQLIntegrityConstraintViolationException ex) {
      Alert usernameDuplicateAlert = new Alert(AlertType.WARNING);
      usernameDuplicateAlert.setHeaderText(String.format("User with email '%s' already exist!", email));
      usernameDuplicateAlert.show();
      this.signUpEmailField.requestFocus();
    } catch (SQLException ex) {
      System.err.println(ex);
      ex.printStackTrace();
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
    this.signIn(email, hashedPassword);
  }
  
  private void signIn(String email, String password) {
    String signInQuery = "SELECT * FROM users WHERE email = ? AND password = ?";
    try {
      PreparedStatement prepStatement = this.conn.prepareStatement(signInQuery);
      prepStatement.setString(1, email);
      prepStatement.setString(2, password);
      ResultSet signInRS = prepStatement.executeQuery();
      if (!signInRS.isBeforeFirst()) {
        Alert incorrectLoginDataAlert = new Alert(AlertType.ERROR);
        incorrectLoginDataAlert.setHeaderText("Email or password is incorrect!");
        incorrectLoginDataAlert.show();
        this.signInPasswordField.requestFocus();
      } else {
        signInRS.next();
        int userId = signInRS.getInt("id");
        String userName = signInRS.getString("name");
        String userEmail = signInRS.getString("email");
        MessengerController.userData = new User(userId, userName, userEmail);
        this.redirectToMessengerWindow();
      }
    } catch (SQLException ex) {
      System.err.println("Sign In SQL error!");
      ex.printStackTrace();
    } catch (IOException ex) {
      System.err.println("Scene error");
      ex.printStackTrace();
    }
  }
  
  private void redirectToMessengerWindow() throws IOException {
    Stage authStage = (Stage) this.signInBtn.getScene().getWindow();
    authStage.hide();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/messenger/MessengerDoc.fxml"));
    Parent root = loader.load();
    
    MessengerController controller = loader.getController();
    new Thread(new Listener(MessengerController.userData, controller)).start();
    
    Stage stage = new Stage();
    stage.setScene(new Scene(root));
    stage.setMaximized(true);
    stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/chat-icon.png")));
    stage.show();
    stage.setOnHiding(e -> System.exit(0));
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
