package com.springvuegradle.team6.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
public class FileService {

    @Value("${IMAGE.ACTIVITY.DIRECTORY}")
    public String activityDirectory;

    @Value("${IMAGE.PROFILE.DIRECTORY}")
    public String profileDirectory;


  /**
     * This function will take a file and save it in the directory given in the application properties.
     * It replaces any existing file with the same name.
     *
     * @param file the file to be saved
     * @return returns the filename if successful, null if failed.
     */
    public String uploadProfileImage(MultipartFile file, int profileId) {
        try {
            String fileName = "profile" + profileId + "." + file.getContentType().replaceFirst("image/", "");
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

    /**
     * This function will take a file for an activity and save it in the directory given in the application properties.
     * It replaces any existing file with the same name.
     *
     * @param file the file to be saved
     * @return returns filename if successful, null if failed.
     */
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

    /**
     * Gets a files mime type from the extension in its filename
     * @param fileName to get mime type from
     * @return string mime type or null if no mime type associated
     */
    public String getMIMEType(String fileName) {
      if(fileName == null) return null;

      try {
        return Files.probeContentType(new File(fileName).toPath());
      } catch (IOException e) {
        Logger.getLogger("FileService")
            .log(Level.WARNING, String.format("No MIME type associated with file '%s'", fileName));
        return null;
      }
    }


    /**
     * Get Image byte data from filename
     * @param fileName of image
     * @return byte data from file. Is empty if file cannot be found
     */
    public byte[] getActivityImage(String fileName) {
      File file = new File(activityDirectory + fileName);
      try {
        return Files.readAllBytes(file.toPath());
      } catch (IOException e) {
        Logger.getLogger("FileService")
            .log(Level.WARNING, String.format("File does not exist '%s'", fileName));
        return new byte[]{};
      }
    }

    /**
     * Removes activity image
     * @param fileName activity filename
     * @return true if success, false otherwise
     */
    public boolean removeActivityImage(String fileName) {
      File file = new File(activityDirectory + fileName);
      return file.delete();
    }
}
