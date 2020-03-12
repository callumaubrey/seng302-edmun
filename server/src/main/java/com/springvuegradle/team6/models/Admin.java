package com.springvuegradle.team6.models;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Admin {

    @Id @GeneratedValue
    private Integer Id;

    private String username;

    private String password;

    public Admin() {
        this.setPassword("Welcome1");
        this.setUsername("DefaultAdmin");
    }

    public Admin(String username, String password) {
        this.setPassword(password);
        this.username = username;
    }

    /**
     * Hashes and sets a plaintext password
     * @param password
     */
    public void setPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAdminId() {
        return Id;
    }
}
