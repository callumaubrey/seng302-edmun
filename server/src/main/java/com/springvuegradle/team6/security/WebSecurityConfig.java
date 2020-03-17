package com.springvuegradle.team6.security;

import com.springvuegradle.team6.models.ProfileRepository;
import com.springvuegradle.team6.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@EnableJpaRepositories(basePackageClasses = ProfileRepository.class)
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private ProfileRepository profileRepository;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService());
    }

    @Bean
    public UserDetailsService customUserDetailsService() {
        CustomUserDetailsService customUser = new CustomUserDetailsService(profileRepository);
        return customUser;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.userDetailsService(customUserDetailsService());
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/account/**", "/profile/**").permitAll()
                .antMatchers("admin/**").authenticated()
                .anyRequest().authenticated();
    }

}
