package com.seungmoo.springmvc.demowebmvc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 핸들러 메소드 2부:URL 패턴 공부하기
 * 핸들러 메소드 2부:요청 매개변수
 * 1. @PathVariable
 * 2. @MatrixVariable
 */
@Slf4j
@Controller
@RequestMapping(value = {"/url_pattern"})
// @SessionAttributes의 기능
// "event"(sessionAttribute Name)에 해당하는 modelAttribute를 자동으로 Session에 넣어줌
// model.addAttribute("event", new Event()) --> addAttribute 하거나 핸들러에서 (@ModelAttribute Event event)해주면
// 해당 Event라는 객체를 Session에 넣어 준다는 것이다. (session attribute name은 "event")
// row level로 하고 있으면 HttpSession으로만 처리할 수 있음
@SessionAttributes({"event"})
public class URLPatternController {

    /*
    // ExceptionHandler에 다중 Exception을 정의할 수 있다.
    @ExceptionHandler({EventException.class, RuntimeException.class})
    public String eventErrorHandler(RuntimeException exception, Model model) { // 이 때 Exception은 위의 둘 다 받을 수 있게 상위타입으로 선언한다.
        model.addAttribute("message", "event error");
        // 에러 페이지를 리턴
        return "error";
    }
     */

    /*
    // Exception Handler 하는 방법 (개별 실행)
    // 구체적인 Exception을 받는 ExceptionHandler가 우선 실행된다.
    @ExceptionHandler
    public String eventErrorHandler(EventException exception, Model model) {
        model.addAttribute("message", "event error");
        // 에러 페이지를 리턴
        return "error";
    }

    @ExceptionHandler
    public String runtimeErrorHandler(RuntimeException exception, Model model) {
        model.addAttribute("message", "event error");
        // 에러 페이지를 리턴
        return "error";
    }
     */

    @Autowired
    EventValidator eventValidator;

    /*
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
     */

    /*
    // @ModelAttribute의 또 다른 사용법
    // @RequestMapping을 사용하는 핸들러 메소드들에 "공통 Model attribute"로 사용하기 위해
    // 여기 Controller의 handler가 호출될 때, 같이 실행된다.
    @ModelAttribute
    public void categories(Model model) {
        // 이렇게 하면 model에 여러개 넣어줄 수 있음
        model.addAttribute("categories", Arrays.asList("study", "seminar", "hobby", "social"));
    }
     */

    // @ModelAttribute는 이렇게 해줘도 된다. (위랑 같음)
    /*
    @ModelAttribute("categories")
    public List<String> categories(Model model) {
        return Arrays.asList("study", "seminar", "hobby", "social");
    }
     */

    // @RequestMapping과 같이 사용하면 해당 handler method에서 return 하는 객체를 Model에 넣어 준다. (RequestToViewNameTranslator)
    // handler에서 return 하는 객체를 model에 담아준다. 이거 생략 가능
    @GetMapping("/events/getModel")
    @ModelAttribute
    public Event eventGetModel() {
        return new Event();
    }

    // @PathVariable parameter의 이름은 웬만하면 URI의 PathVariable과 맞춰주는게 보기 좋다.
    @GetMapping("/events/{id}")
    @ResponseBody
    public Event getEvent(@PathVariable("id") Optional<Integer> idValue, @MatrixVariable Optional<String> name) { // {id}값이 Integer로 자동 Type Conversion 된다.
        // public Event getEvent(@PathVariable(required = false) Integer id) { // Optional과 동일하게 기능한다.
        Integer optionalId = idValue.filter(i -> i > 0).orElse(1);
        String optionalName = name.filter(s -> !s.isEmpty()).orElse("NO_NAME");
        Event event = new Event();
        event.setId(optionalId);
        event.setName(optionalName);
        return event;
    }

    // GET /owners/42;q=11/pets/21;q=22
    @GetMapping("/owners/v1/{ownerId}/pets/{petId}")
    public void findPetV1(
            @MatrixVariable(name = "q", pathVar = "ownerId") int q1,
            @MatrixVariable(name = "q", pathVar = "petId") int q2
    ) {
        // q1 = 11
        // q2 = 22
    }

