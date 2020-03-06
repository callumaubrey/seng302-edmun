package com.springvuegradle.team6.models;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Profile {

    @Id @GeneratedValue
    private Integer id;

    private String firstname;

    private String middlename;

    private String lastname;

    private String nickname;

    private String email;

    private String additionalemail;

    private String password;

    private String bio;

    private String dob;

    private String gender;
    
    private Integer fitness;

    private String passport;

    public Profile() {}

    public Integer getId() {
        return this.id;
    }

    /**
     * Hashes and sets a plaintext password
     * @param password
     */
    public void setPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }

    public String getFirstname() {
        return this.firstname;
    }

    public String getMiddlename() {
        return this.middlename;
    }

    public String getLastname() {
        return this.lastname;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getBio() {
        return this.bio;
    }

    public String getDob() {
        return this.dob;
    }

    public String getGender() {
        return this.gender;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAdditionalemail(String emails) {
        this.additionalemail = emails;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setFitness(Integer fitness) {
        this.fitness = fitness;
    }

    public void setPassport(String passports) {
        this.passport = passports;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Hashes a plain text password and compares to stored password hash. If the hashes match returns True.
     * @param password plaintext password
     * @return True on hash match
     */
    public boolean comparePassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(password, this.password);
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", middlename='" + middlename + '\'' +
                ", lastname='" + lastname + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", bio='" + bio + '\'' +
                ", dob='" + dob + '\'' +
                ", gender='" + gender + '\'' +
                ", fitness=" + fitness + '\'' +
                ", additionalemail='" + additionalemail + '\'' +
                ", passports='" + passport + '\'' +
                '}';
    }
}
