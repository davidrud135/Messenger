package auth;

/**
 * Types of auth respond.
 * @author Daviud Rudenko
 */
public enum AuthRespondType {
  /**
   * Means user has signed up successfully.
   */
  SIGN_UP_SUCCESS,
  /**
   * Means user signing up has failed.
   */
  SIGN_UP_FAILURE,
  /**
   * Means user with given email already exists.
   */
  SIGN_UP_DUPLICATE,
  
  /**
   * Means user has signed in successfully.
   */
  SIGN_IN_SUCCESS,
  /**
   * Means user signing in has failed.
   */
  SIGN_IN_FAILURE,
  /**
   * Means user has typed wrong data.
   */
  SIGN_IN_WRONG_DATA,
  /**
   * Means user already signed in.
   */
  SIGN_IN_DUPLICATE
}
