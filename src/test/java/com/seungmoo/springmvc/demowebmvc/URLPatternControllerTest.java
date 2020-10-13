package com.seungmoo.springmvc.demowebmvc;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RunWith(SpringRunner.class)
@WebMvcTest
public class URLPatternControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    public void getEventV1() throws Exception {
        // 현재 스프링부트 커뮤니티에서 URI에 Matrix binding으로 KEY-VALUE request 보내주는 것 논의 중
        // ex) /events/1;name=seungmoo
        // Matrix binding은 not default support --> WebConfig에서 setting 필요
        mockMvc.perform(get("/url_pattern/events/1;name=seungmoo"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("seungmoo"));
    }

    @Test
    public void getEventV2() throws Exception {
        mockMvc.perform(get("/url_pattern/events/queryString?name=seungmooLee&limit=20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("seungmooLee"));

        mockMvc.perform(get("/url_pattern/events/queryString")
                        .param("name", "seungmooLee")
                        .param("limit", "20")) // 숫자로 주고 handler에서 int로 받으면 알아서 Type 변형
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("seungmooLee"));
    }

    @Test
    public void eventForm() throws Exception {
        MockHttpServletRequest request = mockMvc.perform(get("/url_pattern/events/form"))
                                                .andDo(print())
                                                .andExpect(view().name("/events/form"))
                                                .andExpect(model().attributeExists("event"))
                                                .andExpect(request().sessionAttribute("event", notNullValue()))
                                                .andReturn().getRequest();
        Object event = request.getSession().getAttribute("event");
        log.debug(event.toString());
    }

    @Test
    public void eventModelAttribute() throws Exception {
        mockMvc.perform(get("/url_pattern/events/model_attribute/name/seungmoo")
                            .param("limit", "-10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("seungmoo"));
    }

    @Test
    public void eventPost() throws Exception {
        ResultActions result = mockMvc.perform(post("/url_pattern/events/validated/form")
                                                .param("name", "seungmoo")
                                                .param("limit", "-10"))
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andExpect(model().hasErrors());

        ModelAndView mav = result.andReturn().getModelAndView();
        Map<String, Object> model = mav.getModel();
        log.info("model.size() is : " + model.size());
    }

    @Test
    public void getEvents() throws Exception {
        Event newEvent = new Event();
        newEvent.setName("Winter is coming");
        newEvent.setLimit(10000);

        mockMvc.perform(get("/url_pattern/events/list")
                            .sessionAttr("visitTime", LocalDateTime.now())
                            .flashAttr("newEvent", newEvent))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(xpath("//p").nodeCount(2)) // p 노드가 2개 있는지 check
                        .andExpect(model().attributeExists("categories"));
    }
}