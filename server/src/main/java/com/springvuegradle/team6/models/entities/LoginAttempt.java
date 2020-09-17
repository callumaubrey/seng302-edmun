package com.springvuegradle.team6.models.entities;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * LoginAttempt entity class defines the login attempts made by user, detailing the timestamp of
 * each attempt and the user profile associated to each attempt.
 */
@Entity
public class LoginAttempt {
  @Id @GeneratedValue private Integer id;

  @ManyToOne
  @JoinColumn(name = "profile_id", nullable = false)
  private Profile profile;

  private LocalDateTime timestamp;

  public LoginAttempt() {}

  /**
   * Constructor to create a login attempt object. Timestamp is defaulted to the time when the
   * object is created
   *
   * @param profile profile associated to the login attempt
   */
  public LoginAttempt(Profile profile) {
    this.setProfile(profile);
    this.setTimestampAsNow();
  }

  // ----------- SETTERS -------------------------------------
  private void setProfile(Profile profile) {
    this.profile = profile;
  }

  private void setTimestampAsNow() {
    this.timestamp = LocalDateTime.now();
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  // ----------- GETTERS -------------------------------------
  public Profile getProfile() {
    return this.profile;
  }

  public LocalDateTime getTimestamp() {
    return this.timestamp;
  }

  public Integer getId() {
    return id;
  }
}
