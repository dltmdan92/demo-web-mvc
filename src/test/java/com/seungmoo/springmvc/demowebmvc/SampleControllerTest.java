package com.seungmoo.springmvc.demowebmvc;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// RunWith junit의 Annotation
// SpringRunner : Spring에서 제공해주는 JUnit Runner임. Junit을 편하게 사용하도록 Spring에서 제공
@RunWith(SpringRunner.class)
@WebMvcTest // @WebMvcTest 통해 Web Test 진행한다. (Web MVC에 관련된 Bean @Controller, @Service, @Repository만 체크)
public class SampleControllerTest {
    @Autowired
    MockMvc mockMvc;

    // JUnit 테스트는 public void로 선언해야 한다. 그래야 junit test로 인식하고 실행할 수 있다.
    @Test
    public void helloTest() throws Exception {
        mockMvc.perform(get("/hello/multipath/seungmoo")) // mockMvc.perform -> mvc 요청보내기
                .andDo(print()) // Request header, body 출력 (log 출력)
                .andExpect(status().isOk()) // Response status 체크
                .andExpect(content().string("hello seungmoo")) // Response Body 체크
                .andExpect(handler().handlerType(SampleController.class))
                .andExpect(handler().methodName("helloRest")) // 이렇게 handler에 대한 체크도 할 수 있다.
                ;

        mockMvc.perform(put("/hi/multipath/seungmoo")) // mockMvc.perform -> mvc 요청보내기
                .andDo(print()) // Request header, body 출력 (log 출력)
                .andExpect(status().isOk()) // Http Method가 잘못됬다.
                ;
                // 405 error 발생
                // Error message = Request method 'PUT' not supported
                // Headers = [Allow:"GET"]  --> 허용하는 http Method도 알려준다.

                // 4** error : client에서 Request를 잘못 보낸 경우
                // 5** error : server에서 잘못 처리되는 경우

        mockMvc.perform(post("/hi/multipath/seungmoo"))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void helloJson() throws Exception {
        mockMvc.perform(get("/hello/reqJson") // APPLICATION_JSON, APPLICATION_JSON_VALUE 동일하게 인식되는 듯..
                .contentType(MediaType.APPLICATION_JSON) // 핸들러가 consumes에서 JSON 정의 했다면 request의 contentType에서 JSON 명시 필요, 415 에러 (Unsupported Media Type)
                .accept(MediaType.APPLICATION_JSON_VALUE)) // 핸들러가 produces에서 JSON 정의 했다면 request의 accept에서 JSON 명시 필요, 404 에러 (Not Acceptable)
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void helloHeader() throws Exception {
        MultiValueMap<String, String> map = new HttpHeaders();
        List fromList = new ArrayList<String>();
        fromList.add("localhost");
        List authList = new ArrayList<String>();
        authList.add("111");

        map.put(HttpHeaders.FROM, fromList);
        map.put(HttpHeaders.AUTHORIZATION, authList);

        /**
         * HttpMethod head
         * head 메소드는 GET 요청과 동일하지만 response BODY를 받지 않고 response HEADER만 받아온다.
         * 서버에서 구현할 필요 없음, 그냥 GET handler에 head로 요청보낸다.
         */
        mockMvc.perform(head("/hello/headerKeyVal")
                .contentType(MediaType.APPLICATION_JSON) // 핸들러가 consumes에서 JSON 정의 했다면 request의 contentType에서 JSON 명시 필요, 415 에러 (Unsupported Media Type)
                .accept(MediaType.APPLICATION_JSON_VALUE) // 핸들러가 produces에서 JSON 정의 했다면 request의 accept에서 JSON 명시 필요, 406 에러 (Not Acceptable)
                .headers(new HttpHeaders(map)) // http headers의 key value 에러는 404 에러로 나온다. Not Found
                .param("name", "spring") // HEADER의 parameter 에러는 400 Bad Request 이다.
                //.header(HttpHeaders.FROM, "localhost") // Request HEADER에 key, value를 줄 수 있다.
                //.header(HttpHeaders.AUTHORIZATION, "111")
        )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    /**
     * Http Method - Options : 사용할 수 있는 HTTP Method 제공
     * - 서버 또는 특정 리소스가 제공하는 기능을 확인할 수 있다.
     * - 서버는 Allow 응답 헤더에 사용할 수 있는 HTTP Method 목록을 제공해야 한다.
     * - ex) Headers = [Allow:"GET,HEAD,PUT,POST,OPTIONS"] 이렇게 리턴된다.
     * @throws Exception
     */
    @Test
    public void helloOptions() throws Exception {
        mockMvc.perform(options("/hello/multipath/seungmoo"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.ALLOW)) // Header에 ALLOW라는 값이 있는지 체크
                //.andExpect(header().stringValues(HttpHeaders.ALLOW, "GET,HEAD,PUT,POST,OPTIONS")) // 그냥 String이면 순서 맞춰야 된다....
                .andExpect(header().stringValues(HttpHeaders.ALLOW,
                        hasItems(   containsString("GET"),
                                    containsString("POST"),
                                    containsString("PUT"),
                                    containsString("HEAD"),
                                    containsString("OPTIONS")))) // Matcher를 사용해서 비순서적으로 가능하게..
                ;
    }

    @Test
    public void helloCustom() throws Exception {
        mockMvc.perform(get("/hello/custom"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getEvents() throws Exception {
        mockMvc.perform(get("/events"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getEventsWithId() throws Exception {
        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/events/2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/events/3"))
                .andExpect(status().isOk());
    }

    @Test
    public void createEvent() throws Exception {
        // "/events"에 대해서 post로 요청을 보냈는데
        // 서버는 get요청만 받는다고 하면 --> 405 error, METHOD NOT SUPPORTED 가 발생한다.
        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void deleteEvent() throws Exception {
        mockMvc.perform(delete("/events/1"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/events/2"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/events/3"))
                .andExpect(status().isOk());
    }

    @Test
    public void updateEvent() throws Exception {
        // "/events"에 대해서 post로 요청을 보냈는데
        // 서버는 get요청만 받는다고 하면 --> 405 error, METHOD NOT SUPPORTED 가 발생한다.
        mockMvc.perform(put("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

}