package com.springvuegradle.team6.models.location;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.client.RestTemplate;

/**
 * Open Street Map Location,
 * This class extends the Location class. It has added functionality of
 * updating the display name and location using a known OSMElementID.
 * It does this via requests to the Nomination OSM API. For more
 * information on this API you can look here: https://nominatim.org/release-docs/latest/api/Reverse/
 */
public class OSMLocation extends Location {
    // OpenStreetMap Unique ID
    private OSMElementID osmID = null;

    public OSMLocation(OSMElementID id) {
        setOsmID(id);
    }

    public OSMElementID getOsmID() {
        return osmID;
    }

    public void setOsmID(OSMElementID osmID) {
        this.osmID = osmID;
    }

    /**
     * Makes API call to Open Street Map to check if the display name or coordinate of a location has changed.
     * Requires that osmID is set
     */
    public void updateLocationData() {
        if(osmID != null) {
            // Setup URL
            String url = "https://nominatim.openstreetmap.org/lookup?osm_ids=%s&format=json";
            url = String.format(url, osmID.getUniqueCode());

            // Query OSM
            RestTemplate restTemplate = new RestTemplate();
            JsonNode result = restTemplate.getForObject(url, JsonNode.class);
            if (result != null && !result.isEmpty()) {
                JsonNode osmObject = result.get(0);
                this.setLatitude(Double.parseDouble(osmObject.get("lat").asText("0")));
                this.setLongitude(Double.parseDouble(osmObject.get("lon").asText("0")));
                this.setName(osmObject.get("display_name").asText("N/A"));
            }
        }
    }
}
