package com.springvuegradle.seng302team600;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    //    private ObjectMapper objMapper = new ObjectMapper();
    private String createUserJsonPost;
    private String createUserJsonPostPassport;
    private String editProfileJsonPost;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        createUserJsonPost = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"primary_email\": \"jacky@google.com\",\n" +
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\"\n" +
                "}";

        createUserJsonPostPassport = "{\n" +
                "  \"lastname\": \"Pocket\",\n" +
                "  \"firstname\": \"Poly\",\n" +
                "  \"middlename\": \"Michelle\",\n" +
                "  \"nickname\": \"Pino\",\n" +
                "  \"primary_email\": \"poly@pocket.com\",\n" +
                "  \"password\": \"somepwd\",\n" +
                "  \"bio\": \"Poly Pocket is so tiny.\",\n" +
                "  \"date_of_birth\": \"2000-11-11\",\n" +
                "  \"gender\": \"female\",\n" +
                "  \"fitness\": 3,\n" +
                "  \"passports\": [\"Australia\", \"Antarctica\"]\n" +
                "}";

        editProfileJsonPost = "{\n" +
                "  \"id\": \"1\",\n" +
                "  \"bio\": \"About ME\",\n" +
                "  \"firstname\": \"Ben\"\n" +
                "}";

        objectMapper = new ObjectMapper();
    }

    @Test
    /**Test if a new User can be created*/
    public void newUserTestU1() throws Exception {

        // Setup POST
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/profiles")
                .content(createUserJsonPost)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // Perform POST
        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isOk())
                .andReturn();

        // Get Response as JsonNode
        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);

        // Test response
        assertEquals("Benson", jsonNode.get("lastname").asText());
        assertEquals("jacky@google.com", jsonNode.get("primary_email").get("primaryEmail").asText());
    }

    @Test
    /**Test if a new User can be created*/
    public void newUserTestU2() throws Exception {

        // Setup POST
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/profiles")
                .content(createUserJsonPostPassport)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // Perform POST
        MvcResult result = mvc.perform(httpReq)
                .andExpect(status().isOk())
                .andReturn();

        // Get Response as JsonNode
        String jsonResponseStr = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);

        // Test response
        assertEquals("Pocket", jsonNode.get("lastname").asText());
        assertEquals("poly@pocket.com", jsonNode.get("primary_email").get("primaryEmail").asText());
        assertEquals("Antarctica", jsonNode.get("passports").get(1).asText());
    }



    @Test
    public void allTest() throws Exception {
        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.get("/listprofile")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(httpReq)
                .andExpect(status().isOk());

    }

//    @Test
//    /**Test if a new User can be created*/
//    public void editProfileTest() throws Exception {
//
//        // Setup POST
//        MockHttpServletRequestBuilder httpReq = MockMvcRequestBuilders.post("/editprofile")
//                .content(editProfileJsonPost)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON);
//
//        // Perform POST
//        MvcResult result = mvc.perform(httpReq)
//                .andExpect(status().isOk())
//                .andReturn();
//
//        // Get Response as JsonNode
//        String jsonResponseStr = result.getResponse().getContentAsString();
//        JsonNode jsonNode = objectMapper.readTree(jsonResponseStr);
//
//        // Test response
//        assertEquals("About ME", jsonNode.get("bio").asText());
//        assertEquals("Ben", jsonNode.get("firstname").asText());
//    }
}