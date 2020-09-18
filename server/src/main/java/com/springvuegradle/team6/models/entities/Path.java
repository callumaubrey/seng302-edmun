package com.springvuegradle.team6.models.entities;

import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Path entity class defines the route undertaken for the associated activity. It is created through
 * tracing the list of location coordinates. The two types of path are: Straight and Defined.
 */
@Entity
public class Path implements Serializable {

  @Id
  @GeneratedValue
  private int id;
  /**
   * Each path consists of at least two coordinates: the start and end coordinates. Start coordinate
   * has the smallest location id in the list and vice versa.
   */
  @OneToMany(cascade = CascadeType.REMOVE)
  @Size(min = 2)
  @NotNull
  private List<Location> locations;

  @NotNull
  private PathType type;

  public Path() {
  }

  /**
   * Constructor for entity Path class
   *
   * @param locations location coordinates to trace the path
   * @param type      path type
   */
  public Path(List<Location> locations, PathType type) {
    setLocations(locations);
    setType(type);
  }

  public int getId() {
    return id;
  }

  public List<Location> getLocations() {
    return locations;
  }

  public void setLocations(List<Location> locations) {
    this.locations = locations;
  }

  public PathType getType() {
    return type;
  }

  public void setType(PathType type) {
    this.type = type;
  }
}
