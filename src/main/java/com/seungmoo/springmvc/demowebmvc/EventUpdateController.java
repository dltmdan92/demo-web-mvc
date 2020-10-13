package com.seungmoo.springmvc.demowebmvc;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

// consumes 여부에 따라 HTTP 메서드 별로 Controller를 나눠서 쓸 수 있다.
@Controller
@RequestMapping(
        produces = MediaType.APPLICATION_JSON_VALUE, // controller의 모든 handler의 응답 타입 JSON으로 설정
        consumes = MediaType.APPLICATION_JSON_VALUE // controller의 모든 handler의 contentType Json으로 (받는 데이터)
)
public class EventUpdateController {

    @PostMapping("/events")
    @ResponseBody
    public String createEvent() {
        return "event";
    }

    @PutMapping("/events")
    @ResponseBody
    public String updateEvent() {
        return "event";
    }

}
