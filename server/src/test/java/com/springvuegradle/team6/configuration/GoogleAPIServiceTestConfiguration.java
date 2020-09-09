package com.springvuegradle.team6.configuration;

import com.springvuegradle.team6.services.ExternalAPI.GoogleAPIService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;


/**
 * Changes default bean for GoogleAPIService when the spring profile is 'test'.
 * The changed bean is a GoogleAPIService with a mocked restTemplate. This allows
 * for changes of the api returned data through all tests.
 */
@Profile("test")
@Configuration
public class GoogleAPIServiceTestConfiguration {
    @Bean
    @Primary
    public GoogleAPIService googleAPIService() {
      return new GoogleAPIService(Mockito.mock(RestTemplate.class));
    }
}
