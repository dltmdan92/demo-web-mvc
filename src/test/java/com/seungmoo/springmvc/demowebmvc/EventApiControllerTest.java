package com.seungmoo.springmvc.demowebmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventApiControllerTest {
    @Autowired
    ObjectMapper objectMapper; // jackson에서 제공하는 APi, string <-> json 변환 해준다.

    @Autowired
    MockMvc mockMvc;

    @Test
    public void createEvent() throws Exception {
        Event event = new Event();
        event.setName("seungmoo");
        event.setLimit(20);

        String json = objectMapper.writeValueAsString(event);

        mockMvc.perform(post("/api/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("name").value("seungmoo"))
                        .andExpect(jsonPath("limit").value(20));
    }
}