package com.nishchay.productservice.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {

    private final Logger logger= LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.nishchay.productservice.service.impl.*.*(..))")
    public void logBeforeMethod(JoinPoint joinPoint){
        logger.info("Entering method: {} with arguments: {}",joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "execution(* com.nishchay.productservice.service.impl.*.*(..))", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result){
        logger.info("Exiting method: {} with result: {}",joinPoint.getSignature().getName(),result);
    }

    @AfterThrowing(value = "execution(* com.nishchay.productservice.service.impl.*.*(..))",throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint,Throwable ex){
        logger.error("Exception in method: {} with cause {}",joinPoint.getSignature().getName(),ex.getMessage());
    }
}
