package auth;

/**
 *
 * @author Daviud Rudenko
 */
public enum AuthRespondType {
  SIGN_UP_SUCCESS,
  SIGN_UP_FAILURE,
  SIGN_UP_DUPLICATE,
  
  SIGN_IN_SUCCESS,
  SIGN_IN_FAILURE,
  SIGN_IN_WRONG_DATA,
  SIGN_IN_DUPLICATE
}
