package com.example.aop.annotation.permission.demo.controller;

import com.example.aop.annotation.permission.demo.annotation.LoginRequired;
import com.example.aop.annotation.permission.demo.core.Result;
import com.example.aop.annotation.permission.demo.core.ResultUtil;
import com.example.aop.annotation.permission.demo.dto.UserDto;
import com.example.aop.annotation.permission.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/user")
public class UserContoller {
    @Autowired
    private UserService userService;


    @PostMapping(value="/register")
    @ResponseBody
    public Result<UserDto> register(@RequestBody UserDto userDto) {
        userService.save(userDto);
        return ResultUtil.success(userDto);
    }

    @GetMapping(value = "/getAll")
    @ResponseBody
    @LoginRequired(isRequired = true)
    public Result<List<UserDto>> findAll(){
        List<UserDto> userDtos = userService.findAll();
        return ResultUtil.success(userDtos);
    }

    @GetMapping(value = "/login/{userName}/{password}")
    public Result<String> login(@PathVariable(value = "userName") String userName, @PathVariable(value = "password") String password){
        String token = userService.login(userName,password);
        return ResultUtil.success(token);
    }
}
