package com.codewithmosh.store.Dtos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class RegisterProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Byte categoryId;


}
