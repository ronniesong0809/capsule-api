package com.ronniesong.capsuleapi.controllers;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(properties = "capsule.share.store=memory")
@AutoConfigureMockMvc
class ShareControllerTests {
  @Autowired
  private MockMvc mockMvc;

  @Test
  void createsAndFetchesShare() throws Exception {
    MvcResult result = mockMvc.perform(post("/share")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"kind\":\"deck\",\"data\":{\"cards\":[{\"id\":\"first\",\"title\":\"First card\"}]}}"))
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"))
        .andExpect(jsonPath("$.code", matchesPattern("[A-HJ-NP-Z2-9]{8}")))
        .andExpect(jsonPath("$.expiresIn").value(600))
        .andReturn();

    String code = result.getResponse().getContentAsString().replaceAll(".*\"code\":\"([A-HJ-NP-Z2-9]{8})\".*", "$1");

    mockMvc.perform(get("/share/" + code))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"))
        .andExpect(jsonPath("$.kind").value("deck"))
        .andExpect(jsonPath("$.data.cards[0].id").value("first"));
  }

  @Test
  void supportsExportsAlias() throws Exception {
    MvcResult result = mockMvc.perform(post("/exports")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"kind\":\"config\",\"data\":{\"theme\":\"light\"}}"))
        .andExpect(status().isCreated())
        .andReturn();

    String code = result.getResponse().getContentAsString().replaceAll(".*\"code\":\"([A-HJ-NP-Z2-9]{8})\".*", "$1");

    mockMvc.perform(get("/exports/" + code))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.kind").value("config"))
        .andExpect(jsonPath("$.data.theme").value("light"));
  }

  @Test
  void rejectsSensitiveFields() throws Exception {
    mockMvc.perform(post("/share")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"kind\":\"deck\",\"data\":{\"cards\":[],\"apiKey\":\"do-not-store\"}}"))
        .andExpect(status().isUnprocessableContent())
        .andExpect(jsonPath("$.error").value("sensitive_payload"));
  }

  @Test
  void deletesShare() throws Exception {
    MvcResult result = mockMvc.perform(post("/share")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"kind\":\"deck\",\"data\":{\"cards\":[]}}"))
        .andExpect(status().isCreated())
        .andReturn();

    String code = result.getResponse().getContentAsString().replaceAll(".*\"code\":\"([A-HJ-NP-Z2-9]{8})\".*", "$1");

    mockMvc.perform(delete("/share/" + code))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/share/" + code))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("not_found"));
  }
}
