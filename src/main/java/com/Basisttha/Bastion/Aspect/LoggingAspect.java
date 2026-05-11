package com.Basisttha.Bastion.Aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);
    //return type, fully qualified method name, method name, args
    @Around("execution(* com.Basisttha.Bastion.Service.*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable{
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getClass().getSimpleName();

        LOGGER.info("class name = "+ className + " | method name = "+ methodName + "called at "+ System.currentTimeMillis());

        try{
            Object result = joinPoint.proceed();
            LOGGER.info("class name = "+ className + " | method name = "+ methodName + " sucessfully executed, ended at "+ System.currentTimeMillis());
            return result;
        }
        catch(Exception e){
            LOGGER.error("class name = "+ className + " | method name = "+ methodName + " threw exception");
            throw e;
        }
    }
}
