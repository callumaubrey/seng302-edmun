package com.springvuegradle.team6.models.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class PasswordToken {

    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    private Profile profile;

    private String token;

    public PasswordToken(){}

    public PasswordToken(Profile profile) {
        setProfile(profile);
        generateToken();
    }

    private void generateToken() {
        // return randomized token
        String token = " ";
        this.token = token;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
