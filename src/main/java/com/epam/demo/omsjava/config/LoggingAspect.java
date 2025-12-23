package com.epam.demo.omsjava.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    
    @Around("execution(* com.epam.demo.omsjava.service..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String methodName = joinPoint.getSignature().getName();
        
        logger.info("Executing: {}", methodName);
        long start = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            logger.info("Completed: {} in {}ms", methodName, duration);
            return result;
        } catch (Exception e) {
            logger.error("Failed: {} - {}", methodName, e.getMessage());
            throw e;
        }
    }
    
    @Around("execution(* com.epam.demo.omsjava.saga..*(..))")
    public Object logSagaMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String methodName = joinPoint.getSignature().getName();
        
        logger.info("SAGA: {} started", methodName);
        
        try {
            Object result = joinPoint.proceed();
            logger.info("SAGA: {} completed", methodName);
            return result;
        } catch (Exception e) {
            logger.error("SAGA: {} failed - {}", methodName, e.getMessage());
            throw e;
        }
    }
}
