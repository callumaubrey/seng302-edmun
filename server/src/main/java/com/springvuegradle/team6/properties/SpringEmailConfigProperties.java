package com.springvuegradle.team6.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class SpringEmailConfigProperties {
  private String username;
  private String password;
  private String url;

  public String getUsername() {
    return username;
  }

  public void setUsername(String email) {
    this.username = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
