package com.springvuegradle.team6.models;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private Collection<Profile> users;

    public Role() {
        super();
    }

    public Role(final String name) {
        super();
        this.name = name;
    }

    public String getRoleName() {
        return name;
    }

}
