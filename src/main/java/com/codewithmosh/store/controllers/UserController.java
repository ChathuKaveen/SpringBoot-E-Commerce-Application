package com.codewithmosh.store.controllers;
import com.codewithmosh.store.repositories.UserRepository;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.codewithmosh.store.Dtos.RegisterUserRequest;
import com.codewithmosh.store.Dtos.UpdateUserPassword;
import com.codewithmosh.store.Dtos.UpdateUserRequest;
import com.codewithmosh.store.Dtos.UserDto;
import com.codewithmosh.store.Mappers.UserMappper;


@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMappper userMappper;
    @GetMapping
    public Iterable<UserDto> getAllUsers(@RequestParam(required = false , defaultValue = "" , name = "sort") String sort){
        if(!Set.of("name" , "email").contains(sort))
            sort="name";
        return userRepository.findAll(Sort.by(sort))
                .stream()
                .map(user->userMappper.toDto(user))
                .toList();
        // return userRepository.findAll()
        //         .stream()
        //         .map(user->new UserDto(user.getId(), user.getName(), user.getEmail()))
        //         .toList();
    } 

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id){
        var user =  userRepository.findById(id).orElse(null);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        //var userDto = new UserDto(user.getId(), user.getName(), user.getEmail());
        return ResponseEntity.ok(userMappper.toDto(user));
    }

    @PostMapping
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserRequest request , UriComponentsBuilder uriBuilder){
        var user = userMappper.toEntity(request);
        if(userRepository.existsByEmail(request.getEmail())){
            return ResponseEntity.badRequest().body(Map.of("email" , "Already Taken"));
        }
        userRepository.save(user);

        var userDto = userMappper.toDto(user);
        var builder = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(builder).body(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable(name = "id") Long id , @RequestBody UpdateUserRequest request){
        var user = userRepository.findById(id).orElseThrow();
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        userRepository.save(user);
        return ResponseEntity.ok(userMappper.toDto(user));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") Long id){
        var user = userRepository.findById(id).orElseThrow();
        if(user == null){
            return ResponseEntity.notFound().build();
        }
       
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable(name = "id") Long id , @RequestBody UpdateUserPassword request){
        var user = userRepository.findById(id).orElseThrow();
        if(!user.getPassword().equals(request.getOldPassword())){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
       
       user.setPassword(request.getNewPassword());
       userRepository.save(user);
       return ResponseEntity.noContent().build();
    }

    
}
