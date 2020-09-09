package com.springvuegradle.team6.services.ExternalAPI;

import com.springvuegradle.team6.models.entities.Location;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GoogleAPIService {
    // API key is automatically set
    @Value("#{environment.GOOGLE_API_KEY}")
    protected String API_KEY;

    // URL API Constants
    protected static final String URL_REVERSE_GEOCODE = "https://maps.googleapis.com/maps/api/geocode/json?latlng={lat},{lng}&key={key}";

    private RestTemplate template;

    public GoogleAPIService(RestTemplate template) {
        this.template = template;
    }

    /**
     * Call Google Geocoding API for a reverse lookup of a location
     * @param location location to search for
     * @return JSONObject of data recieved from the api
     * @throws HttpServerErrorException when api is unavailable
     */
    public JSONObject reverseGeocode(Location location) {
        // Call API
        ResponseEntity<String> response = template.getForEntity(URL_REVERSE_GEOCODE, String.class,
                location.getLatitude(), location.getLongitude(), API_KEY);

        // Check response
        if (response.getStatusCode() != HttpStatus.OK) {
            Logger.getLogger("ExternalAPI").log(Level.SEVERE, "Google reverse geocode API error");
            Logger.getLogger("ExternalAPI").log(Level.SEVERE, response.getBody());
            throw new HttpServerErrorException(response.getStatusCode());
        }

        // Parse JSON
        try {
            return (JSONObject) new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(response.getBody());
        } catch (ParseException e) {
            Logger.getLogger("ExternalAPI").log(Level.SEVERE, "Could not parse Google reverse geocode API json");
            Logger.getLogger("ExternalAPI").log(Level.SEVERE, response.getBody());
            throw new HttpServerErrorException(HttpStatus.EXPECTATION_FAILED);
        }
    }

    /**
     * Parse location from google api geocode result
     * @param result Result json from geocode result
     * @return location data from json
     */
    private Location parseLocationFromGeocodeResult(JSONObject result) {
        JSONObject resultGeometry = (JSONObject) result.get("geometry");
        JSONObject resultLocation = (JSONObject) resultGeometry.get("location");

        Location location = new Location();
        location.setName(result.getAsString("formatted_address"));
        location.setLatitude(resultLocation.getAsNumber("lat").doubleValue());
        location.setLongitude(resultLocation.getAsNumber("lng").doubleValue());

        return location;
    }

    /**
     * Return rank priority order of place types.
     * This function can be modified in the future to allow more specific address or less specific
     * @param typeName google api type name
     * @return rank value
     */
    private int getPlaceTypeRank(String typeName) {
        if(typeName.equals("country")) return 0;
        if(typeName.equals("administrative_area_level_2")) return 1;
        if(typeName.equals("administrative_area_level_1")) return 2;
        return -1;
    }

    /**
     * Find highest priority obfuscated place result.
     * This is found in the order city, state, country else return null
     * @param responseJson responseJSON from geocode api request
     * @return highest priority result or null if none available
     */
    private JSONObject findHighestPriorityObfuscatedGeocodeResult(JSONObject responseJson) {
        JSONArray results = (JSONArray) responseJson.get("results");

        // Iterate over results to find highest ranked place type
        int foundResultRank = -1;
        JSONObject foundResult = null;
        for(int i = 0; i < results.size(); i++) {
            // Get results primary address type
            JSONObject result = (JSONObject) results.get(i);
            JSONArray addressComponents = (JSONArray) result.get("address_components");
            JSONObject primaryAddressComponent = (JSONObject) addressComponents.get(0);
            String primaryAddressComponentType = ((JSONArray) primaryAddressComponent.get("types")).get(0).toString();

            // Check if has higher priority to display
            int rank = getPlaceTypeRank(primaryAddressComponentType);
            if(rank > foundResultRank) {
                foundResultRank = rank;
                foundResult = result;
            }
        }

        return foundResult;
    }

    /**
     * Parse Reverse Geocode Google API response
     * @param responseJson json response from api call
     * @return Location of highest detail place
     */
    public Location parseReverseGeocodeDetailed(JSONObject responseJson) {
        JSONArray results = (JSONArray) responseJson.get("results");
        JSONObject primaryResult = (JSONObject) results.get(0);
        return parseLocationFromGeocodeResult(primaryResult);
    }

    /**
     * Parse Reverse Geocode Google API response
     * @param responseJson json response from api call
     * @return Location of minimal detail place. By priority available city,state,country otherwise null
     */
    public Location parseReverseGeocodeObfuscated(JSONObject responseJson) {
        JSONObject result = findHighestPriorityObfuscatedGeocodeResult(responseJson);

        // Return null if no possible lower detail location could be found
        if(result == null) {
            return null;
        }

        return parseLocationFromGeocodeResult(result);
    }
}
