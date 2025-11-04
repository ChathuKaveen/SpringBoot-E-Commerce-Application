package com.codewithmosh.store.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.codewithmosh.store.entities.User;
import com.codewithmosh.store.repositories.UserRepository;

import lombok.AllArgsConstructor;
@AllArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    public User getCurrentUser(){
        var authenticate = SecurityContextHolder.getContext().getAuthentication();
        var id = (Long)authenticate.getPrincipal();
        return userRepository.findById(id).orElse(null);
    }
}
