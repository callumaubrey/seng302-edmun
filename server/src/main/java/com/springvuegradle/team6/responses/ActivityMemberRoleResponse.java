package com.springvuegradle.team6.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;

import java.util.List;

public class ActivityMemberRoleResponse {

    public ActivityMemberRoleResponse() {

    }

    @JsonProperty("Participant")
    public List<JSONObject> participants;

    @JsonProperty("Organiser")
    public List<JSONObject> organisers;

    @JsonProperty("Follower")
    public List<JSONObject> followers;

    @JsonProperty("Creator")
    public List<JSONObject> creators;

    @JsonProperty("Access")
    public List<JSONObject> accessors;

//    public void setFields(List<String> paricipant, List<String> organisers, List<String> creators, List<String> followers, List<String> accessors) {
    public void setFields(List<JSONObject> paricipant, List<JSONObject> organisers, List<JSONObject> creators, List<JSONObject> followers, List<JSONObject> accessors) {
        this.participants = paricipant;
        this.organisers = organisers;
        this.creators = creators;
        this.followers = followers;
        this.accessors = accessors;
    }
}
