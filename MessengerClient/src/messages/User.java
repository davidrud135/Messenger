package messages;

import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable {

  private int id;
  private String name;
  private String email;
  private boolean isOnline;
  private LocalDateTime lastTimeOnline;

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

  /**
   * @return the isOnline
   */
  public boolean getOnlineStatus() {
    return isOnline;
  }

  /**
   * @param isOnline the isOnline to set
   */
  public void setOnlineStatus(boolean isOnline) {
    this.isOnline = isOnline;
  }

  /**
   * @return the lastTimeOnline
   */
  public LocalDateTime getLastTimeOnline() {
    return lastTimeOnline;
  }

  /**
   * @param lastTimeOnline the lastTimeOnline to set
   */
  public void setLastTimeOnline(LocalDateTime lastTimeOnline) {
    this.lastTimeOnline = lastTimeOnline;
  }
  
}
