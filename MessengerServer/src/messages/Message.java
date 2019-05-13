package messages;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Message implements Serializable {

  private User sender;
  private MessageType type;
  private String text;
  private LocalDateTime sentDateTime;
  private ArrayList<User> users;
  private File image;

  /**
   * @return the sender
   */
  public User getSender() {
    return sender;
  }

  /**
   * @param sender the sender to set
   */
  public void setSender(User sender) {
    this.sender = sender;
  }

  /**
   * @return the type
   */
  public MessageType getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(MessageType type) {
    this.type = type;
  }

  /**
   * @return the text
   */
  public String getText() {
    return text;
  }

  /**
   * @param text the text to set
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * @return the sentDateTime
   */
  public LocalDateTime getDateTime() {
    return sentDateTime;
  }

  /**
   * @param sentDateTime the sentDateTime to set
   */
  public void setDateTime(LocalDateTime sentDateTime) {
    this.sentDateTime = sentDateTime;
  }

  /**
   * @return the users
   */
  public ArrayList<User> getUsers() {
    return users;
  }

  /**
   * @param users the users to set
   */
  public void setUsers(ArrayList<User> users) {
    this.users = users;
  }

  /**
   * @return the image
   */
  public File getImage() {
    return image;
  }

  /**
   * @param image the image to set
   */
  public void setImage(File image) {
    this.image = image;
  }
}
