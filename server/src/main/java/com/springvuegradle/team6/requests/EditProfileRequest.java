package com.springvuegradle.team6.requests;

import com.springvuegradle.team6.models.*;

import javax.validation.constraints.*;
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
    @Min(value = 0)
    @Max(value = 3)
    public Integer fitness = 0;

    /**
     * New passport country list for profile unchanged if empty
     */
    public List<String> passports;
    public Set<ActivityType> activityTypes;

    /**
     * Takes a profile and uses the info stored in its attributes from a Json
     * to edit the profile
     *
     * @param edit the profile to be edited
     */
    public void editProfileFromRequest(Profile edit, CountryRepository countries) {
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

        if (this.activityTypes != null) {
            edit.setActivityTypes(this.activityTypes);
        }
    }
}
