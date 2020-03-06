package com.springvuegradle.team6.requests;

import javax.validation.constraints.*;
import java.util.List;
import java.util.regex.Pattern;

public class EditEmailRequest {

    /**
     * Regex to validate user emails in list
     */
    final private Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
    
    /**
     * User id
     */
    @NotNull
    public Integer id;

    /**
     * User email cannot be null and must be an email
     */
    @NotNull
    @Email(message = "Email should be valid")
    public String primaryemail;

    /**
     * List of additional emails
     */
    public List<String> additionalemail;

    /**
     * Checks given data is a valid email
     *
     * @param value
     * @return boolean, true if valid, false if not
     */
    public boolean isValidEmail(String value) {
        return emailPattern.matcher(value).matches();
    }
}