package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.requests.objects.EmailRolePair;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class EditActivityVisibilityRequest {

  @NotNull
  @NotEmpty
  @JsonProperty("visibility")
  private String visibility;

  @JsonProperty("accessors")
  private List<EmailRolePair> accessors;

  public String getVisibility() {
    return visibility;
  }

  public List<EmailRolePair> getAccessors() {
    return accessors;
  }
}
