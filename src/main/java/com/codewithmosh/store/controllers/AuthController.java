package com.codewithmosh.store.controllers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codewithmosh.store.Dtos.LoginRequest;

import com.codewithmosh.store.repositories.UserRepository;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest requst){
        var user = userRepository.findByEmail(requst.getEmail()).orElse(null);
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(!user.getPassword().equals(requst.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().build();
    }
}
