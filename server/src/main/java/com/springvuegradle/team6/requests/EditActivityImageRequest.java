package com.springvuegradle.team6.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.sql.Blob;

public class EditActivityImageRequest {

    @JsonProperty("photo_filename")
    @Pattern(
            regexp = "([^\\s]+(\\.(?i)(jpg|jpeg|png|gif))$)",
            message = "Those image formats are not supported")
    @NotEmpty
    String photoFilename;

    public String getPhotoFilename() {
        return photoFilename;
    }
}
