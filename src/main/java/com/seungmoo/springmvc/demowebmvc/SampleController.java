package com.seungmoo.springmvc.demowebmvc;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

@Controller
@RequestMapping(value = {"/hello", "/hi"}, method = {RequestMethod.GET, RequestMethod.PUT}) // Class 단위로 설정가능, Controller내 모든 핸들러에 적용된다.
public class SampleController {

    // 이러한 메소드를 핸들러라고 한다. (Controller 내 Request를 Mapping해서 Request를 처리하는 메소드)
    @RequestMapping("/helloView")
    public String helloView() {
        return "hello"; // 여기에 해당하는 View를 찾아간다.
    }

    // Http Method를 지정하지 않으면, 모든 Http Method(GET, POST, DELETE, PUT 등)를 허용하게 된다.
    // 특정한 URL에 매핑할 수 있다.
    // @RequestMapping(value = "/hello", method = {RequestMethod.GET, RequestMethod.PUT})
    // @GetMapping("/hello") // @RequestMapping GET을 사용한 것과 동일
    // @RequestMapping({"/hello", "/hi"}) // Controller Class에서 GET/PUT 요청 만 받도록 설정하고, 핸들러에는 RequestMapping("url")만 등록
    // @RequestMapping("/**") // class 단위에서 설정한 RequestMapping 뒤에 /** --> 여러 패스를 설정
    //
    @RequestMapping("/multipath/{name:[a-z]+}") // 정규식으로 사용한 경우 @PathVariable로 value를 받을 수 있다.
    @ResponseBody
    public String helloRest(@PathVariable String name) {
        return "hello " + name; // 해당 리턴 타입 데이터를 그대로 반환한다.
    }

    // 만약 /hello/multipath/seungmoo가 들어오게 될 경우, 위의 handler와 중복이 된다.
    // 이때는 어떻게 될까??? --> 가장 구체적인 handler에 매핑이 된다. (가장 우선순위는 /hello/multipath/seungmoo 로 명시된 핸들러 일 것임)
    // 여기서는 /** 보다는 위의 정규표현식이 더 구체적임
    //@RequestMapping(value = "/multipath/**")
    @RequestMapping("/multipath/**")
    @ResponseBody
    public String helloRestDup() {
        return "hello seungmoo"; // 해당 리턴 타입 데이터를 그대로 반환한다.
    }

    @PostMapping(value = "/multipath/**")
    @ResponseBody
    public String helloPost() {
        return "hello seungmoo";
    }

    @RequestMapping(
            value = "/reqJson",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String helloReqJson() {
        return "hello";
    }

    @RequestMapping(
            value = "/headerKeyVal",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            //headers = "!" + HttpHeaders.FROM, // not을 선언할 수 있다. (FROM이 없어야 한다.)
            headers = {HttpHeaders.FROM, HttpHeaders.AUTHORIZATION + "=" + "111"},
            params = "name=spring" // HEADER에 parameter를 셋팅할 수 있다.
            )
    @ResponseBody
    public String helloHeaderKeyVal() {
        return "hello";
    }

    @GetCustomMapping
    @ResponseBody
    public String helloCustom() {
        return "hello custom";
    }

    /**
     * RequestMapping에 관하여
     *
     * 요청 식별자로 맵핑하기
     * - RequestMapping의 URI 매핑은 Class 단위와 핸들러 단위의 선언을 조합해서 사용 가능
     *  ex) class - /hello, handler - /seungmoo ==> /hello/seungmoo
     * - @RequestMapping은 다음의 패턴을 지원한다.
     * - ? : 한 글자 ("/author/???" ==> "/author/123")
     * - * : 여러 글자 ("/author/*" ==> "/author/keesun")
     * - ** : 여러 패스 ("/author/**" ==> "/author/keesun/book", "/author/seungmoo/book/123")
     *
     * Tip : URI 확장자 맵핑은 Spring-boot에서는 지원하지 않는다. --> 404 Not Found 뜬다.
     * - 보안이슈 존재 (http://localhost:8080/hello.zip --> zip 파일 다운로드로 인식한다.)
     * - RFD Attack 이슈 존재
     * - 특정 리소스 json, html 등을 원할 때, http://localhost:8080/hello.json, http://localhost:8080/hello.html 이게 아니라
     * - Accept HEADER에 원하는 파일 형식을 등록하도록 한다.
     * - URI 확장자 맵핑은 안쓰는 추세임.
     * - 정 쓰고 싶으면 "/hello/seungmoo.*" 이런식으로 쓰든가 한다.
     *
     * ContentType Mapping
     * 1. 특정한 타입의 데이터를 담고 있는 Request만 처리하는 handler
     * - @RequestMapping(consumes = "application/json" or MediaType.APPLICATION_JSON_VALUE)
     * - Content-Type Header로 filtering
     * - consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE} 이렇게 배열로 선언가능
     * - 클래스에서 선언 후, 핸들러에서도 선언하면 조합 사용이 아닌, 핸들러에 Override해서 덮어 쓴다.
     *
     * 2. 특정한 타입의 응답을 만드는 핸들러
     * - @RequestMapping(produces = "application/json")
     * - Accept Header로 filtering
     * - produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE} 이렇게 배열로 선언가능
     * - 클래스에서 선언 후, 핸들러에서도 선언하면 조합 사용이 아닌, 핸들러에 Override해서 덮어 쓴다.
     */

    /**
     * GET 요청
     * - client가 server의 resource를 요청할 때 사용
     * - 캐싱할 수 있다. (조건적인 GET 요청을 통해 효율적 사용)
     *  ㄴ 조건적인 GET 요청 : HEADER에 ifNotModified, modifiedSince를 사용해서 조건에 따라 304(not modified)를 리턴하여
     *                      서버에서 body를 response하지 않아도 client가 갖고 있는 body를 사용하게 할 수 있다.
     * - 브라우저 기록, 북마크 할 수 있다.
     * - 민감한 데이터를 보낼 때 사용하지 말 것 (URL에 다 보임)
     * - idemponent하다 --> 동일한 GET 요청은 동일한 response를 리턴한다.
     *
     * POST 요청
     * - client가 server의 resource를 수정하거나 새로 만들 때 사용
     * - URI : 내가 보내는 데이터를 처리(Process)할 수 있는 Resource를 지정(indicate).
     * - idemponent하지 않다. --> 동일한 POST 요청에도 리턴되는 response는 다르다.
     *
     * PUT 요청
     * - URI에 해당하는 데이터를 새로 만들거나 수정할 때 사용
     * - URI : 내가 보내는 데이터에 해당하는 Resource를 지정(indicate).
     * - idemponent하다 --> 리소스에 대한 URI이기 때문에
     *
     * PATCH 요청
     * - PUT과 비슷하지만, 기존 엔티티와 새 데이터의 차이점만 보낸다.
     * - idemponent하다.
     *
     * DELETE 요청
     * - URI에 해당하는 리소스를 삭제할 때 사용
     * - idemponent하다.
     */
}
