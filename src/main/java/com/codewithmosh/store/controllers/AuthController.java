package com.codewithmosh.store.controllers;
import org.springframework.http.HttpHeaders;
import java.time.Duration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.codewithmosh.store.Dtos.JwtResponseDto;
import com.codewithmosh.store.Dtos.LoginRequest;
import com.codewithmosh.store.Dtos.UserDto;
import com.codewithmosh.store.Mappers.UserMappper;
import com.codewithmosh.store.repositories.UserRepository;
import com.codewithmosh.store.services.JWTService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final UserMappper userMappper;
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody LoginRequest requst , HttpServletResponse response){
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
        var userIdByToken = userRepository.findByEmail(requst.getEmail()).orElseThrow();
        var token = jwtService.generateToken(userIdByToken);
        var refreshToken = jwtService.refreshToken(userIdByToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
        .httpOnly(true)
        .secure(true)
        .path("/auth/refresh")
        .maxAge(Duration.ofDays(7))
        .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new JwtResponseDto(token));
    }

    //No longer needed
    // @PostMapping("/validate")
    // public boolean validate(@RequestHeader("Authorization") String authHeader){
    //     var token = authHeader.replace("Bearer ", "");
    //     return jwtService.validateToken(token);
    // }
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDto> refresh(@CookieValue(value = "refreshToken") String refreshToken){
        var token = jwtService.parseToken(refreshToken);
        if(!token.validateToken()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var userId =  token.getIdFromToken();
        var user = userRepository.findById(userId).orElseThrow();
        var userToken = jwtService.generateToken(user);

        return ResponseEntity.ok(new JwtResponseDto(userToken));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleException(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(){
        var authenticate = SecurityContextHolder.getContext().getAuthentication();
        var id = (Long)authenticate.getPrincipal();
        var user = userRepository.findById(id).orElse(null);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        var userDto = userMappper.toDto(user);
        return ResponseEntity.ok(userDto);
    }

}
