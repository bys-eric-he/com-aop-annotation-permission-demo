package com.example.aop.annotation.permission.demo.config;


import com.example.aop.annotation.permission.demo.config.properties.IgnoreUrlsProperties;
import com.example.aop.annotation.permission.demo.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * 将处理器和我们的Jwt拦截器添加到Spring Security的配置中
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private IgnoreUrlsProperties ignoreUrlsProperties;

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * antMatchers: ant的通配符规则
     * ?	匹配任何单字符
     * *	匹配0或者任意数量的字符，不包含"/"
     * **	匹配0或者更多的目录，包含"/"
     * 要用Security的密码加盐算法必须要写这部分
     * authorize：授权
     * authenticated：认证
     * authorizeRequests所有security全注解配置实现的开端，表示说明开始需要的权限
     * 需要的权限分两部分：第一部分是拦截的路径，第二部分是访问该路径需要的权限
     * antMatchers：拦截路径"/**所有路径"，permitAll()：任何权限都可以直接通行
     * anyRequest:任何的请求，authenticated认证后才可以访问
     * .and().csrf().disable();固定写法，表示使csrf（一种网络攻击技术）拦截失效
     * 测试用资源，需要验证了的用户才能访问
     * 其他都放行了
     * .anyRequest().permitAll()
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .headers()
                .frameOptions().disable();
        //定义不需要保护的URL
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = httpSecurity
                .authorizeRequests();

        //这里表示不需要权限校验
        Arrays.asList(ignoreUrlsProperties.getUrls().split(",")).forEach(url -> registry.antMatchers(url).permitAll());

        //使用jwt的Authentication,来解析过来的请求是否有token
        httpSecurity
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        //这里表示不需要权限校验
        //registry.antMatchers("/swagger-ui.html").permitAll();
        //registry.antMatchers("/swagger-resources/**").permitAll();
        //registry.antMatchers("/swagger/**").permitAll();
        //registry.antMatchers("/**/v2/api-docs").permitAll();
        //registry.antMatchers("/**/*.js").permitAll();
        //registry.antMatchers("/**/*.css").permitAll();
        //registry.antMatchers("/**/*.png").permitAll();
        //registry.antMatchers("/**/*.ico").permitAll();
        //registry.antMatchers("/webjars/springfox-swagger-ui/**").permitAll();
        //registry.antMatchers("/**/user/login/**").permitAll();
        //registry.antMatchers("/**/user/register").permitAll();

        httpSecurity
                //对请求的授权
                .authorizeRequests()
                .anyRequest()//任何请求
                .authenticated()//需要认证
                .and()
                .formLogin()
                .permitAll()
                .permitAll()

                .and()
                .logout()

                .and()
                .csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //权限控制需要忽略所有静态资源，不然登录页面未登录状态无法加载css等静态资源
        web.ignoring().mvcMatchers("/static/**", "/img/**");
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

}
