package com.springvuegradle.team6.security;

import com.springvuegradle.team6.models.repositories.ProfileRepository;
import java.util.Collection;
import javax.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserSecurityService {

  private UserSecurityService() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Method to check if user is authorised. User is authorised if user is the creator of the
   * information or an admin
   *
   * <p>Checks for admin are implemented through accessing the SecurityContextHolder (the store for
   * Spring Security to hold principal information of authenticated user in the session) and getting
   * the SimpleGrantedAuthority collection (Privileges of the authenticated user stored for Spring
   * Security)
   *
   * @param sessionId HttpSession id
   * @param creatorId Creator id
   * @return true if authorised, false otherwise
   */
  public static boolean checkIsAdminOrCreator(Integer sessionId, Integer creatorId) {
    if (!(sessionId.toString().equals(creatorId.toString()))) {
      Collection<SimpleGrantedAuthority> userRoles =
          (Collection<SimpleGrantedAuthority>)
              SecurityContextHolder.getContext().getAuthentication().getAuthorities();
      boolean isAdmin =
          userRoles.stream()
              .anyMatch(
                  simpleGrantedAuthority ->
                      (simpleGrantedAuthority.getAuthority().equals("ROLE_ADMIN")
                          || simpleGrantedAuthority.getAuthority().equals("ROLE_USER_ADMIN")));
      return isAdmin;
    }
    return true;
  }

  /**
   * Check if user is authorised to access requested user information. User is authorised if they
   * are the owner of the information or an admin. The function also checks if the user is logged in
   * and whether the user is registered in the system
   *
   * @param requestId  user id under query
   * @param session    Http session
   * @param repository profile repository
   * @return ResponseEntity will return 401 if user is not logged in or user is not authorized to
   * edit the requested user's information, or 404 if requested user's profile is not found, else it
   * will return null if user is authorized
   */
  public static ResponseEntity<String> checkAuthorised(
      Integer requestId, HttpSession session, ProfileRepository repository) {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    boolean isAdminOrCreator = checkIsAdminOrCreator((Integer) id, requestId);
    if (!isAdminOrCreator) {
      return new ResponseEntity<>("Only accessible by the owner or admin", HttpStatus.UNAUTHORIZED);
    }

    if (!repository.existsById(requestId)) {
      return new ResponseEntity<>("No such user", HttpStatus.NOT_FOUND);
    }
    return null;
  }

  /**
   * Check if user is authorised to view requested user information. The user is authorised if the
   * user is registered in the system and the user is logged in
   *
   * @param requestId  user id under query
   * @param session    Http session
   * @param repository profile repository
   * @return ResponseEntity will return 401 if user is not logged in or 404 if requested user's
   * profile is not found, else it will return null if user is authorized
   */
  public static ResponseEntity<String> checkViewingPermission(
      Integer requestId, HttpSession session, ProfileRepository repository) {
    Object id = session.getAttribute("id");
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }
    if (!repository.existsById(requestId)) {
      return new ResponseEntity<>("No such user", HttpStatus.NOT_FOUND);
    }
    return null;
  }
}
