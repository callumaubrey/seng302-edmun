package com.springvuegradle.team6.startup;

import com.springvuegradle.team6.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@Component
public class SetupDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailRepository emailRepository;

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
        Email email = new Email(("test@test.com"));
        emailRepository.save(email);
        user.setEmail(email);
        Collection<Role> roles = new ArrayList<>(Arrays.asList(adminRole));
        user.setRoles(roles);
    }

    private boolean isAlreadySetup() {
        Optional<Email> email = emailRepository.findByAddress("test@test.com");
        return email.isPresent();
    }


    @Transactional
    public void createRoleIfNotFound(String name) {

        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            roleRepository.save(role);
        }
    }
}
