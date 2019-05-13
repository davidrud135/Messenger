package messenger;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import shared.DBConnector;
import messages.Message;
import messages.User;

/**
 *
 * @author David RJ
 */
public class MessengerController implements Initializable {
  
  private Connection conn = null;
  public static User userData;
  
  final private String HOST = "localhost";
  final private int PORT  = 12345;
  private Thread readThread;
  ObjectOutputStream outputStream;
  ObjectInputStream inputStream;
  Socket server;
          
  @FXML
  private Label userNameLbl;
  @FXML
  private Label userEmailLbl;
  @FXML 
  private ListView usersListView;
  @FXML
  private VBox chatBox;
  @FXML
  private TextField messageField;
  @FXML
  private ScrollPane chatScrollPane;
  @FXML
  private Button chooseImageBtn;        
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    this.conn = DBConnector.connect();
    this.userNameLbl.setText(userData.toString());
    this.userEmailLbl.setText(userData.getEmail());
    this.setImageChooserBtn();
    this.chatBox.heightProperty().addListener(observable -> this.chatScrollPane.setVvalue(1D));
  }
  
  public void openImageChooser() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select Image File");
    fileChooser.getExtensionFilters().addAll(
      new FileChooser.ExtensionFilter("Image Files", "*.bmp", "*.png", "*.jpg")
    );
    File selectedImage = fileChooser.showOpenDialog(this.chooseImageBtn.getScene().getWindow());

    if (selectedImage != null) {
     Listener.sendImageMsg(selectedImage);
    }
  }
  
  @FXML
  private void onMessageSend(KeyEvent ev) {
    if (ev.getCode() == KeyCode.ENTER) {
      String msgText = this.messageField.getText().trim();
      if (msgText.isEmpty()) return;
      String encryptedMsgText = Coder.encrypt(msgText);
      Listener.sendTextMsg(encryptedMsgText);
      this.messageField.clear();
    }
  }
  
  public void setUserList(Message msg) {
    Platform.runLater(() -> {
      ObservableList<User> usersObsList = FXCollections.observableList(msg.getUsers());
      usersListView.setItems(usersObsList);
    });
  }
  
  private void setImageChooserBtn() {
    Image imageIcon = new Image(getClass().getResource("/images/image-icon.png").toString());
    ImageView imageIconView = new ImageView(imageIcon);
    imageIconView.setFitHeight(35);
    imageIconView.setFitWidth(35);
    this.chooseImageBtn.setGraphic(imageIconView);
    this.chooseImageBtn.setOnAction(ev -> openImageChooser());
  }
  
  public void addNotificationToChat(Message msg) {
    Platform.runLater(() -> {
      String notificationText = msg.getText();
      Label notificationTextLbl = new Label(notificationText);
      notificationTextLbl.getStyleClass().add("notification-lbl");
      HBox notificationHBox = new HBox(notificationTextLbl);
      notificationHBox.prefWidthProperty().bind(chatBox.widthProperty());
      notificationHBox.setAlignment(Pos.CENTER);
      chatBox.getChildren().add(notificationHBox);
    });
  }
  
  public void addImageToChat(Message msg) {
    Platform.runLater(() -> {
      String msgSenderName = msg.getSender().toString();
      File msgImage = msg.getImage();
      Image image = null;
      try {
        image = new Image(msgImage.toURI().toURL().toString());
      } catch (MalformedURLException ex) {
        System.err.println("Cant parse image message");
        ex.printStackTrace();
      }
      ImageView imageView = new ImageView(image);
      
      String msgDateTime = msg.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
      Label msgSenderLbl = new Label(msgSenderName);
      Label msgTimeLbl = new Label(msgDateTime);
      msgSenderLbl.getStyleClass().add("msg-sender");
      msgTimeLbl.getStyleClass().add("msg-time");
      HBox hMsgBox = new HBox();
      hMsgBox.prefWidthProperty().bind(this.chatBox.widthProperty());
      VBox messageBox = new VBox(msgSenderLbl, imageView, msgTimeLbl);
      messageBox.getStyleClass().add("msg-box");
      messageBox.setMinWidth(100);
      messageBox.setMinHeight(100);
      messageBox.setMaxWidth(500);
      messageBox.setMaxHeight(500);
      msgTimeLbl.prefWidthProperty().bind(messageBox.widthProperty());
      imageView.fitWidthProperty().bind(messageBox.widthProperty().subtract(10));
      imageView.fitHeightProperty().bind(messageBox.heightProperty().subtract(40));
      hMsgBox.getChildren().add(messageBox);
      if (msg.getSender().getId() == userData.getId()) {
        hMsgBox.setAlignment(Pos.CENTER_RIGHT);
      }
      this.chatBox.getChildren().add(hMsgBox);
    });
  }
  
  public void addMessageToChat(Message msg) {
    Platform.runLater(() -> {
      String msgSenderName = msg.getSender().toString();
      String msgText = Coder.decrypt(msg.getText());
      String msgDateTime = msg.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
      Label msgSenderLbl = new Label(msgSenderName);
      Label msgTextLbl = new Label(msgText);
      Label msgTimeLbl = new Label(msgDateTime);
      msgSenderLbl.getStyleClass().add("msg-sender");
      msgTextLbl.getStyleClass().add("msg-text");
      msgTimeLbl.getStyleClass().add("msg-time");
      msgTextLbl.setWrapText(true);
      HBox hMsgBox = new HBox();
      hMsgBox.prefWidthProperty().bind(this.chatBox.widthProperty());
      VBox messageBox = new VBox(msgSenderLbl, msgTextLbl, msgTimeLbl);
      messageBox.getStyleClass().add("msg-box");
      messageBox.setMinWidth(100);
      messageBox.setMaxWidth(500);
      msgTimeLbl.prefWidthProperty().bind(messageBox.widthProperty());
      hMsgBox.getChildren().add(messageBox);
      if (msg.getSender().getId() == userData.getId()) {
        hMsgBox.setAlignment(Pos.CENTER_RIGHT);
      }
      this.chatBox.getChildren().add(hMsgBox);
    });
  }
  
}