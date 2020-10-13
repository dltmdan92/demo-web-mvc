package com.seungmoo.springmvc.demowebmvc;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

@Controller
@RequestMapping(
        produces = MediaType.APPLICATION_JSON_VALUE // controller의 모든 handler의 응답 타입 JSON으로 설정
)
public class EventController {

    @GetMapping("/events")
    @ResponseBody
    public ResponseEntity<String> events(Locale locale, TimeZone timeZone, ZoneId zoneId) {
        /**
         * <Handler Method Arguments>
         * - 주로 요청 그 자체 또는 요청에 들어있는 정보를 받아오는데 사용한다.
         *
         * NativeWebRequest, WebRequest parameter --> Request에 대한 광범위한 API 제공
         * Servlet의 Request API에 대해 Spring에서 추가적인 기능을 덧붙였음
         * --> EX) public String events(NativeWebRequest request) {
         *
         * HttpServletRequest, HttpServletResponse --> Servlet에서 제공하는 req, res API (Spring에서 당연히 사용 가능)
         * --> EX) public String events(HttpServletRequest request, HttpServletResponse response) {
         *
         * public String events(InputStream requestBody, OutputStream responseBody) {
         * --> InputStream은 request BODY, OutputStream은 response BODY로 이렇게 쓸 수 있다. (JAVA API 활용, Servlet API X)
         * --> 더 편하게 public String events(Reader requestBody, Writer responseBody) { --> 이렇게 쓸 수 있음
         *
         * Spring 5에서 새로 추가된 것 : PushBuilder (HTTP2 에서 사용)
         * --> 1. Client가 Server에 Resource request를 보냄 -> 2. Server가 Client에 Response를 보냄
         * --> 3. 그리고 또 Client가 Server에 Resource request를 보냄 -> 4. Server가 다시 한번 Client에 Response를 보냄
         * --> PushBuilder(HTTP2)는 위의 3번 절차를 없애준다. --> 좀 더 Research 해봐야 함
         * But!! 이런 것들은 실 개발하면서 잘안쓰게 된다.
         *
         * HttpMethod를 파라미터로 받을 수 있음
         * public String events(HttpMethod httpMethod) {
         * httpMethod.matches("GET") --> 이렇게 도 쓸 수 있는듯
         *
         * LocaleResolver가 분석한 Locale 정보
         * ex) public String events(Locale locale, TimeZone timeZone, ZoneId zoneId) {
         *
         * # 여기서 부터는 많이 쓰게될 것임
         * @PathVariable
         * @MatrixVariable --> 이건 자주 안 쓸거임
         * @RequestParam
         * @@RequestHeader
         * ... 등등
         * 참고 : https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-arguments
         */

        /**
         * Handler Method Return
         * - 주로 응답 또는 모델을 렌더링할 뷰에 대한 정보를 제공하는데 사용
         *
         * @ResponseBody
         * - 리턴 값을 httpMessageConverter를 사용해 응답 본문(Response Body)으로 사용한다.
         *
         * HttpEntity, ResponseEntity
         * - Response Body 뿐만 아니라 Header 정보까지, 전체 응답을 만들 때 사용
         * - HttpEntity<T>, ResponseEntity<T> --> ResponseBody에 들어갈 dataType을 알고 있다면, <T>으로 타입 명시 가능
         * - ex) public ResponseEntity<String> events(Locale locale, TimeZone timeZone, ZoneId zoneId) {
         * --> ResponseEntity를 사용하면 response Header/Body, status code 등등 response에 대해 다 셋팅할 수 있다.
         * --> ResponseEntity는 중요!!!
         *
         * String (@ResponseBody 애노테이션 없이 return 타입으로 쓰면)
         * - ViewResolver를 사용해서 View를 찾을 때 사용할 View Name
         * - ex) public String event() {  (@ResponseBody 셋팅 X)
         *
         * HttpHeaders
         * - ex) public httpHeaders events() {
         * --> response Header만 return 할 수 있다.
         *
         * View
         * - Return할 View를 안다고 하면 View를 return type으로 설정.
         * - ViewResolver를 생략하게 된다.
         *
         * Map/Model
         * - Map/Model로도 return 가능하다.
         * - 이 때 ViewResolver는 RequestToViewNameTranslator를 통해서 URI를 통해 View를 유추한다. (여기서는 events라는 View를 찾으려고 함)
         *
         * 기타 등등 핸들러 리턴 타입이 많이 있다.
         * 참고 - https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-return-types
         *
         */
        return ResponseEntity.ok().build();
    }

    @GetMapping("/events/{id}")
    @ResponseBody
    public String getAnEvents(@PathVariable("id") int id) {
        // @PathVariable("id") int idValue  or  @PathVariable int id  --> 이렇게 사용할 수 있다.
        return "events " + id;
    }

    @DeleteMapping("/events/{id}")
    @ResponseBody
    public String removeAnEvents(@PathVariable("id") int id) {
        // @PathVariable("id") int idValue  or  @PathVariable int id  --> 이렇게 사용할 수 있다.
        return "events " + id;
    }

}
