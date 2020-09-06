package com.springvuegradle.team6.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team6.models.entities.Location;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.repositories.LocationRepository;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * This service is used to communicate to the photon api
 */
@Service
public class LocationService {

  private final RestTemplate restTemplate;

  public LocationService(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  /**
   * Gets an address string from latitude and longitude using photon API
   *
   * @param lat double latitude
   * @param lng double longitude
   * @param hideDetails boolean to specify if should return full address or not
   * @return address string or null if errors are present
   */
  public String getLocationAddressFromLatLng(double lat, double lng, boolean hideDetails) {
    JSONObject response = getOSMFeatures(lat, lng);
    return getAddressFromOSMFeature(response, hideDetails);
  }

  /**
   * Get OSM Features at coordinates using photon api
   * @param lat latitude
   * @param lng longitude
   * @return Parsed json response from api
   */
  private JSONObject getOSMFeatures(double lat, double lng) {
    // Send API request
    String url = "https://photon.komoot.de/reverse?lon={lng}&lat={lat}";
    ResponseEntity<String> response = this.restTemplate.getForEntity(url, String.class, lng, lat);
    if (response.getStatusCode() != HttpStatus.OK) {
      return null;
    }

    // Parse JSON
    try {
      return  (JSONObject) new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(response.getBody());
    } catch (ParseException e) {
      // Json is not in a valid format
      return null;
    }
  }

  /**
   * Get first feature coordinates from query
   * @param query photon api search query
   * @param geoPriority location to give priority responses from
   * @return location of feature from query
   */
  private Location getOSMFeatureCoordinates(String query, Location geoPriority) {
    // Send API request
    String url = "https://photon.komoot.de/api?q={query}&lon={lng}&lat={lat}";
    ResponseEntity<String> response = this.restTemplate.getForEntity(url, String.class, query, geoPriority.getLatitude(), geoPriority.getLongitude());
    if (response.getStatusCode() != HttpStatus.OK) {
      return null;
    }

    // Parse JSON
    try {
      JSONObject responseJson = (JSONObject) new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(response.getBody());
      JSONArray features = (JSONArray) responseJson.get("features");
      JSONObject primaryFeature = (JSONObject) features.get(0);
      JSONObject geometry = (JSONObject) primaryFeature.get("geometry");
      JSONArray coordinates = (JSONArray) geometry.get("coordinates");
      double lng = Double.parseDouble(coordinates.get(0).toString());
      double lat = Double.parseDouble(coordinates.get(1).toString());

      return new Location(lat, lng);

    } catch (ParseException e) {
      // Json is not in a valid format
      return null;
    }
  }

  /**
   * Get Location address from OSM feature json response
   * @param responseJson json returned from osm api request
   * @param hideDetails boolean to specify if should return full address or not
   * @return display string of address
   */
  private String getAddressFromOSMFeature(JSONObject responseJson, boolean hideDetails) {
    // Process JSON
    JSONArray features = (JSONArray) responseJson.get("features");
    if (features.isEmpty()) {
      return "";
    }

    JSONObject primaryFeature = (JSONObject) features.get(0);
    JSONObject properties = (JSONObject) primaryFeature.get("properties");
    String country = properties.getAsString("country");
    String state = properties.getAsString("state");
    String street = properties.getAsString("street");
    String housenumber = properties.getAsString("housenumber");
    String city = properties.getAsString("city");

    // Build address string
    StringBuilder address = new StringBuilder();
    if (street != null && !hideDetails) {
      if (housenumber != null) {
        address.append(housenumber);
        address.append(" ");
      }
      address.append(street);
      address.append(", ");
    }
    if (city != null) {
      address.append(city);
      address.append(", ");
    }
    if (state != null) {
      address.append(state);
      address.append(", ");
    }
    if (country != null) {
      address.append(country);
    }

    return address.toString();
  }

  /**
   * Returns a higher feature location to hide a location.
   * For Example: If the location specified a house. The returned location would be of the city the location
   * came from.
   * @param featureJson features from api call of a location
   * @return An obfuscated location in proximity to the realLocation
   */
  private Location obfuscateLocation(JSONObject featureJson) {
    JSONArray features = (JSONArray) featureJson.get("features");
    JSONObject primaryFeature = (JSONObject) features.get(0);
    JSONObject properties = (JSONObject) primaryFeature.get("properties");

    // Get available higher feature
    String country = properties.getAsString("country");
    String state = properties.getAsString("state");
    String city = properties.getAsString("city");

    JSONObject geometry = (JSONObject) primaryFeature.get("geometry");
    JSONArray coordinates = (JSONArray) geometry.get("coordinates");
    double lat = Double.parseDouble(coordinates.get(0).toString());
    double lng = Double.parseDouble(coordinates.get(1).toString());

    String featureName = "";
    if(city != null) featureName += city + " ";
    if(state != null) featureName += state + " ";
    if(country != null) featureName += country + " ";
    if(featureName.length() == 0) return null;

    // Get Feature coordinates
    return getOSMFeatureCoordinates(featureName, new Location(lat, lng));
  }

  /**
   * Adds location to profile json. Option to make obfuscate the location for user security
   * @param profile Profile to set locations for
   * @param privateLocation private Location to set for profile
   */
  public void updateProfileLocation(Profile profile, Location privateLocation, LocationRepository locationRepository) {

    // Set Private and Public Location to null if null
    if (privateLocation == null) {
      profile.setPrivateLocation(null);
      profile.setPublicLocation(null);
      return;
    }

    // Get OSM feature at location coordinates
    JSONObject response = getOSMFeatures(privateLocation.getLatitude(), privateLocation.getLongitude());
    if(response == null) return;

    // Generate coordinates for public location
    Location publicLocation = obfuscateLocation(response);

    // Get address from feature
    String privateAddress = getAddressFromOSMFeature(response, false);
    privateLocation.setName(privateAddress);

    if(publicLocation != null) {
      String publicAddress = getAddressFromOSMFeature(response, true);
      publicLocation.setName(publicAddress);
    }

    // Set Locations
    if(publicLocation != null) locationRepository.save(publicLocation);
    locationRepository.save(privateLocation);
    profile.setPublicLocation(publicLocation);
    profile.setPrivateLocation(privateLocation);
  }
}
