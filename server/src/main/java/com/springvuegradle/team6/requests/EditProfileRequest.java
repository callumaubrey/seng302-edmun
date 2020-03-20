package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team6.models.*;
import com.springvuegradle.team6.models.Email;
import com.springvuegradle.team6.validators.EmailCollection;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EditProfileRequest {

    /**
     * New first name for profile unchanged if empty
     */
    @NotNull
    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$")
    public String firstname;

    /**
     * New middle name for profile unchanged if empty
     */
    @NotNull
    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$")
    public String middlename;

    /**
     * New last name for profile unchanged if empty
     */
    @NotNull
    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$")
    public String lastname;

    /**
     * New nickname for profile unchanged if empty
     */
    public String nickname;

    /**
     * New bio for profile unchanged if empty
     */
    public String bio;

    /**
     * New date of birth for profile unchanged if empty
     */
    @NotNull
    @NotEmpty
    @JsonProperty("date_of_birth")
    public String dob;

    /**
     * New gender for profile unchanged if empty
     */
    @NotNull
    @NotEmpty
    @Pattern(regexp = "(male)|(female)|(nonbinary)")
    public String gender;

    /**
     * New fiteness level for profile unchanged if empty
     */
    @Min(value = 0) @Max(value = 4)
    public Integer fitness = 0;

    /**
     * New activity type for profile
     */
    public Set<ActivityType> activityTypes;

    /**
     * New passport country list for profile unchanged if empty
     */
    public List<@Length(min = 3, max = 3) String> passports;

    @NotNull
    @NotEmpty
    @JsonProperty("primary_email")
    @javax.validation.constraints.Email(message = "Email should be valid")
    public String primaryemail;

    /**
     * List of additional emails
     */
    @EmailCollection @Size(min=0, max=5)
    public List<String> additionalemail;

    /**
     * Takes a profile and uses the info stored in its attributes from a Json
     * to edit the profile
     *
     * @param edit the profile to be edited
     */
    public void editProfileFromRequest(Profile edit, CountryRepository countries, EmailRepository emailRepository) {
            edit.setFirstname(this.firstname);
            edit.setLastname(this.lastname);
            edit.setBio(this.bio);
            edit.setNickname(this.nickname);
            edit.setDob(this.dob);
            edit.setGender(this.gender);
            edit.setMiddlename(this.middlename);
            edit.setFitness(this.fitness);
            Set<Country> validPassports = new HashSet<>();
            if (this.passports != null) {
                for (String iso : this.passports) {
                    Country country = countries.findByIsoCode(iso);
                    if (country != null) {
                        validPassports.add(country);
                    }
                }
            }
            edit.setPassports(validPassports);
            edit.getEmail().setAddress(primaryemail);
            Set<Email> emails = new HashSet<>();
            if (this.additionalemail != null) {
                for (String address : this.additionalemail) {
                    Email newEmail = new Email(address);

                    emailRepository.save(newEmail);
                    emails.add(newEmail);
                }
            }
            edit.setAdditionalemail(emails);
            edit.setActivityTypes(this.activityTypes);
    }
}
