package server;

import messages.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
 
  private static final int PORT = 12345;
  private static HashMap<User, ObjectOutputStream> usersOutStreams = new HashMap<>();
  private static ArrayList<User> users = new ArrayList<>();

  public static void main(String[] args) throws Exception {
    System.out.println(
      String.format("The chat server is running on port %d.", PORT)
    );
    ServerSocket listener = new ServerSocket(PORT);

    try {
      while (true) {
        new UserHandler(listener.accept()).start();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      listener.close();
    }
  }

  private static class UserHandler extends Thread {
    private Socket socket;
    private User user;
    private ObjectInputStream input;
    private OutputStream os;
    private ObjectOutputStream output;
    private InputStream is;

    public UserHandler(Socket userSocket) throws IOException {
      this.socket = userSocket;
    }

    public void run() {
      System.out.println("Attempting to connect a user...");
      try {
        is = socket.getInputStream();
        input = new ObjectInputStream(is);
        os = socket.getOutputStream();
        output = new ObjectOutputStream(os);

        Message firstMessage = (Message) input.readObject();
        System.out.println(
          String.format(
            "Server recieved Message: %s, MessageType: %s, Sender: %s", 
            firstMessage.getText(), 
            firstMessage.getType(), 
            firstMessage.getSender().toString()
          )
        );
        
        User newUser = firstMessage.getSender();
        user = newUser;
        usersOutStreams.put(newUser, output);
        users.add(newUser);
        sendNotification(firstMessage);
        addToList();

        while (socket.isConnected()) {
          Message inputmsg = (Message) input.readObject();
          if (inputmsg != null) {
            System.out.println(
              String.format(
                "Server recieved Message: %s, MessageType: %s, Sender: %s", 
                inputmsg.getText(),
                inputmsg.getType(),
                inputmsg.getSender().toString()
              )
            );
            switch (inputmsg.getType()) {
              case USER_TEXT:
                write(inputmsg);
                break;
              case USER_IMAGE:
                write(inputmsg);
                break;
              case CONNECTED:
                addToList();
                break;
            }
          }
        }
      } catch (SocketException socketEx) {
        System.err.println(
          String.format("Socket Exception for user %s", user.toString())
        );
      } catch (Exception ex) {
        System.err.println(
          String.format("Exception in run() method for user: %s", user.toString())
        );
      } finally {
        closeConnections();
      }
    }

    private Message sendNotification(Message firstMessage) throws IOException {
      Message msg = new Message();
      msg.setText(firstMessage.getSender().toString() + " has joined the chat.");
      msg.setType(MessageType.NOTIFICATION);
      write(msg);
      return msg;
    }

    private Message removeFromList() throws IOException {
      System.out.println("removeFromList() method Enter");
      Message msg = new Message();
      msg.setText("has left the chat.");
      msg.setType(MessageType.DISCONNECTED);
      msg.setDateTime(LocalDateTime.now());
      write(msg);
      System.out.println("removeFromList() method Exit");
      return msg;
    }

    /*
     * For displaying that a user has joined the server
     */
    private Message addToList() throws IOException {
      Message msg = new Message();
      msg.setText("Welcome, You have now joined the server! Enjoy chatting!");
      msg.setType(MessageType.CONNECTED);
      msg.setUsers(users);
      write(msg);
      return msg;
    }

    /*
     * Creates and sends a Message to the listeners.
     */
    private void write(Message msg) throws IOException {
      for (ObjectOutputStream userOutStream : usersOutStreams.values()) {
        msg.setUsers(users);
        userOutStream.writeObject(msg);
        userOutStream.reset();
      }
    }

    /*
     * Once a user has been disconnected, we close the open connections and remove the writers
     */
    private synchronized void closeConnections()  {
      System.out.println("closeConnections() method Enter");
      System.out.println(
        String.format("Out streams: %d, users: %d", usersOutStreams.size(), users.size())
      );
      if (output != null) {
        usersOutStreams.remove(user, output);
        System.out.println(
          String.format("Out stream of user: %s has been removed!", user.toString())
        );
      }
      if (user != null) {
        users.remove(user);
        System.out.println(
          String.format("User object: %s has been removed!", user.toString())
        );
      }
      try {
        if (is != null) is.close();
        if (os != null) os.close();
        if (input != null) input.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        removeFromList();
      } catch (Exception e) {
        e.printStackTrace();
      }
      System.out.println(
        String.format("Out streams: %d, users: %d", usersOutStreams.size(), users.size())
      );
      System.out.println("closeConnections() method Exit");
    }
  }
}
