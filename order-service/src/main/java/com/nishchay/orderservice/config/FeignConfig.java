package com.nishchay.orderservice.config;

import feign.RequestInterceptor;
import io.micrometer.core.instrument.binder.http.HttpServletRequestTagsProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
//import org.springframework.web.context.request.ServletRequetAttributes;

@Configuration
public class FeignConfig {


    @Bean
    public RequestInterceptor interceptor(){
        return requestTemplate -> {
            HttpServletRequest request= ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
            String cookie = request.getHeader("Cookie");
            if(cookie!=null){
                requestTemplate.header("Cookie",cookie);
            }
        };

    }
}
