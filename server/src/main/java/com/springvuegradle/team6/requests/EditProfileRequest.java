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
    @NotNull(message = "firstname must be included")
    @NotEmpty(message = "firstname cannot be empty")
    @Pattern(regexp = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$", message = "firstname is invalid")
    public String firstname;

    /**
     * New middle name for profile unchanged if empty
     */
    @Pattern(regexp = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$", message = "middlename is invalid")
    public String middlename;

    /**
     * New last name for profile unchanged if empty
     */
    @NotNull(message = "lastname cannot be empty")
    @NotEmpty(message = "lastname cannot be empty")
    @Pattern(regexp = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$", message = "lastname is invalid")
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
    @NotNull(message = "date_of_birth must be included")
    @NotEmpty(message = "date_of_birth cannot be empty")
    @Pattern(regexp = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))", message = "date of birth is invalid")
    @JsonProperty("date_of_birth")
    public String dob;

    /**
     * New gender for profile unchanged if empty
     */
    @NotNull(message = "gender must be included")
    @NotEmpty(message = "gender cannot be empty")
    @Pattern(regexp = "(male)|(female)|(nonbinary)", message = "gender is invalid")
    public String gender;

    /**
     * New fiteness level for profile unchanged if empty
     */
    @Min(value = 0, message = "fitness cannot be less than 0")
    @Max(value = 4, message = "fitness cannot be larger than 4")
    public Integer fitness = 0;

    /**
     * New activity type for profile
     */
    public Set<ActivityType> activityTypes;

    /**
     * New passport country list for profile unchanged if empty
     */
    public List<@Length(min = 3, max = 3) String> passports;

    @NotNull(message = "primary_email must be included")
    @NotEmpty(message = "primary_email cannot be empty")
    @JsonProperty("primary_email")
    @javax.validation.constraints.Email(message = "Email is invalid")
    public String primaryemail;

    /**
     * List of additional emails
     */
    @EmailCollection @Size(max=5)
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
