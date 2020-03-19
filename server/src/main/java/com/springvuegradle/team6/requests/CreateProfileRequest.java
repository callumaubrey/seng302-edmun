package com.springvuegradle.team6.requests;

import com.springvuegradle.team6.models.EmailRepository;
import com.springvuegradle.team6.models.Profile;
import com.springvuegradle.team6.models.Email;
import javax.validation.constraints.*;

public class CreateProfileRequest {
    @NotNull
    @NotEmpty
    public String firstname;

    public String middlename;

    @NotNull
    @NotEmpty
    public String lastname;

    public String nickname;

    @NotNull
    @javax.validation.constraints.Email(message = "Email should be valid")
    public String primary_email;

    @NotNull
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")
    public String password;

    public String bio = "";

    @NotNull
    @NotEmpty
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
}
