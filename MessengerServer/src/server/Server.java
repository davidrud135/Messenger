package server;

import auth.AuthRespond;
import auth.AuthRespondType;
import auth.AuthType;
import database.DBConnector;
import auth.UserAuthData;
import messages.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
  private static Connection conn = null;
  private static final int PORT = 12345;
  private static HashMap<User, ObjectOutputStream> usersOutStreams = new HashMap<>();
  private static ArrayList<User> usersList = new ArrayList<>();

  public static void main(String[] args) throws IOException {
    ServerSocket listener = new ServerSocket(PORT);
    System.out.println(
      String.format("The chat server is running on port %d.", PORT)
    );
    
    try {
      conn = DBConnector.connect();
      System.out.println("Server has connected to Database.");
    } catch (SQLException ex) {
      System.err.println("Server cant connect to Database.");
      ex.printStackTrace();
    }

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
      
  private static AuthRespond signUpUser(String name, String email, String password) {
    final String signUpQuery =  "INSERT INTO users (name, email, password) VALUES (?, ?, ?);";
    AuthRespond authRespond = new AuthRespond();
    try {
      PreparedStatement prepStatement = conn.prepareStatement(signUpQuery);
      prepStatement.setString(1, name);
      prepStatement.setString(2, email);
      prepStatement.setString(3, password);
      prepStatement.executeUpdate();
      authRespond.setType(AuthRespondType.SIGN_UP_SUCCESS);
    } catch (SQLIntegrityConstraintViolationException ex) {
      authRespond.setType(AuthRespondType.SIGN_UP_EMAIL_DUPLICATE);
      System.out.println(String.format("User with email '%s' already exist!", email));
    } catch (SQLException ex) {
      authRespond.setType(AuthRespondType.SIGN_UP_FAILURE);
      System.err.println("Can't sign up user.");
      ex.printStackTrace();
    }
    return authRespond;
  }
  
  private static AuthRespond signInUser(String email, String password) {
    String signInQuery = "SELECT id, name, email FROM users WHERE email = ? AND password = ?;";
    AuthRespond authRespond = new AuthRespond();
    try {
      PreparedStatement prepStatement = conn.prepareStatement(signInQuery);
      prepStatement.setString(1, email);
      prepStatement.setString(2, password);
      ResultSet signInRS = prepStatement.executeQuery();
      if (signInRS.next()) {
        int userId = signInRS.getInt("id");
        String userName = signInRS.getString("name");
        String userEmail = signInRS.getString("email");
        User signedInUser = new User(userId, userName, userEmail);
        authRespond.setSignedInUserData(signedInUser);
        authRespond.setType(AuthRespondType.SIGN_IN_SUCCESS);
        System.out.println("Success sign in.");
      } else {
        authRespond.setType(AuthRespondType.SIGN_IN_WRONG_DATA);
        System.out.println("Incorrect data.");
      }
    } catch (SQLException ex) {
      authRespond.setType(AuthRespondType.SIGN_IN_FAILURE);
      System.err.println("Can't sign in user.");
      ex.printStackTrace();
    }
    return authRespond;
  }
  
  private static class UserHandler extends Thread {
    private Socket socket;
    private User user;
    private ObjectInputStream objInStream;
    private OutputStream outStream;
    private ObjectOutputStream objOutStream;
    private InputStream inStream;

    public UserHandler(Socket userSocket) throws IOException {
      this.socket = userSocket;
    }

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
      } finally {
        closeConnections();
      }
    }

    private void handleUserAuthDataObj(UserAuthData userAuthData) {
      System.out.println(
        String.format("User with email \"%s\" is trying to login.", userAuthData.getEmail())
      );
      try {
       if (userAuthData.getAuthType() == AuthType.SIGN_UP) {
          AuthRespond signUpResp = signUpUser(
            userAuthData.getName(), 
            userAuthData.getEmail(), 
            userAuthData.getPassword()
          );
          objOutStream.writeObject(signUpResp);
          objOutStream.flush();
        } else {
          AuthRespond signInResp = signInUser(
            userAuthData.getEmail(), 
            userAuthData.getPassword()
          );
          objOutStream.writeObject(signInResp);
          objOutStream.flush();
          if (signInResp.getType() == AuthRespondType.SIGN_IN_SUCCESS) {
            user = signInResp.getSignedInUserData();
            usersOutStreams.put(user, objOutStream);
            usersList.add(user);
            addUserToChat();
          }
        }          
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    
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
          case USER_TEXT:
            writeMessageToChat(msg);
            break;
          case USER_IMAGE:
            writeMessageToChat(msg);
            break;
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }

    private Message removeFromChat() throws IOException {
      System.out.println("removeFromChat() method Enter");
      Message msg = new Message();
      msg.setText("has left the chat.");
      msg.setType(MessageType.DISCONNECTED);
      msg.setDateTime(LocalDateTime.now());
      writeMessageToChat(msg);
      System.out.println("removeFromChat() method Exit");
      return msg;
    }

    /*
     * For displaying that a user has joined the server
     */
    private Message addUserToChat() throws IOException {
      Message msg = new Message();
      msg.setText(user.toString() + " has joined the chat.");
      msg.setType(MessageType.CONNECTED);
      msg.setUsers(usersList);
      writeMessageToChat(msg);
      return msg;
    }

    /*
     * Creates and sends a Message to the listeners.
     */
    private void writeMessageToChat(Message msg) throws IOException {
      for (ObjectOutputStream userOutStream : usersOutStreams.values()) {
        msg.setUsers(usersList);
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
        String.format("Out streams: %d, usersList: %d", usersOutStreams.size(), usersList.size())
      );
      if (objOutStream != null) {
        usersOutStreams.remove(user, objOutStream);
        System.out.println(
          String.format("Out stream of user: %s has been removed!", user.toString())
        );
      }
      if (user != null) {
        usersList.remove(user);
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
        String.format("Out streams: %d, usersList: %d", usersOutStreams.size(), usersList.size())
      );
      System.out.println("closeConnections() method Exit");
    }
  }
}
