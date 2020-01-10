package com.example.aop.annotation.permission.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private Integer id;

    private String userName;

    private String passWord;

    private String email;

    private String nickName;

    private String registerTime;
}
