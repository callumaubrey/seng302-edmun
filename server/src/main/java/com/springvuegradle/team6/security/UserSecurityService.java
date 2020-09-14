package com.springvuegradle.team6.security;

import com.springvuegradle.team6.models.entities.Activity;
import com.springvuegradle.team6.models.entities.ActivityRole;
import com.springvuegradle.team6.models.entities.VisibilityType;
import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ActivityRoleRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import java.util.Collection;
import java.util.Optional;
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
      return new ResponseEntity<>("Only accessible by the owner or admin", HttpStatus.FORBIDDEN);
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

  /**
   * Checks if user is authorised to view a requested activity. The user is authorised if they are logged in,
   * the activity exists, and they are either an admin, creator or have permission to restricted profile.
   * @param activity_id the activity the user is trying to view
   * @param session Http Session
   * @param activityRepository activity repository
   * @param roleRepository activity role repository
   * @return null on success otherwise 404 on activity not found or a 401 with message why.
   */
  public static ResponseEntity<String> checkActivityViewingPermission(Integer activity_id, HttpSession session, ActivityRepository activityRepository, ActivityRoleRepository roleRepository) {
    Integer id = (Integer) session.getAttribute("id");

    // Check if logged in
    if (id == null) {
      return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
    }

    // Check if activity exists
    Optional<Activity> optionalActivity = activityRepository.findById(activity_id);
    if (optionalActivity.isEmpty()) {
      return new ResponseEntity<>("No such activity", HttpStatus.NOT_FOUND);
    }
    Activity activity = optionalActivity.get();

    // Check if admin
    if (checkIsAdminOrCreator(id, activity.getProfile().getId())) {
      return null;
    }

    // Check if user has rights to view it
    if (activity.getVisibilityType() == VisibilityType.Private) {
      return new ResponseEntity<>("Activity is private", HttpStatus.UNAUTHORIZED);
    }

    if (activity.getVisibilityType() == VisibilityType.Restricted) {
      ActivityRole activityRoles = roleRepository.findByProfile_IdAndActivity_Id(id, activity_id);
      if (activityRoles == null) {
        return new ResponseEntity<>("Activity is restricted", HttpStatus.UNAUTHORIZED);
      }
    }

    return null;
  }

  /**
   * Method to check if user is authorised. User is authorised if user is of role (ROLE_ADMIN or
   * ROLE_USER_ADMIN)
   *
   * <p>Checks for admin are implemented through accessing the SecurityContextHolder (the store for
   * Spring Security to hold principal information of authenticated user in the session) and getting
   * the SimpleGrantedAuthority collection (Privileges of the authenticated user stored for Spring
   * Security)
   *
   * @return true if user is an admin, otherwise false
   */
  public static boolean checkIsAdmin() {
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
}