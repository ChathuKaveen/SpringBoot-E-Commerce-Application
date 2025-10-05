package com.codewithmosh.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            .sessionManagement(c->c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(c->c.disable())
            .authorizeHttpRequests(c->c
                .requestMatchers("/carts/**").permitAll()
                .requestMatchers(HttpMethod.POST,"/users").permitAll()
                .requestMatchers(HttpMethod.POST,"/auth/login").permitAll()
                .anyRequest().authenticated());
        return http.build();

    }
}
