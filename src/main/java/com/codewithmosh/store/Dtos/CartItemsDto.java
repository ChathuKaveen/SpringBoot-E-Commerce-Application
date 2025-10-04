package com.codewithmosh.store.Dtos;

import java.math.BigDecimal;

import lombok.Data;
@Data
public class CartItemsDto {
     private CartProductDto product;
     private int quantity;
     private BigDecimal totalPrice;
}
