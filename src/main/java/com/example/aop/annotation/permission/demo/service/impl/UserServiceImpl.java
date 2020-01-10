package com.example.aop.annotation.permission.demo.service.impl;

import com.example.aop.annotation.permission.demo.dto.UserDto;
import com.example.aop.annotation.permission.demo.entity.User;
import com.example.aop.annotation.permission.demo.jwt.JwtTokenUtil;
import com.example.aop.annotation.permission.demo.repository.UserRepository;
import com.example.aop.annotation.permission.demo.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private UserDto convert(User user) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        return userDto;
    }

    private User convert(UserDto userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        return user;
    }

    @Override
    public void save(UserDto userDto) {
        userDto.setPassWord(passwordEncoder.encode(userDto.getPassWord()));
        userRepository.save(convert(userDto));
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();
        users.forEach(user -> userDtos.add(convert(user)));
        return userDtos;
    }

    @Override
    public UserDto findByUserName(String userName) {
        UserDto userDto = new UserDto();
        User user = userRepository.findByUserName(userName);
        BeanUtils.copyProperties(user, userDto);
        return userDto;
    }

    @Override
    public Page<UserDto> findByNickName(String nickName) {
        int page = 0;
        int size = 1;
        Pageable pageable = PageRequest.of(page, size, Sort.by(new Sort.Order(Sort.Direction.DESC, "id")));
        Page<User> all = userRepository.findByNickName(nickName, pageable);
        List<UserDto> userDtos = new ArrayList<>();
        all.getContent().forEach(user -> userDtos.add(convert(user)));
        return new PageImpl<>(userDtos);
    }

    @Override
    public UserDto selectByPrimaryKey(Integer userId) {
        User user = userRepository.findById(userId);
        return convert(user);
    }

    @Override
    public String login(String userName, String password) {
        User user = userRepository.findByUserName(userName);
        if (user != null) {
            if (!passwordEncoder.matches(password, user.getPassWord())) {
                throw new BadCredentialsException("密码错误！");
            }
            return JwtTokenUtil.generateToken(user.getId().toString(), user.getUserName());
        }
        throw new BadCredentialsException("用户不存在！");
    }
}
