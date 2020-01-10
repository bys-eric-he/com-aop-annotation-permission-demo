package com.example.aop.annotation.permission.demo.jwt;

import com.example.aop.annotation.permission.demo.dto.UserDto;
import com.example.aop.annotation.permission.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class JwtUserDetailService implements UserDetailsService {

    @Autowired
    private UserService userService;


    /**
     * 获取用户信息,然后交给spring去校验权限
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //获取用户信息
        UserDto userDto = userService.findByUserName(username);
        if(userDto == null){
            throw new UsernameNotFoundException("用户名不存在");
        }
        JwtUserDetails jwtUserDetails = new JwtUserDetails(userDto);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        jwtUserDetails.setAuthorities(authorities);
        log.info("authorities size:{}", authorities.size());

        return jwtUserDetails;
    }

}