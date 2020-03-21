package com.springvuegradle.team6.models;


import com.springvuegradle.team6.exceptions.DuplicateRoleException;
import com.springvuegradle.team6.exceptions.RoleNotFoundException;
import com.springvuegradle.team6.models.location.OSMLocation;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
public class Profile {

    @Id @GeneratedValue
    private Integer id;

    private String firstname;

    private String middlename;

    private String lastname;

    private String nickname;

    @OneToOne
    private Email email;

    @OneToMany
    private Set<Email> additionalemail;

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

    @ManyToMany
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

    public Email getEmail() {
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

    public Set<Country> getPassports() {
        return this.passports;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public Set<Email> getAdditionalemail() {
        return this.additionalemail;
    }

    public Set<ActivityType> getActivityTypes() {
        return this.activityTypes;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setEmail(Email email) {
        this.email = email;

        // Remove if primary email is in additional emails
        if (this.additionalemail != null) {
            this.additionalemail.removeIf(additionalEmail -> this.email.equals(additionalEmail));
        }
    }

    public void setAdditionalemail(Set<Email> emails) {
        this.additionalemail = emails;

        // Remove if primary email is in additional emails
        this.additionalemail.removeIf(additionalEmail -> this.email.equals(additionalEmail));
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
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", bio='" + bio + '\'' +
                ", dob='" + dob + '\'' +
                ", gender='" + gender + '\'' +
                ", fitness=" + fitness + '\'' +
                ", additionalemail='" + additionalemail + '\'' +
                ", passports='" + passports + '\'' +
                '}';
    }
}
