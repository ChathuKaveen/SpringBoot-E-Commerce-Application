package com.codewithmosh.store.services;

import java.util.Date;
import org.springframework.stereotype.Service;

import com.codewithmosh.store.config.JwtConfig;
import com.codewithmosh.store.entities.User;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
@AllArgsConstructor
@Service
public class JWTService {
   private final JwtConfig jwtConfig;

    public String generateToken(User user){
        return Jwts.builder()
            .subject(user.getId().toString())
            .claim("email" , user.getEmail())
            .claim("name", user.getName())
            .claim("user", user.getRole())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 1000 * 86400))
            .signWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
            .compact();
    }

    public String refreshToken(User user){
        return Jwts.builder()
            .subject(user.getId().toString())
            .claim("email" , user.getEmail())
            .claim("name", user.getName())
            .claim("user", user.getRole())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 1000 * jwtConfig.getRefreshTokenExp()))
            .signWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
            .compact();
    }

    public boolean validateToken(String token){

            try{
                var claims = Jwts.parser()
                                .verifyWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                                .build()
                                .parseSignedClaims(token)
                                .getPayload();
                return claims.getExpiration().after(new Date());
            }catch(JwtException ex){
                return false;
            }
    }

    public Long getIdFromToken(String token){
        var claims = Jwts.parser()
                                .verifyWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
                                .build()
                                .parseSignedClaims(token)
                                .getPayload();
        return Long.valueOf(claims.getSubject());
    }
}
