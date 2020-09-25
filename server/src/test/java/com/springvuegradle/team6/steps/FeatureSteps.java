package com.springvuegradle.team6.steps;

import io.cucumber.java.After;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;

@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FeatureSteps {
  @Autowired DataSource dataSource;

  @After
  public void tearDown() throws SQLException {
    System.out.println("TEARDOWN");
    try (Connection conn = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(conn, new ClassPathResource("tearDown.sql"));
    }
  }
}
