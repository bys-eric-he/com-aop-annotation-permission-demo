package com.example.aop.annotation.permission.demo.exception;

public class TokenValidationException extends RuntimeException{

    private String errCode;

    private String errMsg;

    private boolean isInnerError;

    public TokenValidationException(){
        this.isInnerError=false;
    }

    public TokenValidationException(String errCode){
        this.errCode =errCode;
        this.isInnerError = false;
    }

    public TokenValidationException(String errCode,boolean isInnerError){
        this.errCode =errCode;
        this.isInnerError = isInnerError;
    }

    public TokenValidationException(String errCode,String errMsg){
        this.errCode =errCode;
        this.errMsg = errMsg;
        this.isInnerError = false;
    }

    public TokenValidationException(String errCode,String errMsg,boolean isInnerError){
        this.errCode =errCode;
        this.errMsg = errMsg;
        this.isInnerError = isInnerError;
    }
}