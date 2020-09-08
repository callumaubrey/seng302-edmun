package com.springvuegradle.team6.services.ExternalAPI;

import com.springvuegradle.team6.models.entities.Location;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
class GoogleAPIServiceTest {

    private RestTemplate template = mock(RestTemplate.class);
    private GoogleAPIService googleAPIService = new GoogleAPIService(template);

    // Helper function
    final String RESOURCE_LOCATION_DIR = "ExternalAPI/GoogleAPIServiceTest/";
    String getResourceAsString(String filename) throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        File file = new File(classLoader.getResource(RESOURCE_LOCATION_DIR + filename).getFile());

        return Files.readString(file.toPath());
    }

    @Test
    void testReverseGeocodeReturnsOK() throws Exception {
        // Setup Mockito
        String googleAPIResponse = getResourceAsString("reverseGeocode_OK.json");
        Location location = new Location(40.714224, -73.961452);

        Mockito.when(template.getForEntity(GoogleAPIService.URL_REVERSE_GEOCODE, String.class, location.getLatitude(), location.getLongitude(), null))
	          .thenReturn(new ResponseEntity<String>(googleAPIResponse, HttpStatus.OK));

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
        String googleAPIResponse = getResourceAsString("reverseGeocode_OK.json");
        Location location = new Location(40.714224, -73.961452);

        Mockito.when(template.getForEntity(GoogleAPIService.URL_REVERSE_GEOCODE, String.class, location.getLatitude(), location.getLongitude(), null))
                .thenReturn(new ResponseEntity<String>(googleAPIResponse, HttpStatus.INTERNAL_SERVER_ERROR));

        // Call mock api
        assertThrows(HttpServerErrorException.class, () -> {
            googleAPIService.reverseGeocode(location);
        });
    }

    // This exception is very unlikely to occur unless the google codebase fails
    @Test
    void testReverseGeocodeReturnsParseException() throws Exception {
        // Setup Mockito
        String googleAPIResponse = "{{}";
        Location location = new Location(40.714224, -73.961452);

        Mockito.when(template.getForEntity(GoogleAPIService.URL_REVERSE_GEOCODE, String.class, location.getLatitude(), location.getLongitude(), null))
                .thenReturn(new ResponseEntity<String>(googleAPIResponse, HttpStatus.OK));

        // Call mock api
        HttpStatus status = assertThrows(HttpServerErrorException.class, () -> {
            googleAPIService.reverseGeocode(location);
        }).getStatusCode();

        assertEquals(HttpStatus.EXPECTATION_FAILED, status);
    }

    @Test
    void parseReverseGeocodeDetailed() throws IOException {
        // Setup Mockito
        String googleAPIResponse = getResourceAsString("reverseGeocode_OK.json");
        Location location = new Location(40.714224, -73.961452);

        Mockito.when(template.getForEntity(GoogleAPIService.URL_REVERSE_GEOCODE, String.class, location.getLatitude(), location.getLongitude(), null))
                .thenReturn(new ResponseEntity<String>(googleAPIResponse, HttpStatus.OK));

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
        String googleAPIResponse = getResourceAsString("reverseGeocode_OK.json");
        Location location = new Location(40.714224, -73.961452);

        Mockito.when(template.getForEntity(GoogleAPIService.URL_REVERSE_GEOCODE, String.class, location.getLatitude(), location.getLongitude(), null))
                .thenReturn(new ResponseEntity<String>(googleAPIResponse, HttpStatus.OK));

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
        String googleAPIResponse = getResourceAsString("reverseGeocodeState_OK.json");
        Location location = new Location(40.714224, -73.961452);

        Mockito.when(template.getForEntity(GoogleAPIService.URL_REVERSE_GEOCODE, String.class, location.getLatitude(), location.getLongitude(), null))
                .thenReturn(new ResponseEntity<String>(googleAPIResponse, HttpStatus.OK));

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
        String googleAPIResponse = getResourceAsString("reverseGeocodeCountry_OK.json");
        Location location = new Location(40.714224, -73.961452);

        Mockito.when(template.getForEntity(GoogleAPIService.URL_REVERSE_GEOCODE, String.class, location.getLatitude(), location.getLongitude(), null))
                .thenReturn(new ResponseEntity<String>(googleAPIResponse, HttpStatus.OK));

        // Call mock api
        JSONObject responseJson = googleAPIService.reverseGeocode(location);
        Location privateLocation = googleAPIService.parseReverseGeocodeObfuscated(responseJson);

        assertEquals(37.09024, privateLocation.getLatitude());
        assertEquals(-95.712891, privateLocation.getLongitude());
        assertEquals("United States", privateLocation.getName());
    }
}