package com.springvuegradle.team6.models;

import javax.persistence.*;
import java.util.Set;

@Entity
public class ActivityType {

    @Id @GeneratedValue
    private int id;

    private String activity;

    public ActivityType() {}

    public ActivityType(String activity) {
        this.activity = activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getActivity() {
        return this.activity;
    }
}
