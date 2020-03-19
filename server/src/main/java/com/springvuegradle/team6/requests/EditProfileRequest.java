package com.springvuegradle.team6.requests;

import com.springvuegradle.team6.models.*;
import com.springvuegradle.team6.validators.EmailCollection;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EditProfileRequest {

    /**
     * New first name for profile unchanged if empty
     */
    public String firstname;

    /**
     * New middle name for profile unchanged if empty
     */
    public String middlename;

    /**
     * New last name for profile unchanged if empty
     */
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
    public String dob;

    /**
     * New gender for profile unchanged if empty
     */
    @Pattern(regexp = "(male)|(female)|(nonbinary)")
    public String gender;

    /**
     * New fiteness level for profile unchanged if empty
     */
    @Min(value = 0) @Max(value = 3)
    public Integer fitness = 0;

    /**
     * New passport country list for profile unchanged if empty
     */
    public Set<ActivityType> activityTypes;
    public List<@Length(min = 3, max = 3) String> passports;


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
        if (this.firstname != null && this.firstname != "") {
            edit.setFirstname(this.firstname);
        }
        if (this.lastname != null && this.lastname != "") {
            edit.setLastname(this.lastname);
        }
        if (this.bio != null) {
            edit.setBio(this.bio);
        }
        if (this.nickname != null) {
            edit.setNickname(this.nickname);
        }
        if (this.dob != null && this.dob != "") {
            edit.setDob(this.dob);
        }
        if (this.gender != null && this.gender != "") {
            edit.setGender(this.gender);
        }
        if (this.middlename != null) {
            edit.setMiddlename(this.middlename);
        }
        if (this.fitness != null) {
            edit.setFitness(this.fitness);
        }
        if (this.passports != null) {
            Set<Country> validPassports = new HashSet<>();

            for (String iso : this.passports) {
                Country country = countries.findByIsoCode(iso);
                if (country != null) {
                    validPassports.add(country);
                }
            }
            edit.setPassports(validPassports);
        }

        if (this.primaryemail != null) {
            Email newEmail = new Email(primaryemail);
            emailRepository.save(newEmail);
            edit.setEmail(newEmail);
        }

        if (this.additionalemail != null) {
            Set<Email> emails = new HashSet<>();

            for (String address : this.additionalemail) {
                Email newEmail = new Email(address);

                emailRepository.save(newEmail);
                emails.add(newEmail);
            }

            edit.setAdditionalemail(emails);
        }

        if (this.activityTypes != null) {
            edit.setActivityTypes(this.activityTypes);
        }
    }
}
