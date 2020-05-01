package com.springvuegradle.team6.controllers;

import com.springvuegradle.team6.models.ProfileRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SearchProfileControllerTest {

  @Autowired private ProfileRepository profileRepository;

  @Autowired private MockMvc mvc;

  private int id;

  private MockHttpSession session;

  @BeforeEach
  void setup() throws Exception {
    session = new MockHttpSession();
    String profile1 =
        "{\n"
            + "  \"lastname\": \"Pocket\",\n"
            + "  \"firstname\": \"Poly\",\n"
            + "  \"middlename\": \"Michelle\",\n"
            + "  \"nickname\": \"Pino\",\n"
            + "  \"primary_email\": \"poly@pocket.com\",\n"
            + "  \"password\": \"Password1\",\n"
            + "  \"bio\": \"Poly Pocket is so tiny.\",\n"
            + "  \"date_of_birth\": \"2000-11-11\",\n"
            + "  \"gender\": \"female\"\n"
            + "}";
    String profile2 =
        "{\n"
            + "  \"lastname\": \"Benson\",\n"
            + "  \"firstname\": \"Maurice\",\n"
            + "  \"middlename\": \"Jack\",\n"
            + "  \"nickname\": \"Jacky\",\n"
            + "  \"primary_email\": \"jacky@google.com\",\n"
            + "  \"password\": \"jacky'sSecuredPwd1\",\n"
            + "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n"
            + "  \"date_of_birth\": \"1985-12-20\",\n"
            + "  \"gender\": \"male\"\n"
            + "}\n";
    String profile3 =
        "{\n"
            + "  \"lastname\": \"Benson\",\n"
            + "  \"firstname\": \"Maurice\",\n"
            + "  \"middlename\": \"Jack\",\n"
            + "  \"nickname\": \"JackyCopy\",\n"
            + "  \"primary_email\": \"jackyCopy@google.com\",\n"
            + "  \"password\": \"jacky'sSecuredPwd1\",\n"
            + "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n"
            + "  \"date_of_birth\": \"1985-12-20\",\n"
            + "  \"gender\": \"male\"\n"
            + "}\n";

    String profile4 =
        "{\n"
            + "  \"lastname\": \"Maurice\",\n"
            + "  \"firstname\": \"Benson\",\n"
            + "  \"middlename\": \"Jack\",\n"
            + "  \"nickname\": \"Jacky\",\n"
            + "  \"primary_email\": \"jacfky@ssgoogslse.com\",\n"
            + "  \"password\": \"jacky'sSecuredPwd9\",\n"
            + "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n"
            + "  \"date_of_birth\": \"1985-12-20\",\n"
            + "  \"gender\": \"male\"\n"
            + "}";

    String profile5 =
        "{\n"
            + "  \"lastname\": \"Pocket\",\n"
            + "  \"firstname\": \"Poly\",\n"
            + "  \"middlename\": \"Michelle Christopher\",\n"
            + "  \"nickname\": \"Pino\",\n"
            + "  \"primary_email\": \"polyChris@pocket.com\",\n"
            + "  \"password\": \"Password1\",\n"
            + "  \"bio\": \"Poly Pocket is so tiny.\",\n"
            + "  \"date_of_birth\": \"2000-11-11\",\n"
            + "  \"gender\": \"female\"\n"
            + "}";

    String profile6 =
        "{\n"
            + "  \"lastname\": \"Duan\",\n"
            + "  \"firstname\": \"Yu\",\n"
            + "  \"nickname\": \"Duany20\",\n"
            + "  \"primary_email\": \"duanyu@working.com\",\n"
            + "  \"password\": \"Password1\",\n"
            + "  \"bio\": \"Covid sucks\",\n"
            + "  \"date_of_birth\": \"1980-11-11\",\n"
            + "  \"gender\": \"male\"\n"
            + "}";

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(profile1)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(profile2)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(profile3)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(profile4)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(profile5)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    mvc.perform(
            MockMvcRequestBuilders.post("/profiles")
                .content(profile6)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isCreated())
        .andDo(print());

    String loginInfo =
        "{\n" + "\t\"email\" : \"poly@pocket.com\",\n" + "\t\"password\": \"Password1\"\n" + "}";

    mvc.perform(
            MockMvcRequestBuilders.post("/login")
                .content(loginInfo)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
        .andExpect(status().isOk());

    String body =
        mvc.perform(get("/profiles/id").session(session))
            .andReturn()
            .getResponse()
            .getContentAsString();
    id = Integer.parseInt(body);
  }

  @Test
  void searchProfileByFullnameReturnOneResult() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/profiles?fullname=Yu%20Duan", id).session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(arr.length(), 1);
  }

  @Test
  void searchProfileByFullnameReturnMultipleResults() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get("/profiles?fullname=Maurice%20Jack%20Benson", id)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    System.out.println(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());
  }

  @Test
  void searchProfileByFullnameWithSpaceInMiddleNameReturnCorrectFirstResult() throws Exception {
    String response =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        "/profiles?fullname=Poly%20Michelle%20Christopher%20Pocket", id)
                    .session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    System.out.println(response);
    JSONArray arr = obj.getJSONArray("results");
    String middlename = arr.getJSONObject(0).getString("middlename");
    org.junit.jupiter.api.Assertions.assertEquals("Michelle Christopher", middlename);
    org.junit.jupiter.api.Assertions.assertEquals(2, arr.length());
  }

  @Test
  void searchProfileByFullnameReturnNoResults() throws Exception {
    String response =
        mvc.perform(MockMvcRequestBuilders.get("/profiles?fullname=a", id).session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JSONObject obj = new JSONObject(response);
    System.out.println(response);
    JSONArray arr = obj.getJSONArray("results");
    org.junit.jupiter.api.Assertions.assertEquals(0, arr.length());
  }
}
