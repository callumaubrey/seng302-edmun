package com.springvuegradle.team6.services;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LocationService {

  private final RestTemplate restTemplate;

  public LocationService(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  public String getLocationAddressFromLatLng(double lat, double lng, boolean hideDetails) {

    // Send API requuest
    String url = "https://photon.komoot.de/reverse?lon={lng}&lat={lat}";
    ResponseEntity<String> response = this.restTemplate.getForEntity(url, String.class, lng, lat);
    if (response.getStatusCode() != HttpStatus.OK) {
      return null;
    }

    // Parse JSON
    JSONObject responseJson = null;
    try {
      responseJson = (JSONObject) new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(response.getBody());
    } catch (ParseException e) {
      // Json is not in a valid format
      return null;
    }

    // Process JSON
    JSONArray features = (JSONArray)responseJson.get("features");
    if(features.isEmpty()) {
      return null;
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
    if(country != null) {
      address.append(country);
    }
    if(state != null) {
      address.append(", ");
      address.append(state);
    }
    if(city != null) {
      address.append(", ");
      address.append(city);
    }
    if(street != null && !hideDetails) {
      address.append(", ");
      if(housenumber != null) {
        address.append(housenumber);
        address.append(" ");
      }
      address.append(street);
    }

    return address.toString();
  }
}
