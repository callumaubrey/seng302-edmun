package com.springvuegradle.team6.requests;

import javax.validation.constraints.NotEmpty;
import java.sql.Blob;

public class EditActivityImageRequest {

    @NotEmpty
    String photoFilename;

    public String getPhotoFilename() {
        return photoFilename;
    }
}
