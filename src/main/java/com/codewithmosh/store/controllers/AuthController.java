package com.codewithmosh.store.controllers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codewithmosh.store.Dtos.JwtResponseDto;
import com.codewithmosh.store.Dtos.LoginRequest;
import com.codewithmosh.store.services.JWTService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody LoginRequest requst){
        // var user = userRepository.findByEmail(requst.getEmail()).orElse(null);
        // if(user == null){
        //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        // }

        // if(!user.getPassword().equals(requst.getPassword())){
        //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        // }

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(requst.getEmail(), requst.getPassword())
        );
        var token = jwtService.generateToken(requst.getEmail());
        return ResponseEntity.ok(new JwtResponseDto(token));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleException(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
