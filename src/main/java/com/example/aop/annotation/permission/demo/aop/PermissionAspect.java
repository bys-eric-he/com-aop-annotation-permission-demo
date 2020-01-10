package com.example.aop.annotation.permission.demo.aop;

import com.example.aop.annotation.permission.demo.annotation.LoginRequired;
import com.example.aop.annotation.permission.demo.core.Result;
import com.example.aop.annotation.permission.demo.core.ResultUtil;
import com.example.aop.annotation.permission.demo.dto.UserDto;
import com.example.aop.annotation.permission.demo.jwt.JwtTokenUtil;
import com.example.aop.annotation.permission.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@Order(value = 1)
public class PermissionAspect {

    @Autowired
    private UserService userService;

    /**
     * 第一个 * 代表任意修饰符及任意返回值.
     * 第二个 * 任意包名
     * 第三个 * 代表任意方法.
     * .. 匹配任意数量的参数.
     */
    @Pointcut("execution(* com.example.aop.annotation.permission.demo.controller.*.*(..))")
    public void permissionCheck() {
    }

    @Around("permissionCheck()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod(); //获取被拦截的方法

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();

        Object result = null;

        if (isLoginRequired(method)) {
            Result<Object> loginResult = isLogin(request);
            if (loginResult.getResCode() != 200) {
                result = loginResult;
            }
        }
        if (result == null) {
            try {
                result = joinPoint.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                result = ResultUtil.failure("500", throwable.getMessage());
            }
        }
        return result;
    }

    /**
     * 判断一个方法是否需要登录
     *
     * @param method 方法
     * @return
     */
    private boolean isLoginRequired(Method method) {
        boolean result = false;
        if (method.isAnnotationPresent(LoginRequired.class)) {
            result = method.getAnnotation(LoginRequired.class).isRequired();
        }
        return result;
    }

    /**
     * 判断是否已经登录
     *
     * @param request
     * @return
     */
    private Result<Object> isLogin(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token)) {
            return ResultUtil.failure("400", "会话过期，请重新登陆!");
        }

        UserDto userDto = JwtTokenUtil.verifyToken(request);

        if (userDto == null) {
            return ResultUtil.failure("404", "用户信息不合法!");
        }

        userDto = userService.selectByPrimaryKey(userDto.getId());


        return ResultUtil.success(userDto);
    }
}
