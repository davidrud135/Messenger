package messenger;

import messages.Message;
import messages.MessageType;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Arrays;
import messages.User;

public class Listener implements Runnable {

  private Socket socket;
  final public String HOSTNAME = "localhost";
  final public int PORT = 12345;
  public static User userData;
  private MessengerController controller;
  private static ObjectOutputStream oos;
  private InputStream is;
  private ObjectInputStream input;
  private OutputStream outputStream;
 
  public Listener(User user, MessengerController controller) {
    Listener.userData = user;
    this.controller = controller;
  }

  public void run() {
    try {
      socket = new Socket(HOSTNAME, PORT);
      outputStream = socket.getOutputStream();
      oos = new ObjectOutputStream(outputStream);
      is = socket.getInputStream();
      input = new ObjectInputStream(is);
    } catch (IOException ex) {
      System.err.println("Could not Connect");
    }
    System.out.println(
      String.format("Connection accepted %s:%d", socket.getInetAddress(), socket.getPort())
    );

    try {
      connect();
      System.out.println("Sockets in and out ready!");
      while (socket.isConnected()) {
        Message message = null;
        message = (Message) input.readObject();

        if (message != null) {
          String senderName = (message.getSender() == null) ? "SERVER": message.getSender().toString();
          System.out.println(
            String.format(
              "Client recieved Message: %s, MessageType: %s, Sender: %s", 
              message.getText(),
              message.getType(),
              senderName
            )
          );
          switch (message.getType()) {
            case USER_TEXT:
              controller.addMessageToChat(message);
              break;
            case USER_IMAGE:
              controller.addImageToChat(message);
              break;
            case NOTIFICATION:
              controller.addNotificationToChat(message);
              break;
            case SERVER:
//              controller.addAsServer(message);
              break;
            case CONNECTED:
              controller.setUserList(message);
              break;
            case DISCONNECTED:
              controller.setUserList(message);
              break;
          }
        }
      }
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  /* This method is used for sending text Message
   * @param msg - The message text
   */
  public static void sendTextMsg(String msgText) {
    try {
      Message msg = new Message();
      msg.setSender(userData);
      msg.setType(MessageType.USER_TEXT);
      msg.setText(msgText);
      msg.setDateTime(LocalDateTime.now());
      oos.writeObject(msg);
      oos.flush();
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
      msg.setType(MessageType.USER_IMAGE);
      msg.setDateTime(LocalDateTime.now());
      msg.setImage(msgImage);
      oos.writeObject(msg);
      oos.flush();
    } catch (IOException ex) {
      System.err.println("Cant send msg to server");
    }
  }

  /* This method is used to send a connecting message */
  public static void connect() throws IOException {
    Message msg = new Message();
    msg.setSender(userData);
    msg.setType(MessageType.CONNECTED);
    msg.setDateTime(LocalDateTime.now());
    oos.writeObject(msg);
    oos.flush();
  }

}
