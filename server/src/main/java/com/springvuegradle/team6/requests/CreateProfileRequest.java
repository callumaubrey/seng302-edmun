package com.springvuegradle.team6.requests;

import com.springvuegradle.team6.models.EmailRepository;
import com.springvuegradle.team6.models.Profile;
import com.springvuegradle.team6.models.Email;
import javax.validation.constraints.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateProfileRequest {
    @NotNull
    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$")
    public String firstname;

    @Pattern(regexp = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$")
    public String middlename;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$")
    public String lastname;

    @Pattern(regexp = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$")
    public String nickname;

    @NotNull
    @NotEmpty
    @javax.validation.constraints.Email(message = "Email should be valid")
    public String primary_email;

    @NotNull
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")
    public String password;

    public String bio = "";

    @NotNull
    @NotEmpty
    @Pattern(regexp = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))")
    public String date_of_birth;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "(male)|(female)|(nonbinary)")
    public String gender;

    @Min(value = 0) @Max(value = 3)
    public Integer fitness = 0;

    public Profile generateProfile(EmailRepository emailRepository) {
        Profile profile = new Profile();
        profile.setFirstname(firstname);
        profile.setMiddlename(middlename);
        profile.setLastname(lastname);
        profile.setNickname(nickname);
        Email newEmail = new Email(primary_email);
        emailRepository.save(newEmail);
        profile.setEmail(newEmail);
        profile.setPassword(password);
        profile.setBio(bio);
        profile.setDob(date_of_birth);
        profile.setGender(gender);
        profile.setFitness(fitness);
        return profile;
    }

    public boolean isValidDate(String dob) {
        // Check valid date
        try {
            // Check DOB
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateNow = sdf.parse(sdf.format(new Date()));
            Date dateGiven = sdf.parse(dob);
            if (dateGiven.after(dateNow)) {
                return false;
            }

            Date dateMin = sdf.parse("1900-01-01");
            if (dateGiven.before(dateMin)) {
                return false;
            }
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
