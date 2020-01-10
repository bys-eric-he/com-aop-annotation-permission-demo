package com.example.aop.annotation.permission.demo.exception;

import com.example.aop.annotation.permission.demo.core.Result;
import com.example.aop.annotation.permission.demo.core.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice {
    @ResponseBody
    @ExceptionHandler(TokenValidationException.class)
    public Result<String> tokenValidationExceptionHandler(TokenValidationException exception) {
        log.error(exception.getMessage());
        return ResultUtil.failure("", exception.getMessage(), exception.toString());
    }

    @ResponseBody
    @ExceptionHandler(ServiceException.class)
    public Result<String> serviceExceptionHandler(ServiceException exception) {
        log.error(exception.getMessage());
        return ResultUtil.failure("", exception.getMessage(), exception.toString());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Result<String> exceptionHandler(Exception exception) {
        log.error(exception.getMessage());
        return ResultUtil.failure("", exception.getMessage(), exception.toString());
    }
}
