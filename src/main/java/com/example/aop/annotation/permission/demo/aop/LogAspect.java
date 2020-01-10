package com.example.aop.annotation.permission.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Enumeration;

@Slf4j
@Aspect
@Component
@Order(value = 0)
public class LogAspect {

    ThreadLocal<Long> startTime = new ThreadLocal<Long>();

    /**
     * 第一个 * 代表任意修饰符及任意返回值.
     * 第二个 * 任意包名
     * 第三个 * 代表任意方法.
     * .. 匹配任意数量的参数.
     */
    @Pointcut("execution(* com.example.aop.annotation.permission.demo.service.*.*(..))")
    public void webLog() {
    }

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        log.info("before : " + LocalTime.now());
        startTime.set(System.currentTimeMillis());
        //接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            //记录请求内容
            log.info("URL : " + request.getRequestURL().toString());
            log.info("HTTP_METHOD : " + request.getMethod());
            log.info("IP : " + request.getRemoteAddr());
            log.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
            log.info("ARGS : " + Arrays.toString(joinPoint.getArgs()));
            //获取所有参数方法一：
            Enumeration<String> enu = request.getParameterNames();

            while (enu.hasMoreElements()) {
                String paraName = (String) enu.nextElement();
                log.info(paraName + ": " + request.getParameter(paraName));
            }
        }
    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        log.info("after : " + LocalTime.now());
        //处理完请求，返回内容
        log.info("RESPONCE : " + ret);
        // 处理完请求，返回内容
        log.info("WebLogAspect.doAfterReturning()");
        log.info("耗时（毫秒） : " + (System.currentTimeMillis() - startTime.get()));
    }
}
