package database;

import auth.AuthRespond;
import auth.AuthRespondType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import messages.User;
import server.Server;

/**
 *
 * @author David Rudenko
 */
public class DBCommunicator {
  
  private static Connection conn = null;

  public DBCommunicator() {
    try {
//      conn = DBConnector.connectToRemoteDB();
      conn = DBConnector.connectToLocalDB();
      System.out.println("Server has connected to Database.");
    } catch (SQLException ex) {
      System.err.println("Server cant connect to Database.");
      ex.printStackTrace();
    }
    launchConnectionRefresher();
  }
  
  private static void launchConnectionRefresher() {
    long taskInterval = 1000 * 60 * 60 * 5;
    Timer timer = new Timer ();
    TimerTask task = new TimerTask () {
      @Override
      public void run () {
        try {
          System.out.println("Refreshing DB connection..");
          String query = "SELECT 1";
          Statement st = conn.createStatement();
          st.executeQuery(query);
        } catch (SQLException ex) {
          System.err.println("Can't refresh DB connection.");
          ex.printStackTrace();
        }
      }
    };
    timer.schedule(task, taskInterval, taskInterval);
  }
  
  public static AuthRespond signUpUser(String name, String email, String password) {
    System.out.println(
      String.format("User with email '%s' is trying to Sign Up.", email)
    );
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
      authRespond.setType(AuthRespondType.SIGN_UP_DUPLICATE);
      System.out.println(String.format("User with email '%s' already exists!", email));
    } catch (SQLException ex) {
      authRespond.setType(AuthRespondType.SIGN_UP_FAILURE);
      System.err.println("Can't sign up user.");
      ex.printStackTrace();
    }
    return authRespond;
  }
  
  public static AuthRespond signInUser(String email, String password) {
    System.out.println(
      String.format("User with email '%s' is trying to Sign In.", email)
    );
    final String signInQuery = "SELECT id, name, email FROM users WHERE email = ? AND password = ?;";
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
        authRespond.setSignedInUser(signedInUser);
        authRespond.setType(AuthRespondType.SIGN_IN_SUCCESS);
        System.out.println("Success sign in.");
        if (Server.hasOnlineDuplicate(userId)) {
          authRespond.setType(AuthRespondType.SIGN_IN_DUPLICATE);
        }
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
  
  public static ArrayList<User> getDBUsersList() {
    final String usersQuery = "SELECT id, name, email FROM users;";
    ArrayList<User> usersList = new ArrayList<>();
    try {
      Statement statement = conn.createStatement();
      ResultSet usersRS = statement.executeQuery(usersQuery);
      while (usersRS.next()) {        
        int userId = usersRS.getInt("id");
        String userName = usersRS.getString("name");
        String userEmail = usersRS.getString("email");
        User user = new User(userId, userName, userEmail);
        usersList.add(user);
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return usersList;
  }
  
}
