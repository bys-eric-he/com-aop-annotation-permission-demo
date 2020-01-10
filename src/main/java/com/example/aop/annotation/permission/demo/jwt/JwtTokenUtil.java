package com.example.aop.annotation.permission.demo.jwt;

import com.example.aop.annotation.permission.demo.dto.UserDto;
import com.example.aop.annotation.permission.demo.exception.TokenValidationException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Slf4j
public class JwtTokenUtil {
    public static final long EXPIRATION_TIME = 2592_000_000L; // 有效期30天
    public static final String SECRET = "2020@eric_1988";
    public static final String HEADER = "Authorization";
    public static final String USER_ID = "userId";
    public static final String USER_NAME = "userName";

    /**
     * 根据userId userName生成token
     * @param userId
     * @param userName
     * @return
     */
    public static String generateToken(String userId, String userName) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(USER_ID, userId);
        map.put(USER_NAME, userName);
        return Jwts.builder()
                .setClaims(map)
                .setIssuer("eric.he")
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    /**
     * 验证token
     * @param request
     * @return 验证通过返回userId
     */
    public static UserDto verifyToken(HttpServletRequest request) {
        String token = request.getHeader(HEADER);
        if (token != null) {
            try {
                Map<String, Object> body = Jwts.parser()
                        .setSigningKey(SECRET)
                        .parseClaimsJws(token)
                        .getBody();
                UserDto userDto = new UserDto();
                for (Entry<String, Object> entry : body.entrySet()) {
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    if (key.toString().equals(USER_ID)) {
                        userDto.setId(Integer.valueOf(value.toString()));
                    }
                    if (key.toString().equals(USER_NAME)) {
                        userDto.setUserName(value.toString());
                    }
                }
                return userDto;
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new TokenValidationException("Authorization");
            }
        } else {
            return null;
        }
    }
}
