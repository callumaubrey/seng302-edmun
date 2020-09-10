package com.springvuegradle.team6.services.ExternalAPI;

import com.springvuegradle.team6.models.entities.Location;
import com.springvuegradle.team6.services.ExternalAPI.GoogleAPIService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.constraints.Null;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Testing service that generates mocked responses for the GoogleAPIService.
 * This allows tests to set what the google api should return.
 */
@Profile("test")
@Service
public class GoogleAPIServiceMocking {

  @Autowired
  private GoogleAPIService service;

  /**
   * Add autowired mocked google api service
   * @param service Mocked Google api service
   */
  public GoogleAPIServiceMocking(GoogleAPIService service) {
    this.service = service;
  }

  /**
   * Loads resource file data into memory as a string
   * @param filename resource filename in src
   * @return file data as string
   * @throws IOException if file does not exist
   */
  private String getResourceAsString(String filename) throws IOException {
    ClassLoader classLoader = this.getClass().getClassLoader();
    File file = new File(classLoader.getResource(filename).getFile());

    return Files.readString(file.toPath());
  }

  /**
   * Sets api mocking to return data contained in resource json file.
   * @param testJsonFilename json file stored in src/resources
   *                         For example if you had a file "src/resources/controllers/test.json"
   *                         then testJsonFilename='controllers/test.json'
   * @throws IOException if resource file does not exist
   */
  public void mockReverseGeocode(String testJsonFilename) throws IOException {
    String expectedJson = getResourceAsString(testJsonFilename);
    Mockito.when(service.template.getForEntity(
        eq(GoogleAPIService.URL_REVERSE_GEOCODE), eq(String.class),
        anyDouble(), anyDouble(), eq(null)))
        .thenReturn(new ResponseEntity<>(expectedJson, HttpStatus.OK));
  }

  /**
   * Sets api mocking to return data contained in resource json file.
   * @param testJsonFilename json file stored in src/resources
   *                         For example if you had a file "src/resources/controllers/test.json"
   *                         then testJsonFilename='controllers/test.json'
   * @param status Status to return from api
   * @throws IOException if resource file does not exist
   */
  public void mockReverseGeocode(String testJsonFilename, HttpStatus status) throws IOException {
    String expectedJson = getResourceAsString(testJsonFilename);
    Mockito.when(service.template.getForEntity(
        eq(GoogleAPIService.URL_REVERSE_GEOCODE), eq(String.class),
        anyDouble(), anyDouble(), eq(null)))
        .thenReturn(new ResponseEntity<>(expectedJson, status));
  }
}
