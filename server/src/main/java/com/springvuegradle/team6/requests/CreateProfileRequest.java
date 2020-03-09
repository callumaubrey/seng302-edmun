package com.springvuegradle.team6.requests;

import com.springvuegradle.team6.models.Profile;

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
    @Email(message = "Email should be valid")
    public String email;

    @NotNull
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")
    public String password;

    public String bio = "";

    @NotNull
    @NotEmpty
    public String dob;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "(male)|(female)|(nonbinary)")
    public String gender;

    @Min(value = 0) @Max(value = 3)
    public Integer fitness = 0;


    public Profile generateProfile() {
        Profile profile = new Profile();
        profile.setFirstname(firstname);
        profile.setMiddlename(middlename);
        profile.setLastname(lastname);
        profile.setNickname(nickname);
        profile.setEmail(email);
        profile.setPassword(password);
        profile.setBio(bio);
        profile.setDob(dob);
        profile.setGender(gender);
        profile.setFitness(fitness);

        return profile;
    }
}
