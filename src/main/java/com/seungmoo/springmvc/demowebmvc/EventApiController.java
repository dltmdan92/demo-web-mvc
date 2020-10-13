package com.seungmoo.springmvc.demowebmvc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.naming.Binding;
import javax.validation.Valid;

@Slf4j
//@RestController
@Controller // ResponseEntity를 리턴하는 Handler는 @RestController가 필요없다.
@RequestMapping("/api/events")
public class EventApiController {

    // REST API의 경우에는 ExceptionHandler에서 ResponseEntity를 return (주로 이렇게 쓴다.)
    // client에 error 정보를 주기 위함
    @ExceptionHandler
    public ResponseEntity errorHandler() {
        return ResponseEntity.badRequest().body("can't create event as ....");
    }

    @PostMapping
    // @ResponseBody 란???
    // response 값을 HttpMessageConverter를 사용해서 response Body message로 보낼 때 사용, @RestController 사용 시 자동으로 모든 핸들러 메소드에 적용
    // @ResponseBody는 가장 기본적으로 RequestHeader에서 Accept header를 본다.
    // @ResponseBody // @RestController 사용 시 생략
    public ResponseEntity<Event> createEvent(
            // HttpEntity를 통해 request header,body를 둘다 받을 수 있다.
            // BUT!! HttpEntity는 @Valid, @Validated 못쓴다.
            //@RequestParam HttpEntity<Event> request

            // @Valid, @Validated에서 검증 실패 시 400error가 발생한다. BUT BindingResult를 통해 400error를 바로 발생시키지 않고 custom 가능
            @Validated(Event.ValidateAll.class) @RequestBody Event request,// request body (요청 본문)에 들어오는 Event를 받는다. BUT!!! Header정보는 못갖고 온다.
            BindingResult bindingResult, // --> HttpEntity 쓰면 @Valid도 못쓰고 이것도 못쓴다.
            @RequestHeader HttpHeaders httpHeaders
    )
    {
        // handlerAdapter 가 HttpMessageConverter를 사용한다.
        // 해당 requestBody에 맞는 Converter를 선택해서 Conversion을 하고 handler로 들어온다.
        // ex) Content-Type을 보고 json이면 jsonConvert하는 HttpMessageConverter(jackson2)를 사용 --> Event 객체로 json 문자열을 변환해준다.
        // spring-boot-starter-web 내 stater-json에서 jackson2를 기본 제공하고 있다.
        //MediaType contentType = request.getHeaders().getContentType();

        // bindingResult를 통해 binding error 났을 때 서버에서 좀 더 custom하여 response를 return 할 수 있다.
        if(bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> log.error(e.getDefaultMessage()));
            return ResponseEntity.badRequest().build();
        }

        ResponseEntity<Event> responseEntity = new ResponseEntity(request, HttpStatus.OK);
        return  responseEntity;
        //return ResponseEntity.ok(request.getBody());
    }

}
