package com.codewithmosh.store.services;

import java.util.Date;
import org.springframework.stereotype.Service;

import com.codewithmosh.store.config.JwtConfig;
import com.codewithmosh.store.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
@AllArgsConstructor
@Service
public class JWTService {
   private final JwtConfig jwtConfig;

    public String generateToken(User user){
        return jwtBuilder(user);
    }

    public String refreshToken(User user){
        return jwtBuilder(user);
    }

    public String jwtBuilder(User user){
        return Jwts.builder()
            .subject(user.getId().toString())
            .claim("email" , user.getEmail())
            .claim("name", user.getName())
            .claim("role", user.getRole())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 1000 * jwtConfig.getRefreshTokenExpiration()))
            .signWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
            .compact();
    }
    public Claims getClaims(String token){
         return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
    }
    public JWT parseToken(String token){
        return new JWT(getClaims(token), jwtConfig.getSecret());
    }

}
