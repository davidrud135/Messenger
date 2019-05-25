package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
  final private static int DB_PORT = 3306;
  final private static String DB_SETTINGS = "?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true";
  
  final private static String LOCAL_DB_NAME = "messenger";
  final private static String LOCAL_DB_USERNAME = "root";
  final private static String LOCAL_DB_PASSWORD = "";
  final private static String LOCAL_DB_HOST = "localhost";
  final private static String LOCAL_DB_URL = String.format("jdbc:mysql://%s:%d/%s%s", LOCAL_DB_HOST, DB_PORT, LOCAL_DB_NAME, DB_SETTINGS);  
  
  final private static String REMOTE_DB_NAME = "sql7292476";
  final private static String REMOTE_DB_USERNAME = "sql7292476";
  final private static String REMOTE_DB_PASSWORD = "Ka7utRYIjz";
  final private static String REMOTE_DB_HOST = "sql7.freemysqlhosting.net";
  final private static String REMOTE_DB_URL = String.format("jdbc:mysql://%s:%d/%s%s", REMOTE_DB_HOST, DB_PORT, REMOTE_DB_NAME, DB_SETTINGS);  
  
  public static Connection connectToLocalDB() throws SQLException {
    return DriverManager.getConnection(LOCAL_DB_URL, LOCAL_DB_USERNAME, LOCAL_DB_PASSWORD);
  }
  
  public static Connection connectToRemoteDB() throws SQLException {
    return DriverManager.getConnection(REMOTE_DB_URL, REMOTE_DB_USERNAME, REMOTE_DB_PASSWORD);
  }
  
}