package com.springvuegradle.team6.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.exceptions.DuplicateRoleException;
import com.springvuegradle.team6.exceptions.RoleNotFoundException;
import com.springvuegradle.team6.models.location.OSMLocation;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.*;

@Entity
public class Profile {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    private String firstname;

    private String middlename;

    private String lastname;

    private String nickname;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Email> emails;

    private String password;

    private String bio;

    private String dob;

    private String gender;

    private Integer fitness;

    @OneToOne
    private OSMLocation location;

    @ElementCollection(targetClass=ActivityType.class)
    @Enumerated(EnumType.ORDINAL)
    private Set<ActivityType> activityTypes;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH})
    @JoinTable(
            name = "users_countries",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "country_code", referencedColumnName = "isoCode"))
    private Set<Country> passports;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

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

    @JsonProperty("primary_email")
    public Email getPrimaryEmail() {
        for(Email email: emails) {
            if(email.isPrimary()) return email;
        }

        // If no primary email exists set first email to be primary
        if(!emails.isEmpty()) {
            Email newPrimary = emails.iterator().next();
            newPrimary.setPrimary(true);
            System.getLogger("SystemEvents").log(System.Logger.Level.INFO, String.format("User %d has no primary email so %s was set as primary", getId(), newPrimary.getAddress()));

            return newPrimary;
        } else {
            System.getLogger("SystemEvents").log(System.Logger.Level.WARNING, String.format("User %d contains no emails", getId()));
            return new Email("null");
        }
    }

    public String getPassword() {
        return this.password;
    }

    public String getBio() {
        return this.bio;
    }

    @JsonProperty("date_of_birth")
    public String getDob() {
        return this.dob;
    }

    public String getGender() {
        return this.gender;
    }

    public Set<Country> getPassports() {
        return this.passports;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    @JsonProperty("additional_email")
    public Set<Email> getEmails() {
        return this.emails;
    }

    @JsonProperty("activities")
    public Set<ActivityType> getActivityTypes() {
        return this.activityTypes;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @JsonProperty("date_of_birth")
    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFitness() {
        return fitness;
    }

    @JsonProperty("primary_email")
    public void setPrimaryEmail(Email email) {
        if(emails == null) emails = new HashSet<>();
        this.emails.add(email);
    }

    @JsonProperty("additional_email")
    public void setEmails(Set<Email> emails) {
        if(emails == null) emails = new HashSet<>();
        clearNonPrimaryEmails();
        this.emails.addAll(emails);
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setFitness(Integer fitness) {
        this.fitness = fitness;
    }

    public void setPassports(Set<Country> passports) {
        this.passports = passports;
    }

    @JsonProperty("activities")
    public void setActivityTypes(Set<ActivityType> activityTypes) {
        this.activityTypes = activityTypes;
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

    public void setRoles(final Collection<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role newRole) throws DuplicateRoleException {
        for (Role role : roles) {
            if (role.getRoleName().equals(newRole.getRoleName())) {
                throw new DuplicateRoleException();
            }
        }
        roles.add(newRole);
    }

    public void removeRole(String roleName) throws RoleNotFoundException {
        List<Role> roleList = new ArrayList(roles);
        for (int i = 0; i < roles.size(); i++) {
            if (roleList.get(i).getRoleName().equals(roleName)) {
                roles.remove(roleList.get(i));
                return;
            }
        }
        throw new RoleNotFoundException();
    }

    public OSMLocation getLocation() {
        return location;
    }

    public void setLocation(OSMLocation location) {
        this.location = location;
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
                ", email='" +  getPrimaryEmail() + '\'' +
                ", password='" + password + '\'' +
                ", bio='" + bio + '\'' +
                ", dob='" + dob + '\'' +
                ", gender='" + gender + '\'' +
                ", fitness=" + fitness + '\'' +
                ", additionalemail='" + getEmails() + '\'' +
                ", passports='" + passports + '\'' +
                '}';
    }

    /**
     * Clears all emails except for the primary email in emails
     */
    private void clearNonPrimaryEmails() {
        emails.removeIf(email -> !email.isPrimary());
    }
}
