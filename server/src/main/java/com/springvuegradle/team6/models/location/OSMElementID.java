package com.springvuegradle.team6.models.location;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.client.RestTemplate;

/**
 * OpenStreetMap Element Identifier,
 * Elements can be uniquely identified using a 64 bit ID and the elements type.
 */
public class OSMElementID {
    private Long id;
    private OSMElementType type;

    public OSMElementID() {}

    public OSMElementID(Long id, OSMElementType type) {
        this.id = id;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OSMElementType getType() {
        return type;
    }

    public void setType(OSMElementType type) {
        this.type = type;
    }


    public String getUniqueCode() {
        return String.format("%c%d", type.code, id);
    }

    @Override
    public String toString() {
        return getUniqueCode();
    }

    public boolean findBySearch(String query) {
        String url = "https://nominatim.openstreetmap.org/search?q=\"%s\"&format=json&limit=1";
        url = String.format(url, query);

        // Query OSM
        RestTemplate restTemplate = new RestTemplate();
        JsonNode result = restTemplate.getForObject(url, JsonNode.class);


        if (result != null && !result.isEmpty()) {
            JsonNode osmObject = result.get(0);

            long jsonId = osmObject.get("osm_id").asLong(0);
            OSMElementType jsonType = OSMElementType.getFromStr(osmObject.get("osm_type").asText(""));

            // Check that both elements are found in the json
            if (jsonId != 0 && jsonType != null) {
                setId(jsonId);
                setType(jsonType);
            } else {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }
}
