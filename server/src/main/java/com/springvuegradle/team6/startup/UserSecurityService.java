package com.springvuegradle.team6.startup;

import com.springvuegradle.team6.models.ProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpSession;
import java.util.Collection;

public class UserSecurityService {

    private UserSecurityService() {
        throw new IllegalStateException("Utility class");
    }

    public static ResponseEntity<String> checkAuthorised(Integer requestId, HttpSession session, ProfileRepository repository) {
        Object id = session.getAttribute("id");
        if (id == null) {
            return new ResponseEntity<>("Must be logged in", HttpStatus.UNAUTHORIZED);
        }

        if (!(id.toString().equals(requestId.toString()))) {
            Collection<SimpleGrantedAuthority> userRoles = (Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            boolean isAdmin = userRoles.stream().anyMatch(simpleGrantedAuthority -> simpleGrantedAuthority.getAuthority().equals("ROLE_ADMIN"));
            if (!isAdmin) {
                return new ResponseEntity<>("You can only edit your own profile", HttpStatus.UNAUTHORIZED);
            }
        }

        if (!repository.existsById(requestId)) {
            return new ResponseEntity<>("No such user", HttpStatus.NOT_FOUND);
        }
        return null;
    }
}
