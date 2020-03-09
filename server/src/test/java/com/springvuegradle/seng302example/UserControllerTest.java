package com.springvuegradle.seng302example;

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
    private String editProfileJsonPost;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        createUserJsonPost = "{\n" +
                "  \"lastname\": \"Benson\",\n" +
                "  \"firstname\": \"Maurice\",\n" +
                "  \"middlename\": \"Jack\",\n" +
                "  \"nickname\": \"Jacky\",\n" +
                "  \"email\": \"jacky@google.com\",\n" +
                "  \"password\": \"jacky'sSecuredPwd\",\n" +
                "  \"bio\": \"Jacky loves to ride his bike on crazy mountains.\",\n" +
                "  \"date_of_birth\": \"1985-12-20\",\n" +
                "  \"gender\": \"male\"\n" +
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
    public void newUserTest() throws Exception {

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
        assertEquals("jacky@google.com", jsonNode.get("email").get("primaryEmail").asText());
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