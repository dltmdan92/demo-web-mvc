package com.seungmoo.springmvc.demowebmvc;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component // Validator를 Controller에서 @Autowired 하는 경우에 선언
public class EventValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        // 어느 domain class에 대한 Validation을 지원하는지 설정
        return Event.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Event event = (Event) o;
        if (event.getName().equalsIgnoreCase("aaa")) {
            errors.rejectValue("name", "wrongValue", "the value is not allowed.");
        }
    }
}
