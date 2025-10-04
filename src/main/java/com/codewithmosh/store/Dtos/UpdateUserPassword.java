package com.codewithmosh.store.Dtos;

import lombok.Data;

@Data
public class UpdateUserPassword {
    private String oldPassword;
    private String newPassword;

}
