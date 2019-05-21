package shared;

import auth.AuthRespond;
import auth.AuthType;
import auth.UserAuthData;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import javafx.scene.control.Alert;
import messages.Message;
import messages.MessageType;
import messages.User;
import messenger.Coder;
import messenger.MessengerController;

/**
 *
 * @author David Rudenko
 */
public class Communicator implements Runnable {
  private Socket socket;
  final public String LOCAL_SERVER_HOST = "127.0.0.1";
  final public String REMOTE_SERVER_HOST = "95.46.98.47";
  final public int SERVER_PORT = 12345;
  private static ObjectOutputStream objOutStream;
  private static ObjectInputStream objInStream;
  private static User userData;
  private static MessengerController messengerController;
  
  public Communicator() {
    try {
//      socket = new Socket(REMOTE_SERVER_HOST, SERVER_PORT);
      socket = new Socket(LOCAL_SERVER_HOST, SERVER_PORT);
      objOutStream = new ObjectOutputStream(socket.getOutputStream());
      objInStream = new ObjectInputStream(socket.getInputStream());
    } catch (IOException ex) {
      Alert cantConnectToServerAlert = new Alert(Alert.AlertType.ERROR);
      cantConnectToServerAlert.setHeaderText("Cant connect to server.");
      cantConnectToServerAlert.setContentText("Please, try again.");
      cantConnectToServerAlert.showAndWait();
      ex.printStackTrace();
      System.exit(0);
    }
    System.out.println(
      String.format("Conneccted to server with IP %s on port %d", socket.getInetAddress(), socket.getPort())
    );
  }
  
  @Override
  public void run() {
    try {
      System.out.println("Sockets in and out ready!");
      while (socket.isConnected()) {
        Object inputObj = objInStream.readObject();
        if (inputObj instanceof Message) {
          handleMessageObj((Message) inputObj);
        }
      }
    } catch (IOException | ClassNotFoundException ex) {
      ex.printStackTrace();
    }
  }
  
  public static AuthRespond signUpUser(String name, String email, String password) {
    AuthRespond resp = null;
    try {
      UserAuthData userAuthData = new UserAuthData(name, email, password, AuthType.SIGN_UP);
      objOutStream.writeObject(userAuthData);
      objOutStream.flush();
      resp = (AuthRespond) objInStream.readObject();
    } catch (IOException | ClassNotFoundException ex) {
      ex.printStackTrace();
    }
    return resp;
  }
  
  public static AuthRespond signInUser(String email, String password) {
    AuthRespond resp = null;
    try {
      UserAuthData userAuthData = new UserAuthData(null, email, password, AuthType.SIGN_IN);
      objOutStream.writeObject(userAuthData);
      objOutStream.flush();
      resp = (AuthRespond) objInStream.readObject();
    } catch (IOException | ClassNotFoundException ex) {
      ex.printStackTrace();
    }
    return resp;
  }
  
  private void handleMessageObj(Message msg) {
    String senderName = (msg.getSender() == null) ? "Server": msg.getSender().toString();
    System.out.println(
      String.format(
        "Client recieved Message: %s, MessageType: %s, Sender: %s", 
        msg.getText(),
        msg.getType(),
        senderName
      )
    );
    switch (msg.getType()) {
      case USER_PUBLIC_TEXT:
        messengerController.addMessageToChat(msg);
        break;
      case USER_PUBLIC_IMAGE:
        messengerController.addImageToChat(msg);
        break;
      case USER_PRIVATE_TEXT:
        messengerController.addPrivateMessageToChat(msg);
        break;
      case CONNECTED:
        messengerController.setUserList(msg);
        messengerController.addNotificationToChat(msg);
        break;
      case DISCONNECTED:
        messengerController.setUserList(msg);
        break;
    }
  }
  
  public void setUserData(User userData) {
    Communicator.userData = userData;
  }
  
  public void setMessengerController(MessengerController messengerController) {
    Communicator.messengerController = messengerController;
  }
  
  public static void sendPrivateTextMsg(String msgText, User receiver) {
    String text = msgText.substring(msgText.indexOf(" ") + 1);
    try {
      Message msg = new Message();
      msg.setSender(userData);
      msg.setReceiver(receiver);
      msg.setType(MessageType.USER_PRIVATE_TEXT);
      msg.setText(Coder.encrypt(text));
      msg.setDateTime(LocalDateTime.now());
      objOutStream.writeObject(msg);
      objOutStream.flush();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
  
  /* This method is used for sending text Message
   * @param msg - The message text
   */
  public static void sendTextMsg(String msgText) {
    try {
      Message msg = new Message();
      msg.setSender(userData);
      msg.setType(MessageType.USER_PUBLIC_TEXT);
      msg.setText(Coder.encrypt(msgText));
      msg.setDateTime(LocalDateTime.now());
      objOutStream.writeObject(msg);
      objOutStream.flush();
    } catch (IOException ex) {
      System.err.println("Cant send msg to server");
    }
  }
  
  /* This method is used for sending image Message
   * @param msg - The message image
   */
  public static void sendImageMsg(File msgImage) {
    try {
      Message msg = new Message();
      msg.setSender(userData);
      msg.setType(MessageType.USER_PUBLIC_IMAGE);
      msg.setDateTime(LocalDateTime.now());
      msg.setImage(msgImage);
      objOutStream.writeObject(msg);
      objOutStream.flush();
    } catch (IOException ex) {
      System.err.println("Cant send msg to server");
    }
  }
  
  
}
