package com.springvuegradle.team6.models.location;


public class Location {

    private String name = "";

    private double latitude = 0;
    private double longitude = 0;

    public Location() { }

    public Location(String name) {
        setName(name);
    }

    /**
     * Sets a location using a specific latitude and longitude
     * @param latitude Global coordinate latitude
     * @param longitude Global coordinate longitude
     */
    public Location(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
