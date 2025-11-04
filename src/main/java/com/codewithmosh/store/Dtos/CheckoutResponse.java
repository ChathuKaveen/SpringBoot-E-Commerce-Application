package com.codewithmosh.store.Dtos;

import lombok.Data;

@Data
public class CheckoutResponse {
    public CheckoutResponse(Long id) {
        this.orderId = id;
    }

    private Long orderId;
}
