package messages;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Message implements Serializable {

  private User sender;
  private User receiver;
  private MessageType type;
  private String text;
  private LocalDateTime sentDateTime;
  private ArrayList<User> allUsersList;
  private ArrayList<User> onlineUsersList;
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
   * @return the onlineUsersList
   */
  public ArrayList<User> getOnlineUsersList() {
    return onlineUsersList;
  }

  /**
   * @param onlineUsersList the onlineUsersList to set
   */
  public void setOnlineUsersList(ArrayList<User> onlineUsersList) {
    this.onlineUsersList = onlineUsersList;
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

  /**
   * @return the receiver
   */
  public User getReceiver() {
    return receiver;
  }

  /**
   * @param receiver the receiver to set
   */
  public void setReceiver(User receiver) {
    this.receiver = receiver;
  }

  /**
   * @return the allUsersList
   */
  public ArrayList<User> getAllUsersList() {
    return allUsersList;
  }

  /**
   * @param allUsersList the allUsersList to set
   */
  public void setAllUsersList(ArrayList<User> allUsersList) {
    this.allUsersList = allUsersList;
  }

}
