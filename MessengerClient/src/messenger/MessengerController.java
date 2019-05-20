package messenger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import messages.Message;
import messages.User;
import shared.Communicator;

/**
 *
 * @author David RJ
 */
public class MessengerController implements Initializable {
  
  public static User userData;
  
  private ArrayList<User> usersList;
          
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
  
  EventHandler<ActionEvent> addUserEmailToField = new EventHandler<ActionEvent>() {
    public void handle(ActionEvent ev) {
      MenuItem item = (MenuItem) ev.getSource();
      String userEmail = item.getId();
      messageField.setText(
        String.format("%s: ", userEmail)
      );
      messageField.requestFocus();
      messageField.selectPositionCaret(userEmail.length() + 2);
    }
  };
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    this.userNameLbl.setText(userData.toString());
    this.userEmailLbl.setText(userData.getEmail());
    this.setImageChooserBtn();
    this.chatBox.heightProperty().addListener(observable -> this.chatScrollPane.setVvalue(1D));
  }
  
  @FXML
  private void onMessageSend(KeyEvent ev) {
    if (ev.getCode() == KeyCode.ENTER) {
      String msgText = this.messageField.getText().trim();
      if (msgText.isEmpty()) return;
      if (isMessagePrivate(msgText)) {
        String receiverEmail = msgText.substring(0, msgText.indexOf(":"));
        User receiverUser = null;
        for (User user : usersList) {
          if (user.getEmail().equals(receiverEmail)) {
            receiverUser = user;
            break;
          }
        }
        Communicator.sendPrivateTextMsg(msgText, receiverUser);
      } else {
        Communicator.sendTextMsg(msgText);      
      }
      this.messageField.clear();
    }
  }
  
  public void openImageChooser() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select Image File");
    fileChooser.getExtensionFilters().addAll(
      new FileChooser.ExtensionFilter("Image Files", "*.bmp", "*.png", "*.jpg")
    );
    File selectedImage = fileChooser.showOpenDialog(this.chooseImageBtn.getScene().getWindow());

    if (selectedImage != null) {
      Communicator.sendImageMsg(selectedImage);
    }
  }
  
  
  public void setUserList(Message msg) {
    Platform.runLater(() -> {
      this.usersListView.getItems().clear();
      usersList = msg.getUsers();
      for (User user : usersList) {
        if (user.getId() == userData.getId()) continue;
        Label userNameLbl = new Label(user.toString());
        userNameLbl.setPrefWidth(220);
        
        Image menuIcon = new Image(getClass().getResourceAsStream("/images/menu-vertical-icon.png"));
        ImageView menuIconView = new ImageView(menuIcon);
        menuIconView.setFitHeight(15);
        menuIconView.setFitWidth(15);
        
        MenuButton kebabMenuBtn = new MenuButton();
        MenuItem privateMsgItem = new MenuItem("Private message");
        privateMsgItem.setId(user.getEmail());
        privateMsgItem.setOnAction(addUserEmailToField);
        kebabMenuBtn.getItems().add(privateMsgItem);
        kebabMenuBtn.setGraphic(menuIconView);
        kebabMenuBtn.setPrefSize(15, 15);
        kebabMenuBtn.getStyleClass().add("kebab-menu");
        
        HBox userHBox = new HBox(userNameLbl, kebabMenuBtn);
        userHBox.setSpacing(10);
        userHBox.setAlignment(Pos.CENTER_LEFT);
        this.usersListView.getItems().add(userHBox);
      }
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
      messageBox.getStyleClass().add("public-msg-box");
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
  
  public void addPrivateMessageToChat(Message msg) {
    System.out.println("Add private msg to chat");
    Platform.runLater(() -> {
      String msgSenderName = msg.getSender().toString();
      String msgText = Coder.decrypt(msg.getText());
      String msgDateTime = msg.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
      Label msgSenderLbl = new Label("Private message from " + msgSenderName);
      Label msgTextLbl = new Label(msgText);
      Label msgTimeLbl = new Label(msgDateTime);
      msgSenderLbl.getStyleClass().add("msg-sender");
      msgTextLbl.getStyleClass().add("msg-text");
      msgTimeLbl.getStyleClass().add("msg-time");
      msgTextLbl.setWrapText(true);
      HBox hMsgBox = new HBox();
      hMsgBox.prefWidthProperty().bind(this.chatBox.widthProperty());
      VBox messageBox = new VBox(msgSenderLbl, msgTextLbl, msgTimeLbl);
      messageBox.getStyleClass().add("private-msg-box");
      messageBox.setMinWidth(100);
      messageBox.setMaxWidth(500);
      msgTimeLbl.prefWidthProperty().bind(messageBox.widthProperty());
      hMsgBox.getChildren().add(messageBox);
      if (msg.getSender().getId() == userData.getId()) {
        hMsgBox.setAlignment(Pos.CENTER_RIGHT);
        msgSenderLbl.setText("Private message to " + msg.getReceiver());
      }
      this.chatBox.getChildren().add(hMsgBox);
    });
  }
  
  public boolean isMessagePrivate(String text) {
    System.out.println(text);
    return text.matches("^[A-Za-z0-9+_.-]+@(.+): .+$");
  }
  
}