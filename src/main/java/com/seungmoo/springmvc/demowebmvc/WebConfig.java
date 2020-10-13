package com.seungmoo.springmvc.demowebmvc;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

// 스프링부트에서 웹 Config하려면 @Configuration + WebMvcConfigurer implement
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Matrix Binding을 하려면 따로 WebConfig에서 설정 필요하다. (스프링부트에서 디폴트 지원 ㄴㄴ)
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        // semi-colon을 없애게 되면 Matrix Binding이 안되기 때문에 아래 설정 필수!
        urlPathHelper.setRemoveSemicolonContent(false);
        configurer.setUrlPathHelper(urlPathHelper);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new VisitTimeInterceptor());
    }
}
