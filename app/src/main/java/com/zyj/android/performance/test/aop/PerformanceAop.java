package com.zyj.android.performance.test.aop;

import com.zyj.android.performance.test.utils.LogUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect  //
public class PerformanceAop {

    // 定义切点，也就是定义匹配规则，匹配到那些方法需要做耗时计算
    @Pointcut("call(* com.zyj.android.performance.test.PerformanceApp.**(..))")
    public void pointcut() {

    }


    // advice，定义具体的耗时计算方式
    @Around("call(* com.zyj.android.performance.test.PerformanceApp.**(..))")
    public void getTime(ProceedingJoinPoint joinPoint){
        String name = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        LogUtils.i(name + " cost " + (System.currentTimeMillis() - startTime));
    }
}
