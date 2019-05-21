package messages;

import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable {

  private int id;
  private String name;
  private String email;

  public User(int id, String name, String email) {
    this.id = id;
    this.name = name;
    this.email = email;
  }
  
  public int getId() {
    return this.id;
  }

  public String toString() {
    return this.name;
  }

  public String getEmail() {
    return this.email;
  }
  
}
