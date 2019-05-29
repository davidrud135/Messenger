package auth;

import java.io.Serializable;
import messages.User;

/**
 * Class for sending authentication respond to client.
 * @author David Rudenko
 */
public class AuthRespond implements Serializable {
  
  private User signedInUserData;
  private AuthRespondType type;

  /**
   * @return the signedInUserData of type User.
   */
  public User getSignedInUser() {
    return signedInUserData;
  }

  /**
   * @param signedInUserData signed in user data of type User.
   */
  public void setSignedInUser(User signedInUserData) {
    this.signedInUserData = signedInUserData;
  }
  
  /**
   * @return the type of auth respond.
   */
  public AuthRespondType getType() {
    return type;
  }

  /**
   * @param type the type of auth respond.
   */
  public void setType(AuthRespondType type) {
    this.type = type;
  }
  
}
