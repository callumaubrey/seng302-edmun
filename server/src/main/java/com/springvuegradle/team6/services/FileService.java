package com.springvuegradle.team6.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {

    @Value("${IMAGE.ACTIVITY.DIRECTORY}")
    public String activityDirectory;

    @Value("${IMAGE.PROFILE.DIRECTORY}")
    public String profileDirectory;


    /**
     * This function will take a file and save it in the directory given in the applciation properties.
     * It replaces any existing file with the same name.
     * @param file the file to be saved
     * @return returns null if successful, a response entity if failed.
     */
    public ResponseEntity<String> uploadProfileImage(MultipartFile file, int profileId) {
        try {
            Path copyLocation = createPath(profileDirectory,"profile",profileId, file.getContentType());
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
            return null;
        } catch (Exception e) {
            return new ResponseEntity<>("Could not save image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> uploadActivityImage(MultipartFile file, int activityId) {
        try {
            Path copyLocation = createPath(activityDirectory,"activity",activityId, file.getContentType());
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
            return null;
        } catch (Exception e) {
            return new ResponseEntity<>("Could not save image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Path createPath(String directory, String entityType, int id, String fileType) {
        return Paths.get(directory + File.separator + StringUtils.cleanPath(entityType + id + fileType));
    }






}
