package com.ennie.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//交给容器管理
//@Component
//@Aspect
public class AlphaAspect {


    @Pointcut("execution( * com.ennie.community.service.*.*(..))")
    public void pointcut(){
    }

    @Before("pointcut()")
    public void before(){
        System.out.println("before");
    }

    @After("pointcut()")
    public void after(){
        System.out.println("after");
    }

    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("afterReturning");
    }

    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        System.out.println("afterThrowing");
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        System.out.println("around before");
        Object obj = point.proceed();
        System.out.println("around after");



        return obj;
    }



}
