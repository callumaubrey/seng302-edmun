package com.springvuegradle.team6.startup;

import com.springvuegradle.team6.models.Profile;
import com.springvuegradle.team6.models.ProfileRepository;
import com.springvuegradle.team6.models.Role;
import com.springvuegradle.team6.models.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Component
public class SetupDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (isAlreadySetup())
            return;
        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_USER");

        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        Profile user = new Profile();
        profileRepository.save(user);
        user.setPassword("test");
        user.setEmail("test@test.com");
        Collection<Role> roles = new ArrayList<>(Arrays.asList(adminRole));
        user.setRoles(roles);
    }

    private boolean isAlreadySetup() {
        return profileRepository.findByEmail("test@test.com") != null;
    }


    @Transactional
    private Role createRoleIfNotFound(
            String name) {

        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            roleRepository.save(role);
        }
        return role;
    }
}
