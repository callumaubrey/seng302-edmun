package com.springvuegradle.team6.models;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Entity
public class Tag {

  @Id
  @GeneratedValue
  @Column(name = "id")
  private Integer id;

  @Column(name = "name")
  private String name;

  @ManyToMany(mappedBy = "tags")
  private Collection<Activity> activities;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
