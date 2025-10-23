package com.codewithmosh.store.services;

import java.util.Date;

import javax.crypto.SecretKey;

import com.codewithmosh.store.entities.Role;

import io.jsonwebtoken.Claims;

public class JWT {
    private final Claims claims;
    private final String secretKey;

    public JWT(Claims claims , String secretKey){
        this.claims = claims;
        this.secretKey = secretKey;
    }

    public boolean validateToken(){
        return claims.getExpiration().after(new Date());
    }

    public Long getIdFromToken(){
        return Long.valueOf(claims.getSubject());
    }

    public Role getRoleFromToken(){
        return Role.valueOf(claims.get("role" , String.class));
    }
}
