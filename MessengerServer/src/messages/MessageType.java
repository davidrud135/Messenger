package messages;

/**
 * Types of message.
 * @author David Rudenko
 */
public enum MessageType {
  /**
   * Means user has disconnected from server.
   */
  DISCONNECTED,
  /**
   * Means user has connected to server.
   */
  CONNECTED,
  /**
   * Means public message with text.
   */
  USER_PUBLIC_TEXT,
  /**
   * Means public message with image.
   */
  USER_PUBLIC_IMAGE,
  /**
   * Means private message with text.
   */
  USER_PRIVATE_TEXT,
  /**
   * Means private message with image.
   */
  USER_PRIVATE_IMAGE,
  /**
   * Means message from server to client.
   */
  SERVER
}
