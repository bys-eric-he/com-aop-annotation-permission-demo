package com.example.aop.annotation.permission.demo.filter;

import com.example.aop.annotation.permission.demo.dto.UserDto;
import com.example.aop.annotation.permission.demo.jwt.JwtTokenUtil;
import com.example.aop.annotation.permission.demo.jwt.JwtUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailService jwtUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        UserDto userDto = JwtTokenUtil.verifyToken(request);

        if (userDto!=null && userDto.getUserName() != null) {
            UserDetails userDetails = jwtUserDetailService.loadUserByUsername(userDto.getUserName());

            //必须token解析的时间戳和session保存的一致
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            //如果有accessToken的请求头，取出token，解析token，解析成功说明token正确，将解析出来的用户信息放到SpringSecurity的上下文中
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }

         /*
        如果请求头中没有Authorization信息则直接放行了。
        前端发起请求的时候将token放在请求头中，在过滤器中对请求头进行解析。
        如果有accessToken的请求头（可以自已定义名字），取出token，解析token，解析成功说明token正确，将解析出来的用户信息放到SpringSecurity的上下文中
        如果有accessToken的请求头，解析token失败（无效token，或者过期失效），取不到用户信息，放行
        没有accessToken的请求头，放行，这里可能有人会疑惑，为什么token失效都要放行呢？
        这是因为SpringSecurity会自己去做登录的认证和权限的校验，靠的就是我们放在SpringSecurity上下文中的SecurityContextHolder.getContext().setAuthentication(authentication);
        没有拿到authentication，放行了，SpringSecurity还是会走到认证和校验，这个时候就会发现没有登录没有权限，就会被AuthenticationEntryPoint实现类拦截。
        **/
        chain.doFilter(request, response);
    }
}
