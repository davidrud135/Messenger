package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
  private static String DB_NAME = "messenger";
  private static String DB_USERNAME = "root";
  private static String DB_PASSWORD = "";
  private static String DB_IP = "localhost";
  private static String DB_ENCODING = "?useUnicode=yes&characterEncoding=UTF-8";
  private static String DB_URL = String.format("jdbc:mysql://%s/%s%s", DB_IP, DB_NAME, DB_ENCODING);
  
  public static Connection connect() throws SQLException {
    return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
  }
  
  public static Connection connect(String databseName, String username, String password, String hostIP) throws SQLException {
    DB_NAME = databseName;
    DB_USERNAME = username;
    DB_PASSWORD = password;
    DB_IP = hostIP;
    return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
  }
  
}