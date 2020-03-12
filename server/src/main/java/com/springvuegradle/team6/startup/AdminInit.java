package com.springvuegradle.team6.startup;

import com.springvuegradle.team6.models.Admin;
import com.springvuegradle.team6.models.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class AdminInit {

    @Autowired
    private AdminRepository repository;

    /**
     *
     */
    @PostConstruct
    public void init() {
        if (repository.findByUsername("DefaultAdmin") == null) {
            Admin def = new Admin();
            repository.save(def);
            System.out.println("Default admin created.");
        } else {
            System.out.println("Default admin already exists.");
        }
    }
}
