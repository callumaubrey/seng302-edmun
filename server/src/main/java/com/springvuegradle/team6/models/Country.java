package com.springvuegradle.team6.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Country {
    public static final int ISO_LENGTH = 3;

    @Id
    @Column(length = 3)
    private String isoCode;

    private String countryName;

    public Country() {}

    public Country(String isoCode, String name) {
        this.isoCode = isoCode;
        this.countryName = name;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Override
    public String toString() {
        return isoCode;
    }
}
