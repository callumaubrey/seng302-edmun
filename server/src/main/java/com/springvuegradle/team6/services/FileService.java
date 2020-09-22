package com.springvuegradle.team6.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
     *
     * @param file the file to be saved
     * @return returns null if successful, a response entity if failed.
     */
    public String uploadProfileImage(MultipartFile file, int profileId) {
        try {
            String fileName = "profile" + profileId + "." + file.getContentType().replaceFirst("image/", "");
            System.out.println(profileDirectory + fileName);
            File newFile = new File(profileDirectory + fileName);
            newFile.createNewFile();
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(newFile));
            stream.write(file.getBytes());
            stream.close();
            return fileName;
        } catch (IOException ex) {
            return null;
        }
    }

    public String uploadActivityImage(MultipartFile file, int activityId) {
        try {
            String fileName = "activity" + activityId + "." + file.getContentType().replaceFirst("image/", "");
            File newFile = new File(activityDirectory + fileName);
            newFile.createNewFile();
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(newFile));
            stream.write(file.getBytes());
            stream.close();
            return fileName;
        } catch (IOException ex) {
            return null;
        }
    }








}
