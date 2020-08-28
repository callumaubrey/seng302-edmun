package com.springvuegradle.team6.services;

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

  public String getLocationAddressFromLatLng(double lat, double lng) {
    String url = "https://photon.komoot.de/reverse?lon={lng}&lat={lat}";
    ResponseEntity<String> response = this.restTemplate.getForEntity(url, String.class, lng, lat);
    if (response.getStatusCode() == HttpStatus.OK) {
      return response.getBody();
    } else {
      return null;
    }
  }
}
