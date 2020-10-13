package com.seungmoo.springmvc.demowebmvc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
public class Event {

    // 스프링 @Validated Annotation을 위한 Grouping
    interface ValidateLimit {}
    interface ValidateName {}
    interface ValidateAll extends ValidateName, ValidateLimit {}

    private Integer id;

    @NotBlank(groups = {ValidateName.class})
    private String name;

    // Request Param이 -1로 들어오게 되면
    // 먼저 limit 변수에 binding이 된다.
    // 그리고 나서 Validation이 수행됨
    @Min(value = 1, groups = {ValidateLimit.class})
    private Integer limit;

    // formatter
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // (pattern = "yyyy-MM-dd") --> 패턴처리도 가능하다.
    private LocalDate startDate;
}
