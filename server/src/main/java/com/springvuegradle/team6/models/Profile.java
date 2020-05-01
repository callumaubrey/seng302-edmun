package com.springvuegradle.team6.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.exceptions.DuplicateRoleException;
import com.springvuegradle.team6.exceptions.RoleNotFoundException;
import com.springvuegradle.team6.models.location.OSMLocation;
import org.hibernate.search.annotations.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.persistence.Index;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Indexed
@Entity
public class Profile {

  @Id
  @GeneratedValue
  @Column(name = "id")
  private Integer id;

  @Field(
      index = org.hibernate.search.annotations.Index.YES,
      analyze = Analyze.YES,
      store = Store.NO)
  private String firstname;

  @Field(
      index = org.hibernate.search.annotations.Index.YES,
      analyze = Analyze.YES,
      store = Store.NO)
  private String middlename;

  @Field(
      index = org.hibernate.search.annotations.Index.YES,
      analyze = Analyze.YES,
      store = Store.NO)
  private String lastname;

  private String nickname;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "email_id", referencedColumnName = "id")
  private Email email;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  private Set<Email> additionalemail;

  private String password;

  private String bio;

  private String dob;

  private String gender;

  private Integer fitness;

  @OneToOne private OSMLocation location;

  @ElementCollection(targetClass = ActivityType.class)
  @Enumerated(EnumType.ORDINAL)
  private Set<ActivityType> activityTypes;

  @ManyToMany(
      fetch = FetchType.EAGER,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH})
  @JoinTable(
      name = "users_countries",
      joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "country_code", referencedColumnName = "isoCode"))
  private Set<Country> passports;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "users_roles",
      joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
  private Collection<Role> roles;

  public Profile() {}

  public Integer getId() {
    return this.id;
  }

  /**
   * Hashes and sets a plaintext password
   *
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
  public Email getEmail() {
    return this.email;
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
  public Set<Email> getAdditionalemail() {
    return this.additionalemail;
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
  public void setEmail(Email email) {
    this.email = email;
  }

  @JsonProperty("additional_email")
  public void setAdditionalemail(Set<Email> emails) {
    this.additionalemail = emails;
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

  @javax.persistence.Transient
  @Field(
      index = org.hibernate.search.annotations.Index.YES,
      analyze = Analyze.YES,
      store = Store.YES)
  public String getFullname() {
    if (middlename == null) {
      return firstname + " " + lastname;
    }
    return firstname + " " + middlename + " " + lastname;
  }
  /**
   * Hashes a plain text password and compares to stored password hash. If the hashes match returns
   * True.
   *
   * @param password plaintext password
   * @return True on hash match
   */
  public boolean comparePassword(String password) {
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    return passwordEncoder.matches(password, this.password);
  }

  @Override
  public String toString() {
    return "Profile{"
        + "id="
        + id
        + ", firstname='"
        + firstname
        + '\''
        + ", middlename='"
        + middlename
        + '\''
        + ", lastname='"
        + lastname
        + '\''
        + ", nickname='"
        + nickname
        + '\''
        + ", email='"
        + email
        + '\''
        + ", password='"
        + password
        + '\''
        + ", bio='"
        + bio
        + '\''
        + ", dob='"
        + dob
        + '\''
        + ", gender='"
        + gender
        + '\''
        + ", fitness="
        + fitness
        + '\''
        + ", additionalemail='"
        + additionalemail
        + '\''
        + ", passports='"
        + passports
        + '\''
        + '}';
  }
}
