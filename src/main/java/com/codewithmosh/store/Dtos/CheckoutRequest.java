package com.codewithmosh.store.Dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotNull(message = "cart id cannot be null")
    private UUID cartId;

}
