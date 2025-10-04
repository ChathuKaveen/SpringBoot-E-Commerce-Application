package com.codewithmosh.store.Dtos;

import java.math.BigDecimal;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductDto {
   
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Byte categoryId;
}
