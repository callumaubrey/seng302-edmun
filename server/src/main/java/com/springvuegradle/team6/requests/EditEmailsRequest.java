package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.Email;
import com.springvuegradle.team6.validators.EmailCollection;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

public class EditEmailsRequest {
    /**
     * The primary email of the user, used for them to log in.
     */
    @NotNull
    @NotEmpty
    @JsonProperty("primary_email")
    @javax.validation.constraints.Email
    public String primaryEmail;

    @EmailCollection
    @Size(max=5)
    @JsonProperty("additional_email")
    public List<String> additionalEmail;

}
