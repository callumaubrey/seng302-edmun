package com.springvuegradle.team6.models;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;


import java.util.*;

@SpringBootTest
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com", "ADMIN_PASSWORD=test"})
public class SubscriptionHistoryRepositoryTest {

    @Autowired private SubscriptionHistoryRepository subscriptionHistoryRepository;
    @Autowired private ActivityRepository activityRepository;
    @Autowired private ProfileRepository profileRepository;

    @BeforeEach
    void setup() {
        subscriptionHistoryRepository.deleteAll();
    }

}
