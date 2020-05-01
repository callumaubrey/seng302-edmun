package com.springvuegradle.team6.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchProfileResponse {

  @JsonProperty("lastname")
  public String lastname;

  @JsonProperty("firstname")
  public String firstname;

  @JsonProperty("middlename")
  public String middlename;

  @JsonProperty("nickname")
  public String nickname;

  @JsonProperty("primary_email")
  public String primaryEmail;

  public SearchProfileResponse(
      String lastname, String firstname, String middlename, String nickname, String primaryEmail) {
    this.lastname = lastname;
    this.firstname = firstname;
    this.middlename = middlename;
    this.nickname = nickname;
    this.primaryEmail = primaryEmail;
  }
}
