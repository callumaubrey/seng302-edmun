package com.springvuegradle.team6.services.ExternalAPI;

import com.springvuegradle.team6.models.entities.Location;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
class GoogleAPIServiceTest {

  @Autowired
  private GoogleAPIService googleAPIService;

  @Autowired
  private GoogleAPIServiceMocking googleAPIServiceMocking;


  @Test
  void testReverseGeocodeReturnsOK() throws Exception {
    // Setup Mockito
    googleAPIServiceMocking.mockReverseGeocode("GoogleAPIServiceTest/reverseGeocode_OK.json",
        HttpStatus.OK);
    Location location = new Location(40.714224, -73.961452);

    // Call mock api
    JSONObject responseJson = googleAPIService.reverseGeocode(location);

    // Check parsed json is correct
    JSONArray results = (JSONArray) responseJson.get("results");
    JSONObject primaryResult = (JSONObject) results.get(0);
    String primaryAddress =  primaryResult.getAsString("formatted_address");

    assertEquals("277 Bedford Ave, Brooklyn, NY 11211, USA", primaryAddress);
  }

  @Test
  void testReverseGeocodeReturnsExceptionOnAPIError() throws Exception {
    // Setup Mockito
    googleAPIServiceMocking.mockReverseGeocode("GoogleAPIServiceTest/reverseGeocode_OK.json",
        HttpStatus.INTERNAL_SERVER_ERROR);
    Location location = new Location(40.714224, -73.961452);

    // Call mock api
    assertThrows(HttpServerErrorException.class, () -> googleAPIService.reverseGeocode(location));
  }

  // This exception is very unlikely to occur unless the google codebase fails
  @Test
  void testReverseGeocodeReturnsParseException() throws Exception {
    // Setup Mockito
    googleAPIServiceMocking.mockReverseGeocode("GoogleAPIServiceTest/reverseGeocode_Err.json", HttpStatus.OK);
    Location location = new Location(40.714224, -73.961452);

    // Call mock api
    HttpStatus status = assertThrows(HttpServerErrorException.class, () -> googleAPIService.reverseGeocode(location)).getStatusCode();

    assertEquals(HttpStatus.EXPECTATION_FAILED, status);
  }

  @Test
  void parseReverseGeocodeDetailed() throws IOException {
    // Setup Mockito
    googleAPIServiceMocking.mockReverseGeocode("GoogleAPIServiceTest/reverseGeocode_OK.json", HttpStatus.OK);
    Location location = new Location(40.714224, -73.961452);

    // Call mock api
    JSONObject responseJson = googleAPIService.reverseGeocode(location);
    Location publicLocation = googleAPIService.parseReverseGeocodeDetailed(responseJson);

    assertEquals(40.7142205, publicLocation.getLatitude());
    assertEquals(-73.9612903, publicLocation.getLongitude());
    assertEquals("277 Bedford Ave, Brooklyn, NY 11211, USA", publicLocation.getName());
  }

  @Test
  void parseReverseGeocodeObfuscatedCityAvailable() throws IOException {
    // Setup Mockito
    googleAPIServiceMocking.mockReverseGeocode("GoogleAPIServiceTest/reverseGeocode_OK.json", HttpStatus.OK);
    Location location = new Location(40.714224, -73.961452);

    // Call mock api
    JSONObject responseJson = googleAPIService.reverseGeocode(location);
    Location privateLocation = googleAPIService.parseReverseGeocodeObfuscated(responseJson);

    assertEquals(43.2994285, privateLocation.getLatitude());
    assertEquals(-74.21793260000001, privateLocation.getLongitude());
    assertEquals("New York, USA", privateLocation.getName());
  }

  @Test
  void parseReverseGeocodeObfuscatedStateAvailable() throws IOException {
    // Setup Mockito
    googleAPIServiceMocking.mockReverseGeocode("GoogleAPIServiceTest/reverseGeocodeState_OK.json", HttpStatus.OK);
    Location location = new Location(40.714224, -73.961452);

    // Call mock api
    JSONObject responseJson = googleAPIService.reverseGeocode(location);
    Location privateLocation = googleAPIService.parseReverseGeocodeObfuscated(responseJson);

    assertEquals(40.6528762, privateLocation.getLatitude());
    assertEquals(-73.95949399999999, privateLocation.getLongitude());
    assertEquals("Kings County, Brooklyn, NY, USA", privateLocation.getName());
  }

  @Test
  void parseReverseGeocodeObfuscatedCountryAvailable() throws IOException {
    // Setup Mockito
    googleAPIServiceMocking.mockReverseGeocode("GoogleAPIServiceTest/reverseGeocodeCountry_OK.json", HttpStatus.OK);
    Location location = new Location(40.714224, -73.961452);

    // Call mock api
    JSONObject responseJson = googleAPIService.reverseGeocode(location);
    Location privateLocation = googleAPIService.parseReverseGeocodeObfuscated(responseJson);

    assertEquals(37.09024, privateLocation.getLatitude());
    assertEquals(-95.712891, privateLocation.getLongitude());
    assertEquals("United States", privateLocation.getName());
  }
}