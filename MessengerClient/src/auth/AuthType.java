package auth;

/**
 * Types of authentication.
 * @author David Rudenko
 */
public enum AuthType {
  /**
   * New user.
   * Required data: username, email, password.
   */
  SIGN_UP,
  /**
   * Already signed up user.
   * Required data: email, password.
   */
  SIGN_IN
}
