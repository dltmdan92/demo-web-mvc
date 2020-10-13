package com.seungmoo.springmvc.demowebmvc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest // 스프링 프로젝트 전반에 걸친 테스트 --> 이경우에는 @WebMvcTest와는 다르게 모든 Bean이 등록되나, MockMvc는 자동으로 만들지 않는다.
@AutoConfigureMockMvc // MockMvc를 자동으로 만들어주기 위해 달아준다.
public class FileControllerTest {

    // @AutoConfigureMockMvc에서 MockMvc를 만들어준다. or @SpringBootTest말고 @WebMvcTest를 쓰던가...
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void fildUploadTest() throws Exception {
        // spring-mvc에서 지원하는 Test용 class, 가짜 file을 만들어낸다.
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "hello file".getBytes());

        // multipart는 post 요청임, encrupt-type : multipart/form-data
        this.mockMvc.perform(multipart("/file").file(file))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection());
    }
}