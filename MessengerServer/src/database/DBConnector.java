package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class for connecting to DB.
 * @author User
 */
public class DBConnector {
  final private static int DB_PORT = 3306;
  final private static String DB_SETTINGS = "?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true";
  
  final private static String LOCAL_DB_NAME = "messenger";
  final private static String LOCAL_DB_USERNAME = "root";
  final private static String LOCAL_DB_PASSWORD = "";
  final private static String LOCAL_DB_HOST = "localhost";
  final private static String LOCAL_DB_URL = String.format("jdbc:mysql://%s:%d/%s%s", LOCAL_DB_HOST, DB_PORT, LOCAL_DB_NAME, DB_SETTINGS);  
  
  final private static String REMOTE_DB_NAME = "7NiOI4B8W1";
  final private static String REMOTE_DB_USERNAME = "7NiOI4B8W1";
  final private static String REMOTE_DB_PASSWORD = "qTO2qu8Joz";
  final private static String REMOTE_DB_HOST = "remotemysql.com";
  final private static String REMOTE_DB_URL = String.format("jdbc:mysql://%s:%d/%s%s", REMOTE_DB_HOST, DB_PORT, REMOTE_DB_NAME, DB_SETTINGS);  
  
  /**
   * Connects to local MySQL DB.
   * @return successful connection.
   * @throws SQLException if can't connect.
   */
  public static Connection connectToLocalDB() throws SQLException {
    return DriverManager.getConnection(LOCAL_DB_URL, LOCAL_DB_USERNAME, LOCAL_DB_PASSWORD);
  }
  
  /**
   * Connects to remote MySQL DB.
   * @return successful connection.
   * @throws SQLException if can't connect.
   */
  public static Connection connectToRemoteDB() throws SQLException {
    return DriverManager.getConnection(REMOTE_DB_URL, REMOTE_DB_USERNAME, REMOTE_DB_PASSWORD);
  }
  
}