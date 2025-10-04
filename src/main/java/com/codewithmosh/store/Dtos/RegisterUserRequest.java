package com.codewithmosh.store.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {
    @NotNull
    @Size(max = 255 , message = "name musnt not be >than 255")
    private String name;

    @NotBlank(message = "cant be blank")
    @Email(message = "must be valid")
    private String email;

    
    private String password;
}
