package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.repositories.ActivityRepository;
import com.springvuegradle.team6.models.repositories.ProfileRepository;
import com.springvuegradle.team6.models.repositories.TagRepository;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"ADMIN_EMAIL=test@test.com",
    "ADMIN_PASSWORD=test"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SearchActivityControllerTest {

  @Autowired private ProfileRepository profileRepository;

  @Autowired private ActivityRepository activityRepository;

  @Autowired private TagRepository tagRepository;

  @Autowired private MockMvc mvc;

  private int id;

  private MockHttpSession session;

  @BeforeAll
  void setup() {

  }

  @AfterAll
  void tearDown(@Autowired DataSource dataSource) throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(conn, new ClassPathResource("tearDown.sql"));
    }
  }
}