    // GET /owners/42;q=11;r=12/pets/21;q=22;s=23
    @GetMapping("/owners/v2/{ownerId}/pets/{petId}")
    public void findPetV2(
            @MatrixVariable MultiValueMap<String, String> matrixVars,
            @MatrixVariable(pathVar = "petId") MultiValueMap<String, String> petMatrixVars
    ) {
        // matrixVars: ["q" : [11, 22], "r" : 12, "s" : 23]
        // petMatrixVars: ["q" : 22, "s" : 23]
    }

    /**
     * 핸들러 메소드 2부 :요청 매개 변수
     * request Parameter가 들어오는 방식은 2가지로 나뉜다.
     * 1. Query String
     * ex) "/events/queryString/{id}?name=seungmoo&age=29&company=lotte"
     *
     * 2. Form-data (key-value)
     */

    // request parameter 1. Query String
    // "/events/queryString/{id}?name=seungmoo"
    // @RequestParam 생략 가능(Simple Type) but 명시적으로 선언하는게 좋다.
    // parameter Name은 웬만하면 맞춰주는게 좋다. @RequestParam의 value 선언 생략
    @GetMapping("/events/queryString")
    @ResponseBody
    //public Event getEventVer2(@RequestParam(value = "name", required = false, defaultValue = "seungmoo") String nameValue) {
    //public Event getEventVer2(@RequestParam Map<String, String> params) { // Map으로 받을 수 있다.
    public Event getEventVer2(@RequestParam String name,
                              @RequestParam Integer limit) {
        Event event = new Event();
        event.setName(name);
        event.setLimit(limit);
        return event;
    }

    // form으로 데이터 받는 것도 별반 다를게 없음
    @GetMapping("/events/form")
    public String eventsForm(Model model, HttpSession httpSession) { // Session을 받을 수 있다.
        Event newEvent = new Event();
        newEvent.setLimit(50);

        // model attribute 중 @SessionAttributes에 설정한 이름과 동일한 attr이 있으면 Session에 넣어준다.
        model.addAttribute("event", newEvent);
        // session 범위에 객체를 담아둘 수 있다.
        //httpSession.setAttribute("event", newEvent);

        return "/events/form";
    }

    // 제각각 parameter 선언할 필요 없이, @ModelAttribute Composite-Type 객체로 한번에 request-parameter 선언
    // @ModelAttribute 생략 가능함
    // @ModelAttibute에 @Valid or @Validated 를 얹어서 Validation 또한 가능하다.
    // @Valid와 BindingResult를 같이 쓰면 Validation Exception을 BindingResult가 받아 준다.
    @GetMapping("/events/model_attribute/name/{name}")
    @ResponseBody
    public Event getEventVer3(@Valid @ModelAttribute Event event, BindingResult bindingResult) { // @Valid @ModelAttribute BindingResult 조합 --> 많이 사용함
        // @ModelAttribute Request parameter에 bindingError가 있을 경우 400 Bad Request error 발생
        // but 그냥 parameter받고 error 별도로 처리하려면, 해당 @ModelAttribute param 바로 오른쪽에 BindingResult를 넣어준다.
        if(bindingResult.hasErrors()) {
            log.error("========================");
            bindingResult.getAllErrors().forEach(c -> log.error(c.toString()));
        }
        return event;
    }
    /**
     * NOTE!!
     * Spring-boot 2.3 Version 부터는 Validation-starter를 지원하지 않는다!!
     * 이전까지는 sprint-boot-starter-web을 추가하면 validation도 같이 탑재했음
     *
     * javax.validation api 를 maven dependency 추가해서 해봐도 Request-param에 대한 Validation이 작동하지 않을거임
     * - spring-boot-starter-validation 를 직접 dependency 추가해주도록 할 것!!!
     *
     * <Title : Spring Boot 2 3, Web-starter doesn't bring Validation-starter anymore>
     * URL : https://www.youtube.com/watch?v=cP8TwMV4LjE&feature=youtu.be (백기선 유튜브 참고)
     */


