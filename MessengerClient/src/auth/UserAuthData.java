package auth;

import java.io.Serializable;

/**
 * Class for sending user auth data.
 * @author David Rudenko
 */
public class UserAuthData implements Serializable {
  
  private String name;
  private String email;
  private String password;
  private AuthType authType;

  /**
   * Creates new instance of this class.
   * @param name - user name, String.
   * @param email - user email, String.
   * @param password - user password, String.
   * @param authType - auth type, {@link auth.AuthType}.
   */
  public UserAuthData(String name, String email, String password, AuthType authType) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.authType = authType;
  }

  /**
   * @return the user name of type String.
   */
  public String getName() {
    return name;
  }

  /**
   * @return the user email of type String.
   */
  public String getEmail() {
    return email;
  }

  /**
   * @return the user password of type String.
   */
  public String getPassword() {
    return password;
  }
  
  /**
   * @return the auth type of type {@link auth.AuthType}.
   */
  public AuthType getAuthType() {
    return authType;
  }
         
}
