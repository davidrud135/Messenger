package messages;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Class for data communication between client and server.
 * @author David Rudenko
 */
public class Message implements Serializable {

  private User sender;
  private User receiver;
  private MessageType type;
  private String text;
  private LocalDateTime sentDateTime;
  private ArrayList<User> allUsersList;
  private ArrayList<User> onlineUsersList;
  private byte[] imageBytes;

  /**
   * @return the message sender of type {@link messages.User}.
   */
  public User getSender() {
    return sender;
  }

  /**
   * @param sender the message sender, {@link messages.User}.
   */
  public void setSender(User sender) {
    this.sender = sender;
  }

  /**
   * @return the message type, {@link messages.MessageType}.
   */
  public MessageType getType() {
    return type;
  }

  /**
   * @param type the message type, {@link messages.MessageType}.
   */
  public void setType(MessageType type) {
    this.type = type;
  }

  /**
   * @return the message text, String.
   */
  public String getText() {
    return text;
  }

  /**
   * @param text the message text of type String.
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

  /**
   * @return the image bytes.
   */
  public byte[] getImageBytes() {
    return imageBytes;
  }

  /**
   * @param imageBytes sets image bytes.
   */
  public void setImageBytes(byte[] imageBytes) {
    this.imageBytes = imageBytes;
  }

}
