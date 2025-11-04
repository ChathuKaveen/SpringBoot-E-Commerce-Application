package com.codewithmosh.store.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codewithmosh.store.Dtos.CheckoutRequest;
import com.codewithmosh.store.Dtos.CheckoutResponse;
import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.entities.OrderStatus;
import com.codewithmosh.store.entities.Order_items;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.OrderRepository;
import com.codewithmosh.store.services.AuthService;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
@AllArgsConstructor
@RequestMapping("/checkout")
@RestController
public class CheckoutController {
    private final CartRepository cartRepository;
    private final AuthService authService;
    private final OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<?> checkout(@Valid @RequestBody CheckoutRequest request){
        var cart = cartRepository.findById(request.getCartId()).orElse(null);
        if(cart == null){
            return ResponseEntity.badRequest().body(Map.of("error", "cart Not Found"));
        }
        if(cart.getCartItems() == null || cart.getCartItems().isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("error", "Cart is empty"));
        }
        var order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(cart.getTotalPrice());
        order.setCustomer(authService.getCurrentUser());

        cart.getCartItems().forEach(item ->{
            var order_items = new Order_items();
            order_items.setOrder(order);
            order_items.setProduct(item.getProduct());
            order_items.setQuantity(item.getQuantity());
            order_items.setTotal_price(item.getTotalPrice());
            order_items.setUnitPrice(item.getProduct().getPrice());
            order.getItems().add(order_items);
        });

        orderRepository.save(order);
        cart.getCartItems().clear();
        return ResponseEntity.ok(new CheckoutResponse(order.getId()));
        
    }
}
