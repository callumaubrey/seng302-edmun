package com.springvuegradle.team6.models.location;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OSMLocationTest {

    @Test
    void updateLocationData() {
        OSMElementID id = new OSMElementID();
        assertTrue(id.findBySearch("Christchurch"));

        OSMLocation location = new OSMLocation(id);
        location.updateLocationData();

        assertEquals("Christchurch, Christchurch City, Canterbury, New Zealand", location.getName());
    }
}