package com.codewithmosh.store.Dtos;

import lombok.Data;

@Data
public class CheckoutResponse {
    private String url;
    
        public CheckoutResponse(Long id , String url) {
            this.orderId = id;
            this.url = url;
    }

    private Long orderId;
}
