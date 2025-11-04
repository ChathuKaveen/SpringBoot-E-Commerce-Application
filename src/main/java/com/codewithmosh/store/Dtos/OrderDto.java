package com.codewithmosh.store.Dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class OrderDto {
    private Long id;
    private String status;
    private LocalDateTime created_at;
    private List<OrderItemDto> items;
    private BigDecimal totalPrice;
}
