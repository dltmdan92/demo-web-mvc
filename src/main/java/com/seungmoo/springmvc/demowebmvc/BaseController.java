package com.seungmoo.springmvc.demowebmvc;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 전역 컨트롤러 @ControllerAdvice
 * 예외 처리, 바인딩 설정, 모델 객체를 모든 컨트롤러 전반에 걸쳐 적용하고 싶은 경우에 사용한다.
 * ● @ExceptionHandler
 * ● @InitBinder
 * ● @ModelAttributes
 *
 * 스프링 4.0 부터 범위지정 가능
 */
@ControllerAdvice(assignableTypes = {URLPatternController.class, EventApiController.class}) // 범위 지정 가능 (패키지 범위도 가능함)
// @RestControllerAdvice --> 이것도 있음
public class BaseController {

    // ExceptionHandler에 다중 Exception을 정의할 수 있다.
    @ExceptionHandler({EventException.class, RuntimeException.class})
    public String eventErrorHandler(RuntimeException exception, Model model) { // 이 때 Exception은 위의 둘 다 받을 수 있게 상위타입으로 선언한다.
        model.addAttribute("message", "event error");
        // 에러 페이지를 리턴
        return "error";
    }



    // 실은 binder는 계속 써왔던 것임, Request의 parameter를 pathVariable, RequestBody, model 등등으로 binding 했던 것임
    // return type은 반드시 void
    @InitBinder("event") // 여기 "event" binder에 model attr name을 선언해주면, "event"라는 model-attr을 bind할때만 initBinder를 사용
    public void initEventBinder(WebDataBinder webDataBinder) {
        // 받고 싶지 않은 field 값을 걸러낼 수 있다.
        // requsetBody의 event 객체에 id값이 들어와도 null로 binding 된다.
        webDataBinder.setDisallowedFields("id");

        // formatter 하나 만들어서 add 해줄 수 있다.
        // webDataBinder.addCustomFormatter();

        // validator는 이렇게 global하게 할 수도 있고
        // 아니면 Validator를 @Autowired으로 만든 다음에, 직접 handler에서 명시적으로 validate 실행할 수 도 있다.
        //webDataBinder.addValidators(new EventValidator());
    }

    // @ModelAttribute의 또 다른 사용법
    // @RequestMapping을 사용하는 핸들러 메소드들에 "공통 Model attribute"로 사용하기 위해
    // 여기 Controller의 handler가 호출될 때, 같이 실행된다.
    @ModelAttribute
    public void categories(Model model) {
        // 이렇게 하면 model에 여러개 넣어줄 수 있음
        model.addAttribute("categories", Arrays.asList("study", "seminar", "hobby", "social"));
    }
}
