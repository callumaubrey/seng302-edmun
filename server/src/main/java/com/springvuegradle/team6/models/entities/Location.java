package com.springvuegradle.team6.models.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Latitude;
import org.hibernate.search.annotations.Longitude;
import org.hibernate.search.annotations.Spatial;
import org.hibernate.search.spatial.Coordinates;

/**
 * Location is a base class for defining a location in the world. It has three members: locationID:
 * The stored generated id for the database name: The associated name of the location lat & lon: The
 * geo coordinates of the feature This class can be used to define a feature point. Right now it is
 * used to define a users location but it can be extended to be used for instance for vertices in
 * event routes.
 */
@Indexed
@Spatial
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Location implements Serializable, Coordinates {

  @Id
  @GeneratedValue
  private final long location_id = 0;

  @Latitude
  private Double latitude = 0d;
  @Longitude
  private Double longitude = 0d;

  private String name;

  @ManyToOne
  @JoinColumn(name = "path_id")
  @JsonIgnore
  private Path path;

  public Location() {
  }

  /**
   * Sets a location using a specific latitude and longitude
   *
   * @param latitude  Global coordinate latitude
   * @param longitude Global coordinate longitude
   */
  public Location(double latitude, double longitude) {
    setLatitude(latitude);
    setLongitude(longitude);
  }

  public Double getLatitude() {
    return latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPath(Path path) {
    this.path = path;
  }
}
