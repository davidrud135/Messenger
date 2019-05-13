package shared;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.scene.control.Alert;

public class DBConnector {
  private static String DB_NAME = "messenger";
  private static String DB_USERNAME = "root";
  private static String DB_PASSWORD = "";
  private static String DB_IP = "localhost";
  private static String DB_ENCODING = "?useUnicode=yes&characterEncoding=UTF-8";
  private static String DB_URL = String.format("jdbc:mysql://%s/%s%s", DB_IP, DB_NAME, DB_ENCODING);
  
  public static Connection connect() {
    Connection conn = null;
    try {
      conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    } catch (SQLException ex) {
      showConnectionErrorAlert(ex.getMessage());
    }
    return conn;
  }
  
  public static Connection connect(String databseName, String username, String password, String hostIP) {
    Connection conn = null;
    DB_NAME = databseName;
    DB_USERNAME = username;
    DB_PASSWORD = password;
    DB_IP = hostIP;
    try {
      conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    } catch (SQLException ex) {
      showConnectionErrorAlert(ex.getMessage());
    }
    return conn;
  }
  
  private static void showConnectionErrorAlert(String errorMsg) {
    Alert dbConnectionAlert = new Alert(Alert.AlertType.ERROR);
    dbConnectionAlert.setHeaderText("Can't connect to Database");
    dbConnectionAlert.setContentText(errorMsg);
    dbConnectionAlert.showAndWait();
  }
  
}