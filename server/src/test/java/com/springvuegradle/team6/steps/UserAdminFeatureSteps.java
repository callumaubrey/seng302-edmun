package com.springvuegradle.team6.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@WebAppConfiguration
public class UserAdminFeatureSteps {
    private static String dummyId;
    private static MockHttpSession session;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;
    private ResultActions mvcResponse;

    @Before
    public void setupUserAdmin() {
        //TODO find a solution to apply spring security mock behaviour OR addRole via addRole endpoint
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        List<SimpleGrantedAuthority> authorities = new ArrayList();
        SimpleGrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_USER");
        authorities.add(userAuthority);
        SimpleGrantedAuthority userAdminAuthority = new SimpleGrantedAuthority("ROLE_USER_ADMIN");
        authorities.add(userAdminAuthority);
        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        authorities.add(adminAuthority);


        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user_admin",
                        "N/A",
                        authorities));
    }


    @When("I register a dummy user")
    public void i_register_a_dummy_user() throws Exception {
        String jsonString = "{\n"
                + "  \"lastname\": \"Houston\",\n"
                + "  \"firstname\": \"Jacky\",\n"
                + "  \"middlename\": \"Hello\",\n"
                + "  \"nickname\": \"Pizza\",\n"
                + "  \"primary_email\": \"pizza@dominoes.com\",\n"
                + "\"additional_email\": [\n"
                + "    \"velvety@burger.com\",\n"
                + "    \"burger@fuuel.com\",\n"
                + "    \"macccass@kfc.com\"\n"
                + "  ],\n"
                + "  \"password\": \"Houston123\",\n"
                + "  \"bio\": \"This is a bio\",\n"
                + "  \"date_of_birth\": \"1999-08-08\",\n"
                + "  \"gender\": \"male\"\n"
                + "}";
        mvcResponse = mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Then("dummy user is created and I receive a {int} status code.")
    public void dummy_user_is_created_and_I_receive_a_status_code(int status) throws Exception {
        dummyId = mvcResponse.andExpect(status().is(status))
                .andReturn().getResponse().getContentAsString();
    }

    @Given("I create a user admin")
    public void i_create_a_user_admin() throws Exception {
        session = new MockHttpSession();
        String jsonString = "{\n"
                + "  \"lastname\": \"Dallas\",\n"
                + "  \"firstname\": \"Joe\",\n"
                + "  \"middlename\": \"Exotic\",\n"
                + "  \"nickname\": \"Tiger\",\n"
                + "  \"primary_email\": \"poly@pocket.com\",\n"
                + "\"additional_email\": [\n"
                + "    \"velvety123@burger.com\",\n"
                + "    \"burger123@fuuel.com\",\n"
                + "    \"macccass123@kfc.com\"\n"
                + "  ],\n"
                + "  \"password\": \"Password1\",\n"
                + "  \"bio\": \"This is a tiger king bio\",\n"
                + "  \"date_of_birth\": \"1979-08-08\",\n"
                + "  \"gender\": \"male\"\n"
                + "}";

        mvc.perform(MockMvcRequestBuilders
                .post("/profiles")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isCreated());
    }

    @When("I log in as user admin")
    public void i_log_in_as_user_admin() throws Exception {
        String loginInfo = "{\n" +
                "\t\"email\" : \"poly@pocket.com\",\n" +
                "\t\"password\": \"Password1\"\n" +
                "}";
        mvcResponse = mvc.perform(MockMvcRequestBuilders
                .post("/login")
                .content(loginInfo)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        );
    }

    @Then("I am logged in as an admin user and I receive {int} status code")
    public void i_am_logged_in_as_an_admin_user_and_I_receive_status_code(int statusCode) throws Exception {
        mvcResponse.andExpect(status().is(statusCode));
    }

    @When("I edit the primary email of the dummy user to {string}")
    @WithMockUser(roles = {"ROLE_USER_ADMIN"})
    public void i_edit_the_primary_email_of_the_dummy_user_to(String email) throws Exception {
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        String updateEmailsUrl = "/profiles/" + dummyId + "/emails";
        String jsonString = "{\n" +
                "  \"primary_email\": \"" + email + "\",\n" +
                "  \"additional_email\": [\n" +
                "  ]\n" +
                "}\n";
        mvc.perform(MockMvcRequestBuilders
                .put(updateEmailsUrl)
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
        ).andExpect(status().isOk());
    }

    @Then("primary email of the dummy user is {string}")
    public void primary_email_of_the_dummy_user_is(String email) throws Exception {
        String profileData = mvc.perform(MockMvcRequestBuilders
                .get("/profiles/{id}", dummyId)
                .session(session)
        ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertTrue(profileData.contains(email));
    }


}
