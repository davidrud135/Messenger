package server;

import auth.AuthRespond;
import auth.AuthRespondType;
import auth.AuthType;
import auth.UserAuthData;
import database.DBCommunicator;
import messages.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main class which communicates with clients and DB.
 * @author David Rudenko
 */
public class Server {
  
  private static final int PORT = 12345;
  private static HashMap<User, ObjectOutputStream> onlineUsersOutStream;
  private static ArrayList<User> allUsersList;
  public static ArrayList<User> onlineUsersList;

  /**
   * Launches listener for clients and connects to DB.
   * @param args input arguments
   * @throws IOException if can't get initialize ServerSocket.
   */
  public static void main(String[] args) throws IOException {
    ServerSocket listener = new ServerSocket(PORT);
    System.out.println(
      String.format("The chat server is running on port %d.", PORT)
    );
    
    new DBCommunicator();
    onlineUsersOutStream = new HashMap<>();
    onlineUsersList = new ArrayList<>();
    allUsersList = DBCommunicator.getDBUsersList();
    
    try {
      while (true) {
        new UserHandler(listener.accept()).start();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      listener.close();
    }
  }
  
  /**
   * Class for handling user data and in/out streams.
   */
  private static class UserHandler extends Thread {
    private Socket socket;
    private User user;
    private ObjectInputStream objInStream;
    private OutputStream outStream;
    private ObjectOutputStream objOutStream;
    private InputStream inStream;
    
    /**
     * Creates new instance of this class.
     * @param userSocket - socket of new user.
     * @throws IOException if can't get socket or its streams.
     */
    public UserHandler(Socket userSocket) throws IOException {
      this.socket = userSocket;
    }

    /**
     * Listens for client's input data and handles it.
     */
    public void run() {
      System.out.println("Attempting to connect a user...");
      try {
        inStream = socket.getInputStream();
        objInStream = new ObjectInputStream(inStream);
        outStream = socket.getOutputStream();
        objOutStream = new ObjectOutputStream(outStream);

        while (socket.isConnected()) {
          Object inputObj = objInStream.readObject();
          if (inputObj instanceof UserAuthData) {
            handleUserAuthDataObj((UserAuthData) inputObj);
          } else if (inputObj instanceof Message) {
            handleMessageObj((Message) inputObj);
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
        ex.printStackTrace();
      } finally {
        closeConnections();
      }
    }

    /**
     * Handles user's auth data and sends respond.
     * @param userAuthData 
     */
    private void handleUserAuthDataObj(UserAuthData userAuthData) {
      try {
       if (userAuthData.getAuthType() == AuthType.SIGN_UP) {
          AuthRespond signUpResp = DBCommunicator.signUpUser(
            userAuthData.getName(), 
            userAuthData.getEmail(), 
            userAuthData.getPassword()
          );
          objOutStream.writeObject(signUpResp);
          objOutStream.flush();
          if (signUpResp.getType() == AuthRespondType.SIGN_UP_SUCCESS) {
            allUsersList = DBCommunicator.getDBUsersList();
          }
        } else {
          AuthRespond signInResp = DBCommunicator.signInUser(
            userAuthData.getEmail(), 
            userAuthData.getPassword()
          );
          objOutStream.writeObject(signInResp);
          objOutStream.flush();
          if (signInResp.getType() == AuthRespondType.SIGN_IN_SUCCESS) {
            user = signInResp.getSignedInUser();
            onlineUsersOutStream.put(user, objOutStream);
            onlineUsersList.add(user);
            addUserToChat();
          }
        }          
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    
    /**
     * Handles messages from users.
     * @param msg users message object of type {@link messages.Message}.
     */
    private void handleMessageObj(Message msg) {
      try {
        System.out.println(
          String.format(
            "Server recieved Message: %s, MessageType: %s, Sender: %s", 
            msg.getText(),
            msg.getType(),
            msg.getSender().toString()
          )
        );
        switch (msg.getType()) {
          case USER_PUBLIC_TEXT:
            writeMessageToChat(msg);
            break;
          case USER_PUBLIC_IMAGE:
            writeMessageToChat(msg);
            break;
          case USER_PRIVATE_TEXT:
            writePrivateMessageToUser(msg);
            break;
          case USER_PRIVATE_IMAGE:
            writePrivateMessageToUser(msg);
            break;
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    
    /**
     * Once user left the chat, server removes user from online users lists.
     * Then sends message to clients with updated online users list.
     * @throws IOException if can't send message to client.
     */
    private void removeFromChat() throws IOException {
      Message msg = new Message();
      msg.setText(user.toString() + " has left the chat.");
      msg.setType(MessageType.DISCONNECTED);
      msg.setDateTime(LocalDateTime.now());
      writeMessageToChat(msg);
    }

    /**
     * Once user has joined the chat, user added to online users list.
     * Then sends message to clients with updated online users list.
     * @throws IOException if can't send message to client.
     */
    private void addUserToChat() throws IOException {
      Message msg = new Message();
      msg.setText(user.toString() + " has joined the chat.");
      msg.setType(MessageType.CONNECTED);
      writeMessageToChat(msg);
    }

    /**
     * Writes private message to given user.
     * @param msg message object of type {@link messages.Message}.
     * @throws IOException if can't send private message.
     */
    private void writePrivateMessageToUser(Message msg) throws IOException {
      int receiverId = msg.getReceiver().getId();
      User receiverUser = null;
      for (User user : onlineUsersList) {
        if (user.getId() == receiverId) {
          receiverUser = user;
          break;
        }
      }
      System.out.println(
        String.format("Private msg from %s to %s", user.toString(), receiverUser.toString())
      );
      ObjectOutputStream receiverObjectOutputStream = onlineUsersOutStream.get(receiverUser);
      receiverObjectOutputStream.writeObject(msg);
      receiverObjectOutputStream.flush();
      ObjectOutputStream senderObjectOutputStream = onlineUsersOutStream.get(user);
      senderObjectOutputStream.writeObject(msg);
      senderObjectOutputStream.flush();
    }
    
    /**
     * Writes message to all online users.
     * @param msg message object of type {@link messages.Message}.
     */
    private void writeMessageToChat(Message msg) {
      try {
        for (ObjectOutputStream userOutStream : onlineUsersOutStream.values()) {
          msg.setAllUsersList(allUsersList);
          msg.setOnlineUsersList(onlineUsersList);
          userOutStream.writeObject(msg);
          userOutStream.reset();
        }
      } catch (IOException ex) {
        System.out.println("Cant write msg to some user.");
        ex.printStackTrace();
      }
    }

    /*
     * Once a user has been disconnected, we close the open connections and remove him from online users list.
     */
    private synchronized void closeConnections()  {
      System.out.println("closeConnections() method Enter");
      System.out.println(
        String.format("Out streams: %d, onlineUsersList: %d", onlineUsersOutStream.size(), onlineUsersList.size())
      );
      if (objOutStream != null) {
        onlineUsersOutStream.remove(user, objOutStream);
        System.out.println(
          String.format("Out stream of user: %s has been removed!", user.toString())
        );
      }
      if (user != null) {
        onlineUsersList.remove(user);
        System.out.println(
          String.format("User object: %s has been removed!", user.toString())
        );
      }
      try {
        if (inStream != null) inStream.close();
        if (outStream != null) outStream.close();
        if (objInStream != null) objInStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        removeFromChat();
      } catch (Exception e) {
        e.printStackTrace();
      }
      System.out.println(
        String.format("Out streams: %d, onlineUsersList: %d", onlineUsersOutStream.size(), onlineUsersList.size())
      );
      System.out.println("closeConnections() method Exit");
    }
  }
  
  /**
   * Searches for user duplicate in online users list.
   * @param userId - user's unique id, int.
   * @return true if there is duplicate user with given id, otherwise false.
   */
  public static boolean hasOnlineDuplicate(int userId) {
    for (User onlineUser : onlineUsersList) {
      if (userId == onlineUser.getId()) {
        return true;
      }
    }
    return false;
  }
  
}