    /**
     * @Validated를 써보자
     * 스프링에서 만든 Validation용 Annotation
     * Group화를 통한 Validation 로직 통제가 가능하다.
     * @Validated(Event.ValidateLimit.class) --> ValidateLimit 그룹에 해당하는 Validation만 진행
     */
    @GetMapping("/events/validated/name/{name}")
    @ResponseBody
    public Event getEventVer4(
            @Validated(Event.ValidateLimit.class) @ModelAttribute Event event,
            BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            log.error("========================");
            bindingResult.getAllErrors().forEach(c -> log.error(c.toString()));
        }
        return event;
    }

    // PostMapping 할려고 했는데, 일단 편의상 GetMapping으로 했음음
   @GetMapping("/events/validated/form")
    public String postEvent(
            @Validated(value = {Event.ValidateAll.class}) @ModelAttribute Event event,
            BindingResult bindingResult,
            Model model,
            SessionStatus sessionStatus) {
        if(bindingResult.hasErrors()) {
            return "/events/form";
        }

        List<Event> eventList = new ArrayList<>();
        eventList.add(event);
        model.addAttribute("eventList", eventList); // attribute name이 view에 선언된 attribute name과 동일하다면 앞의 파라미터(attribute name)생략 가능

        // 특정 시점에 session의 status를 셋팅할 수 있다.
        // 일반적으로 SessionAttribute 쓸 경우, SessionStatus도 같이 사용한다.
        sessionStatus.setComplete(); // 세션 비우기

        // 리다이렉트를 통해 post 요청 이후 브라우저를 fresh 하더라도
        // 중복 form-submit이 발생하지 않도록 하는 패턴
        // form-submit을 방지하고 GET 요청이 일어나도록 처리한다.
        // 여기서는 부득이하게 GET handlerMapping썻지만...
        return "redirect:/url_pattern/events/list";
    }

    // 리다이렉트 받아서 GET 요청 처리해주는 handler
    @GetMapping("/events/list")
    public String getEventsRedirect(// ModelAttribute로 받을 때 SessionAttribute와 같은 이름 쓰지 않도록 조심!!
                                    // ModelAttribute받을 때 먼저 SessionAttribute를 한번 보고 "event"라는 이름을 가져올려고 함
                                    // eventsFormLimitSubmit 핸들러에서 이미 sessionState.setComplete()으로 세션 비웠으므로 세션없으니 오류 발생
                                    // ModelFactory.class 참고 바람
                                    // 오류 피하기 위해 sessionAttribute이름과 다른 "newEvent" 으로 model을 binding해주면 된다.
                                    //@ModelAttribute("newEvent") Event event, // RedirectAttribute 받을 때 ModelAttribute로 통으로 받을 수 있음
                                    //@RequestParam String name, // RedirectAttribute 받기
                                    //@RequestParam Integer limit, // RedirectAttribute 받기
                                    Model model,
                                    @SessionAttribute LocalDateTime visitTime) {

        // 위에서 @ModelAttribute("newEvent") Event event 파라미터 받을 필요 없이
        // model을 사용해서 session attribute를 꺼낼 수 있다.
        Event newEvent = (Event) model.asMap().get("newEvent");

        log.info(visitTime.toString());
        // DB가 있다고 가정하고 일단 이렇게 처리
        Event evt = new Event();
        evt.setName("spring");
        evt.setLimit(10);

        List<Event> eventList = new ArrayList<>();
        eventList.add(evt);
        eventList.add(newEvent);

        // "/events/list" View 에다가 아래 MODEL 정보를 Rendering
        model.addAttribute(eventList);
        // GET 요청 처리 후 마지막으로 "/events/list"를 보여준다.
        return "/events/list";
    }


    // Multi Form submit 실습
    // Multi-Form, 여러 개의 Form을 거쳐서 Event 객체 데이터를 완성하기위해 Session을 사용할 수 있다.
    // 클래스 선언부 맨위에서 @SessionAttributes를 통해 "event" name으로 SessionAttribute를 넣어주고 있음.
    // @SessionAttributes와 @SessionAttribute는 다른 것임. (많이 다름)
    // @SessionAttributes는 하나의 클래스(Controller) 안에서 세션 데이터 관리(범위 : 하나의 클래스)
    // @SessionAttribute를 사용해서 여러 클래스에서 SessionAttribute를 호출하고 사용할 수 있다.
    @GetMapping("/events/form/name")
    public String eventsFormName(Model model,
                                 // @SessionAttribute로 Session값 꺼낼 수 있다. 변수name은 sessionAttr name과 맞춰주면 좋음
                                 @SessionAttribute(value = "visitTime") LocalDateTime visitTime)
    {
        log.info(visitTime.toString());
        model.addAttribute("event", new Event());
        return "/events/form-name";
    }

    @PostMapping("/events/form/name")
    public String eventsFormNameSubmit(
            // @SessionAttributes("event") + @ModelAttribute Event event --> event로 sessionAttr 추가 됨
            @Validated(value = Event.ValidateAll.class) @ModelAttribute Event event,
            BindingResult bindingResult,
            // 위의 @SessionAttribute를 쓰지 않고 raw하게 HttpSession을 쓸 수 있다.
            HttpSession httpSession)
    {
        // BUT HttpSession.getAttribute는 Object type으로 리턴하므로
        // Type Conversion 작업이 추가로 더 필요하게 된다. --> @SessionAttribute가 편하니까 이거 쓰자 그냥
        LocalDateTime visitTime = (LocalDateTime) httpSession.getAttribute("visitTime");
        log.info(visitTime.toString());
        Event newEvt = (Event) httpSession.getAttribute("event");
        log.info("SessionAttribute name in post : " + newEvt.getName());
        log.info("SessionAttribute startDate in post : " + newEvt.getStartDate());

        if(bindingResult.hasErrors()) {
            return "/events/form-name";
        }

        // 원하는 시점에 Validator를 사용할 수 있다.
        eventValidator.validate(newEvt, bindingResult);

        // 여기서 session Clear 하면 /events/form/limit(GET) 핸들러에서 Expected하는 "event" 세션을 못받는다.
        //sessionStatus.setComplete();
        return "redirect:/url_pattern/events/form/limit";
    }

    @GetMapping("/events/form/limit")
    public String eventsFormLimit(@ModelAttribute Event event, Model model, HttpSession httpSession) {
        Event newEvt = (Event) httpSession.getAttribute("event");
        log.info("SessionAttribute name : " + newEvt.getName());
        model.addAttribute("event", event);
        return "/events/form-limit";
    }

    @PostMapping("/events/form/limit")
    public String eventsFormLimitSubmit(@Validated(value = Event.ValidateAll.class) @ModelAttribute Event event,
                                        BindingResult bindingResult,
                                        HttpSession httpSession,
                                        SessionStatus sessionStatus,
                                        //Model model
                                        RedirectAttributes redirectAttributes)
    {
        Event sessionEvent = (Event) httpSession.getAttribute("event");
        log.info(sessionEvent.getName());
        if(bindingResult.hasErrors()) {
            return "/events/form-limit";
        }
        sessionStatus.setComplete();

        // spring.mvc.ignore-default-model-on-redirect=false 로 셋팅해주고
        // model에 아래처럼 name, limit addAttribute해준다음에
        // redirect를 해주면 뒤에 query string으로 parameter가 붙게 된다.
        // redirect:/url_pattern/events/list?name={}&limit={} 이렇게 됨
        //model.addAttribute("name", event.getName());
        //model.addAttribute("limit", event.getLimit());

        // spring.mvc.ignore-default-model-on-redirect=false 로 셋팅한 거 없이, spring-boot 기본설정(true)으로 하고
        // 위의 방식 말고 RedirectAttributes를 사용하는 방법이 있음
        // addAttribute는 임의의 객체가 아닌 String으로 넣어줘야 한다. (query string으로 들어가야 함)
        //redirectAttributes.addAttribute("name", event.getName());
        //redirectAttributes.addAttribute("limit", event.getLimit());

        // addFlashAttribute --> 바로 session에 넣어준다
        // 그리고 redirect된 핸들러에서 처리가 되면, 해당 데이터는 session에서 제거 된다.
        // session을 통해서 전달되므로 URI 경로에 데이터가 노출되지 않는다.
        // 1회성 = flash
        // addFlashAttribute는 임의의 객체를 넣어줄 수 있다. (sessionAttribute에 저장되므로)
        redirectAttributes.addFlashAttribute("newEvent", event);
        return "redirect:/url_pattern/events/list";
    }
    // Multi-Form 실습 끝

    // ExceptionHandler가 받아서 error page로 이동
    @GetMapping("/event/exception")
    public String eventException(Model model) {
        throw new EventException();
    }
}
