package auth;

import java.io.Serializable;
import messages.User;

/**
 *
 * @author David Rudenko
 */
public class AuthRespond implements Serializable {
  
  private User signedInUserData;
  private AuthRespondType type;


  /**
   * @return the signedInUserData
   */
  public User getSignedInUserData() {
    return signedInUserData;
  }

  /**
   * @param signedInUserData the signedInUserData to set
   */
  public void setSignedInUserData(User signedInUserData) {
    this.signedInUserData = signedInUserData;
  }
  
  /**
   * @return the type
   */
  public AuthRespondType getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(AuthRespondType type) {
    this.type = type;
  }
  
}
