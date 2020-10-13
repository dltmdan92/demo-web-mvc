package com.seungmoo.springmvc.demowebmvc;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

@Documented // --> 해당 Annotation에 문서화됬음을 뜻함(javadoc에 남음)
@Target({ElementType.METHOD}) // --> 해당 Annotation을 사용할 수 있는 Target, ElementType.METHOD : 메서드에 사용한다.
// 해당 애노테이션은 application이 구동될 때, 유지 되야 한다 --> RUNTIME (클래스를 메모리에 읽었을 때까지 유지)
// 그냥 애노테이션을 주석처럼 쓰겠다 --> SOURCE (소스코드 까지만 유지, 컴파일할 때 사라짐)
@Retention(RetentionPolicy.RUNTIME) // 애노테이션의 생명주기를 결정하는 Meta Annotation (중요), JAVA의 영역
@RequestMapping(method = RequestMethod.GET, value = "/custom") // 안타깝게드 @GetMapping은 Meta Annotation이 아니다.
public @interface GetCustomMapping {
}
