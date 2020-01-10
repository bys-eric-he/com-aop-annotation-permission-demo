package com.example.aop.annotation.permission.demo.service;

import com.example.aop.annotation.permission.demo.dto.UserDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();
    UserDto findByUserName(String userName);
    UserDto selectByPrimaryKey(Integer userId);
    Page<UserDto> findByNickName(String nickName);
    void save(UserDto userDto);
    String login(String userName, String password);
}
