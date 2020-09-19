package com.springvuegradle.team6.services;

import com.springvuegradle.team6.models.entities.Location;
import com.springvuegradle.team6.models.entities.Profile;
import com.springvuegradle.team6.models.repositories.LocationRepository;
import com.springvuegradle.team6.services.ExternalAPI.GoogleAPIService;
import java.util.ArrayList;
import java.util.List;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

/** This service is used to communicate to the photon api */
@Service
public class LocationService {

  @Autowired private final GoogleAPIService apiService;

  public LocationService(GoogleAPIService service) {
    this.apiService = service;
  }

  /**
   * Get location address from lat and lng
   *
   * @param latitude location lat
   * @param longitude location lng
   * @param hideDetails hides specific address
   * @return String formatted address
   */
  public String getLocationAddressFromLatLng(
      Double latitude, Double longitude, boolean hideDetails) {
    JSONObject response = apiService.reverseGeocode(new Location(latitude, longitude));
    if (response == null) return "";

    String address;
    Location location;
    if (hideDetails) {
      location = apiService.parseReverseGeocodeObfuscated(response);
    } else {
      location = apiService.parseReverseGeocodeDetailed(response);
    }
    if (location != null) {
      address = location.getName();
    } else {
      address = null;
    }
    return address;
  }

  /**
   * Get a list of location addresses from location short name
   *
   * @param name location short name
   * @return a list of location json object
   */
  public List<JSONObject> getLocationAddressListFromName(String name) {
    JSONObject locationJSON = apiService.autocompleteLocationName(name);
    System.out.println(locationJSON);
    JSONArray locationArray = (JSONArray) locationJSON.get("predictions");

    ArrayList<JSONObject> results = new ArrayList<>();
    for (Object o : locationArray) {
      String locationName = ((JSONObject) o).getAsString("description");
      String locationPlaceId = ((JSONObject) o).getAsString("place_id");
      JSONObject result = new JSONObject();
      result.put("name", locationName);
      result.put("place_id", locationPlaceId);
      results.add(result);
    }
    System.out.println(results);
    return results;
  }

  /**
   * Adds location to profile json. Option to make obfuscate the location for user security
   *
   * @param profile Profile to set locations for
   * @param privateLocation private Location to set for profile
   * @param locationRepository location repository to save locations to
   */
  public void updateProfileLocation(
      Profile profile, Location privateLocation, LocationRepository locationRepository) {

    // Set Private and Public Location to null if null
    if (privateLocation == null) {
      profile.setPrivateLocation(null);
      profile.setPublicLocation(null);
      return;
    }

    // Get OSM feature at location coordinates
    JSONObject response = apiService.reverseGeocode(privateLocation);
    if (response == null) return;

    // Generate coordinates for locations
    Location publicLocation = apiService.parseReverseGeocodeObfuscated(response);
    privateLocation = apiService.parseReverseGeocodeDetailed(response);

    // Set Locations
    if (publicLocation != null) locationRepository.save(publicLocation);
    locationRepository.save(privateLocation);
    profile.setPublicLocation(publicLocation);
    profile.setPrivateLocation(privateLocation);
  }
}
