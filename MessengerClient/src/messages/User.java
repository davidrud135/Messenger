package messages;

import java.io.Serializable;

/**
 * Class for storing user data.
 * @author David Rudenko.
 */
public class User implements Serializable {

  private int id;
  private String name;
  private String email;

  /**
   * Creates new instance of this class.
   * @param id - user ID, int.
   * @param name - user name, String.
   * @param email - user email, String.
   */
  public User(int id, String name, String email) {
    this.id = id;
    this.name = name;
    this.email = email;
  }
  
  /**
   * @return user id of type int.
   */
  public int getId() {
    return this.id;
  }

  /**
   * @return user name of type String.
   */
  public String toString() {
    return this.name;
  }

  /**
   * @return user email of type String.
   */
  public String getEmail() {
    return this.email;
  }
  
}
