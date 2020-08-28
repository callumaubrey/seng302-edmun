package com.springvuegradle.team6.services;

import org.springframework.boot.web.client.RestTemplateBuilder;
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
    return this.restTemplate.getForObject(url, String.class, lng, lat);
  }
}
