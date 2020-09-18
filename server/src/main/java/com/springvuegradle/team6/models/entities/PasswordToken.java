package com.springvuegradle.team6.models.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.security.SecureRandom;

@Entity
public class PasswordToken {

    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    private Profile profile;

    private String token;

    static final String ABC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    public PasswordToken(){}

    public PasswordToken(Profile profile) {
        setProfile(profile);
        generateToken();
    }

    public void setProfile(Profile profile) { this.profile = profile; }

    /**
     * Generates a random token of length 16
     */
    private void generateToken() {
        this.token = randomString();
    }

    public String getToken() { return token; }

    public Profile getProfile() { return profile; }

    /**
     * Creates a random string of length 16
     * @return The random string of length 16
     */
    String randomString(){
        int len = 16;
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
            sb.append(ABC.charAt( rnd.nextInt(ABC.length())));
        return sb.toString();
    }
}
