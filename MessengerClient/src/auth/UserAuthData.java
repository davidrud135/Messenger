package auth;

import java.io.Serializable;

/**
 *
 * @author David Rudenko
 */
public class UserAuthData implements Serializable {
  
  private String name;
  private String email;
  private String password;
  private AuthType authType;

  public UserAuthData(String name, String email, String password, AuthType authType) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.authType = authType;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }
  
  /**
   * @return the authType
   */
  public AuthType getAuthType() {
    return authType;
  }
         
}
