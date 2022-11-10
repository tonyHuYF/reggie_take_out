package com.tony.reggie_take_out.aop;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LopAspect {


    @Pointcut("execution( * com.tony.reggie_take_out.controller.*.*(..))")
    public void logAspect() {
    }

    /**
     * 环绕通知
     */
    @Around("logAspect()")
    public Object around(ProceedingJoinPoint joinPoint) {
        long startTime = System.currentTimeMillis();

        Object[] args = joinPoint.getArgs();
        log.info("{} 接口接收参数:{}", joinPoint.getSignature().getDeclaringType().getName()
                + "." + joinPoint.getSignature().getName(), JSONUtil.toJsonStr(args));
        Object result = null;
        try {
            result = joinPoint.proceed(args);
        } catch (Throwable e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

        long endTime = System.currentTimeMillis();

        log.info(" {} 接口调用时间: {} 毫秒", joinPoint.getSignature().getDeclaringType().getName()
                + "." + joinPoint.getSignature().getName(), endTime - startTime);

        return result;
    }
}
