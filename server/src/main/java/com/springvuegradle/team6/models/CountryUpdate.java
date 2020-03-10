package com.springvuegradle.team6.models;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableScheduling
public class CountryUpdate {
//    @Scheduled(cron = "0/1 * * ? * ?")
//    public void cronExpressionTesting() {
//        System.out.println("print every second");
//    }
//
//    @Scheduled(cron = "0/2 * * ? * ?")
//    public void cronExpressionTestingTwo() {
//        System.out.println("print every 2 seconds");
//    }
//
//    @Scheduled(cron = "0 58 14 ? * ?")
//    public void cronExpressionTestingThree() {
//        System.out.println("print at 2:58pm everyday");
//    }
//
//    @Scheduled(cron = "0 0 0 ? * ?")
//    public void cronExpressionTestingFour() {
//        System.out.println("print at midnight everyday");
//    }
}